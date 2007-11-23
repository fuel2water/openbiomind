package classification.local;

import classification.*;
import classification.evolution.snps.PatternStrengthClassifier;
import dataset.Dataset;

public class SNPLocalFactory implements TrainerFactory{
    
       private Dataset dataset;
    
       public Trainer makeTrainer(){
              return new PatternStrengthClassifier(dataset.getFeatures());
       }
       
       public void setDataset(Dataset dataset){
              this.dataset=dataset;
       }
}