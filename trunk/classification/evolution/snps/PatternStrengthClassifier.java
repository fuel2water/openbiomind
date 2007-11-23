package classification.evolution.snps;

import java.io.*;
import java.util.*;

import classification.*;
import classification.evolution.*;
import dataset.Dataset;
import snps.*;
import util.Randomizer;

/**
 * Inplements a pattern-strength classifier for SNPs.
 * @author Lucio
 *
 */
public class PatternStrengthClassifier extends Evolvable implements Trainer{
    
      private List<String> features;
      private boolean[] genome;
    
      /**
       * Constructs a random classifier.
       * @param features
       */
      public PatternStrengthClassifier(List<String> features){
             this.features=features;
             genome=new boolean[features.size()];
             /*for (int i=0;i<INIT_SIZE;i++){
                 genome[Randomizer.getInstance().natural(genome.length)]=true;
             }*/
             for (int i=0;i<genome.length;i++){
                 genome[i]=Randomizer.getInstance().logic();
                 //genome[i]=Randomizer.getInstance().natural(10)==0;
             }
      }
      
      /**
       * Constructs an "offspring" classifier using information from two parent classifiers.
       * @param dad
       * @param mom
       */
      public PatternStrengthClassifier(PatternStrengthClassifier dad,PatternStrengthClassifier mom){
             this.features=dad.getFeatures();
             this.genome=new boolean[this.features.size()];
             
             int crossPoint=Randomizer.getInstance().natural(this.genome.length);
             
             for (int i=0;i<genome.length;i++){
                 if (i<crossPoint){
                    genome[i]=mom.get(i);
                 }
                 else {
                      genome[i]=dad.get(i);
                 }
             }
             
             int mutationPoint=Randomizer.getInstance().natural(genome.length);
             
             genome[mutationPoint]=!genome[mutationPoint];
      }
      
      public Ensemble train(Dataset dataset){
          
             boolean improved=true;
             
             while (improved){
                   improved=false;
                   
                   float acc=(new ConfusionMatrix(this,dataset.getEntities())).accuracy();
                   
                   float before=acc;
                   int sizeBefore=this.size();
                   
                   for (int i=0;i<this.genome.length;i++){
                       
                       genome[i]=!genome[i];
                       
                       acc=(new ConfusionMatrix(this,dataset.getEntities())).accuracy();
                       if (acc<before){
                          genome[i]=!genome[i];
                          continue;
                       }
                       if ((acc==before)&&(this.size()>sizeBefore)){
                          genome[i]=!genome[i];
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
          
             for (int i=0;i<genome.length;i++){
                 if (!genome[i]){
                    continue;
                 }
                 if (isHomo(sample,features.get(i))){
                    sum++;
                    //sum+=HOMO_WEIGHT;
                 }
                 if (isHetero(sample,features.get(i))){
                    sum--;
                    //sum+=HETERO_WEIGHT;
                 }
             }
             return sum>0;
             //return sumf>0.0f;
             //return sum>threshold;
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
          
             int output=0;
             
             for (int i=0;i<genome.length;i++){
                 if (genome[i]){
                    output+=1;
                 }
             }
             return output;
      }
      
      /**
       * Returns the set of SNPs effectively used for classification.
       */
      public Set<String> featureSet(){
          
              Set<String> output=new HashSet<String>();
              
              for (int i=0;i<genome.length;i++){
                  if (genome[i]){
                     output.add(features.get(i));
                  }
              }
              return output;
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
          
             if (alien.getFeatures().size()!=this.features.size()){
                return false;
             }
             for (int i=0;i<genome.length;i++){
                 if (genome[i]!=alien.get(i)){
                    return false;
                 }
                 if (!alien.getFeatures().get(i).equals(features.get(i))){
                    return false;
                 }
             }
             return true;
      }
      
      public boolean get(int index){
             return genome[index];
      }
      
      public List<String> getFeatures(){
             return this.features;
      }
      
}