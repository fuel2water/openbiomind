package classification.evolution.snps;

import java.io.*;
import java.util.*;

import classification.*;
import classification.evolution.*;
import dataset.Dataset;
import snps.*;
import util.*;

/**
 * Inplements a pattern-strength classifier for SNPs.
 * @author Lucio
 *
 */
public class PatternStrengthClassifier extends Evolvable implements Trainer{
    
       private static int MAX_SIZE=100;
    
       private List<String> features;
       private Set<String> snps=new HashSet<String>();
    
      /**
       * Constructs a random classifier.
       * @param features
       */
      public PatternStrengthClassifier(List<String> features){
             this.features=features;
             
             int maxsize=MAX_SIZE;
             
             if (features.size()/2<maxsize){
                maxsize=features.size()/2;
             }
             for (int i=0;i<maxsize;i++){
                 snps.add(features.get(Randomizer.getInstance().natural(features.size())));
             }
      }
      
      /**
       * Constructs an "offspring" classifier using information from two parent classifiers.
       * @param dad
       * @param mom
       */
      public PatternStrengthClassifier(PatternStrengthClassifier dad,PatternStrengthClassifier mom){
             this.features=dad.getFeatures();
             
             int maxsize=MAX_SIZE;
             
             if (features.size()/2<maxsize){
                maxsize=features.size()/2;
             }
             //int crossPoint=Randomizer.getInstance().natural(features.size());
             
             List<String> dadsnps=new ArrayList<String>(dad.featureSet());
             List<String> momsnps=new ArrayList<String>(mom.featureSet());
             
             for (int i=0;i<maxsize;i++){
                 if (Randomizer.getInstance().logic()){
                    snps.add(momsnps.get(Randomizer.getInstance().natural(momsnps.size())));
                 }
                 else {
                    snps.add(dadsnps.get(Randomizer.getInstance().natural(dadsnps.size())));
                 }
             }
             /*for (int i=0;i<features.size();i++){
                 if (i<crossPoint){
                    if (mom.get(i)){
                       snps.add(features.get(i));
                    }
                 }
                 else {
                    if (dad.get(i)){
                       snps.add(features.get(i));
                    }
                 }
             }*/
             snps.add(features.get(Randomizer.getInstance().natural(features.size())));
             //invert(Randomizer.getInstance().natural(features.size()));
      }
      
      /**
       * Constructs a classifier from a given textual description.
       * @param modelBlock
       */
      public PatternStrengthClassifier(LineTagger modelBlock){
             //System.out.println(modelBlock.toString());
             //System.exit(0);
             for (int i=1;i<modelBlock.size();i++){
                 if (modelBlock.get(i).trim()==""){
                    continue;
                 }
                 snps.add(modelBlock.get(i));
             }
      }
      
      private void invert(int index){
             if (get(index)){
                snps.remove(features.get(index));
             }
             else {
                  snps.add(features.get(index));
             }
      }
      
      private void invert(String snp){
             if (snps.contains(snp)){
                snps.remove(snp);
             }
             else {
                  snps.add(snp);
             }
      }
      
      public Ensemble train(Dataset dataset){
          
             boolean improved=true;
             List<String> flist=new ArrayList<String>(snps);
             
             while (improved){
                   improved=false;
                   
                   float acc=(new ConfusionMatrix(this,dataset.getEntities())).accuracy();
                   
                   float before=acc;
                   int sizeBefore=this.size();
                   
                   for (int i=0;i<flist.size();i++){
                       
                       invert(flist.get(i));
                       
                       acc=(new ConfusionMatrix(this,dataset.getEntities())).accuracy();
                       if (acc<before){
                          invert(flist.get(i));
                          continue;
                       }
                       if ((acc==before)&&(this.size()>sizeBefore)){
                          invert(flist.get(i));
                          continue;
                       }
                       before=acc;
                       sizeBefore=this.size();
                       improved=true;
                       System.out.println(acc+"\t"+this.size());
                   }
             }
             
             Ensemble output=new Ensemble();
             
             output.add(this);
             return output;
      }
      
      public boolean evaluate(Map<String,Float> sample){
          
             int sum=0;
          
             for (String snp:snps){
                 if (isHomo(sample,snp)){
                    sum++;
                 }
                 if (isHetero(sample,snp)){
                    sum--;
                 }
             }
             return sum>0;
      }
      
      /**
       * Verifies if the SNP value corresponds to the heterozygosis constant.
       * @param sample
       * @param feature
       * @return
       */
      private static boolean isHetero(Map<String,Float> sample,String feature){
              return sample.get(feature)==SNPFeature.AB_VALUE;
      }
      
      /**
       * Writes a textual description of this classifier at the given writer.
       */
      public void write(Writer writer) throws IOException{
             for (String snp:this.featureSet()){
                 writer.write(snp+"\n");
             }
      }
      
      /**
       * Returns the number of SNPs effectively used for classification.
       */
      public int size(){
             return snps.size();
      }
      
      /**
       * Returns the set of SNPs effectively used for classification.
       */
      public Set<String> featureSet(){
             return snps;
      }
      
      /**
       * Verifies if the SNP value corresponds to a homozygosis-mapped value.
       * @param sample
       * @param feature
       * @return
       */
      private static boolean isHomo(Map<String,Float> sample,String feature){
              return (sample.get(feature)!=SNPFeature.AB_VALUE)&&(sample.get(feature)!=SNPFeature.BLANK_VALUE);
      }
      
      public boolean equals(Classifier other){
          
             PatternStrengthClassifier alien=(PatternStrengthClassifier)other;
             Set<String> alienSNPs=alien.featureSet();
          
             if (alienSNPs.size()!=snps.size()){
                return false;
             }
             for (String snp:snps){
                 if (!alienSNPs.contains(snp)){
                    return false;
                 }
             }
             return true;
      }
      
      public boolean get(int index){
             return snps.contains(features.get(index));
      }
      
      public List<String> getFeatures(){
             return this.features;
      }
      
}