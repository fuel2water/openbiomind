package classification.evolution.snps;

import java.io.*;
import java.util.*;

import classification.Classifier;
import classification.evolution.*;
import snps.SNPFeature;
import util.Randomizer;

class LinearSNPClassifier extends Evolvable{
    
      private List<Set<Float>> genome=new ArrayList<Set<Float>>();
      private List<String> features;
      
      private static float[] possibles={SNPFeature.AA_VALUE,SNPFeature.AB_VALUE,SNPFeature.BB_VALUE};

      private static Set<Float> makeSet(){
          
              Set<Float> output=new HashSet<Float>();
              
              for (int j=0;j<possibles.length;j++){
                  if (Randomizer.getInstance().logic()){
                     output.add(possibles[j]);
                  }
              }
              if (output.size()==3){
                 output=new HashSet<Float>();
              }
              return output;
      }
      
      public LinearSNPClassifier(List<String> features){
             this.features=features;
             for (int i=0;i<features.size();i++){
                 genome.add(makeSet());
             }
      }
      
      public LinearSNPClassifier(LinearSNPClassifier dad,LinearSNPClassifier mom){
             this.features=dad.getFeatures();
             
             int crosspoint=Randomizer.getInstance().natural(this.features.size());
             
             for (int i=0;i<features.size();i++){
                 if (i<crosspoint){
                    genome.add(mom.getGene(i));
                 }
                 else {
                    genome.add(dad.getGene(i));
                 }
             }
             
             genome.set(Randomizer.getInstance().natural(this.features.size()),makeSet());
      }
      
      public boolean evaluate(Map<String,Float> sample){
          
             int sum=0;
          
             for (int i=0;i<features.size();i++){
                 if (genome.get(i).size()==0){
                    continue;
                 }
                 if (genome.get(i).contains(sample.get(features.get(i)))){
                    sum++;
                 }
                 else {
                      sum--;
                 }
             }
             return sum>0;
      }
      
      public List<String> getFeatures(){
             return this.features;
      }
 
      public int size(){
          
             int output=0;
          
             for (int i=0;i<genome.size();i++){
                 if (genome.get(i).size()>0){
                    output++;
                 }
             }
             return output;
      }
      
      public void write(Writer writer) throws IOException{
             for (int i=0;i<features.size();i++){
                 if (genome.get(i).size()==0){
                    continue;
                 }
                 writer.write(features.get(i)+"\t"+genome.get(i).toString()+"\n");
             }
      }
      
      public Set<String> featureSet(){
          
             Set<String> output=new HashSet<String>();
             
             for (int i=0;i<genome.size();i++){
                 if (genome.get(i).size()>0){
                    output.add(features.get(i));
                 }
             }
             return output;
      }
      
      public boolean equals(Classifier other){
          
             LinearSNPClassifier alien=(LinearSNPClassifier)other;
          
             for (int i=0;i<features.size();i++){
                 if (!genome.get(i).equals(alien.getGene(i))){
                    return false;
                 }
             }
             return true;
      }
      
      public Set<Float> getGene(int index){
             return genome.get(index);
      }
}