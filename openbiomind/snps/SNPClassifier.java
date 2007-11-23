package snps;

import java.io.*;
import java.util.*;

import classification.ConfusionMatrix;
import util.Randomizer;

/**
 * Simple pattern-strength classifier for SNPs.
 * @author Lucio
 *
 */
public class SNPClassifier{
    
      private List<String> features;
      private boolean[] genome;
      private float fitness;
    
      /**
       * Constructs a random classifier.
       * @param features
       */
      public SNPClassifier(List<String> features){
             this.features=features;
             genome=new boolean[features.size()];
             /*for (int i=0;i<INIT_SIZE;i++){
                 genome[Randomizer.getInstance().natural(genome.length)]=true;
             }*/
             for (int i=0;i<genome.length;i++){
                 genome[i]=Randomizer.getInstance().logic();
             }
      }
      
      /**
       * Constructs an "offspring" classifier using information from two parent classifiers.
       * @param dad
       * @param mom
       */
      public SNPClassifier(SNPClassifier dad,SNPClassifier mom){
             this.features=dad.getFeatures();
             this.genome=new boolean[this.features.size()];
             
             int crossPoint=Randomizer.getInstance().natural(this.genome.length);
             int fromMom=0,fromDad=0;
             
             for (int i=0;i<genome.length;i++){
                 if (i<crossPoint){
                    genome[i]=mom.get(i);
                    if (genome[i]){
                       fromMom++;
                    }
                 }
                 else {
                      genome[i]=dad.get(i);
                      if (genome[i]){
                         fromDad++;
                      }
                 }
             }
             
             int mutationPoint=Randomizer.getInstance().natural(genome.length);
             
             genome[mutationPoint]=!genome[mutationPoint];
      }
      
      public float getFitness(){
             return fitness;
      }
      
      public void computeFitness(List<SNPSample> samples){
             this.fitness=this.evaluate(samples).accuracy();
      }
      
      public boolean evaluate(SNPSample sample){
          
             int sum=0;
          
             for (int i=0;i<genome.length;i++){
                 if (!genome[i]){
                    continue;
                 }
                 if (sample.isHomo(features.get(i))){
                    sum++;
                    //sum+=HOMO_WEIGHT;
                 }
                 if (sample.isHetero(features.get(i))){
                    sum--;
                    //sum+=HETERO_WEIGHT;
                 }
             }
             return sum>0;
             //return sum>threshold;
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
      
      public ConfusionMatrix evaluate(List<SNPSample> samples){
          
             ConfusionMatrix output=new ConfusionMatrix();
             
             for (SNPSample sample:samples){
                 output.account(sample.getExpected(),this.evaluate(sample));
             }
             return output;
      }
      
      public boolean get(int index){
             return genome[index];
      }
      
      public List<String> getFeatures(){
             return this.features;
      }
      
      public String toString(){
          
             StringBuffer buffer=new StringBuffer();
             
             for (int i=0;i<genome.length;i++){
                 if (genome[i]){
                    buffer.append(" "+features.get(i));
                 }
             }
             return buffer.toString().trim();
      }
      
      public SNPClassifier(SNPDataset dataset){
             this(dataset.listSNPs());
             
             boolean improved=true;
             
             while (improved){
                   improved=false;
                   this.computeFitness(dataset.getSamples());
                   
                   float before=this.fitness;
                   int sizeBefore=this.size();
                   
                   for (int i=0;i<this.genome.length;i++){
                       
                       genome[i]=!genome[i];
                       
                       this.computeFitness(dataset.getSamples());
                       if (this.fitness<before){
                          genome[i]=!genome[i];
                          continue;
                       }
                       if ((this.fitness==before)&&(this.size()>sizeBefore)){
                          genome[i]=!genome[i];
                          continue;
                       }
                       before=this.fitness;
                       sizeBefore=this.size();
                       improved=true;
                       System.out.println(this.fitness+"\t"+this.size());
                   }
             }
      }
      
      public static void main(String[] args){
          
           if (args.length!=2){
              System.out.println("Usage: java snps.SNPClassifier <dataset> <target category>");
              return;
           }
        
        
           String fileName=args[0];
           String targetCategory=args[1];
           SNPDataset dataset=null;
           
           try {
               
               FileReader reader=new FileReader(fileName);
               
               dataset=new SNPDataset(reader,targetCategory);
               reader.close();
           }
           catch (IOException e){
                 System.out.println("Error loading dataset "+fileName);
           }
           //dataset.shuffle();
           
           SNPClassifier classifier=new SNPClassifier(dataset);
      }
}