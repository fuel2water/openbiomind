package classification.evolution.snps;

import java.util.*;

import classification.evolution.*;
import dataset.*;

class LinearSNPClassifierFactory implements EvolvableFactory{
 
      private List<String> features;
 
      public LinearSNPClassifierFactory(Dataset dataset){
             features=dataset.getFeatures();
      }
      
      public Evolvable makeEvolvable(){
             return new LinearSNPClassifier(features);
      }
      
      public Evolvable makeEvolvable(Evolvable dad,Evolvable mom){
             return new LinearSNPClassifier((LinearSNPClassifier)dad,(LinearSNPClassifier)mom);
      }
}