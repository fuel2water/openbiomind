package classification.evolution.snps;

import java.util.List;

import classification.evolution.*;

class PatternStrengthClassifierFactory implements EvolvableFactory{
 
      private List<String> features;
 
      public PatternStrengthClassifierFactory(List<String> features){
             this.features=features;
      }
      
      public Evolvable makeEvolvable(){
             return new PatternStrengthClassifier(features);
      }
      
      public Evolvable makeEvolvable(Evolvable dad,Evolvable mom){
             return new PatternStrengthClassifier((PatternStrengthClassifier)dad,(PatternStrengthClassifier)mom);
      }
}