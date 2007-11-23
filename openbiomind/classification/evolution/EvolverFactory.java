package classification.evolution;

import classification.*;
import dataset.Dataset;

public class EvolverFactory implements TrainerFactory{

       private EvolutionParametersFactory epf;
    
       public EvolverFactory(EvolutionParametersFactory epf){
              this.epf=epf;
       }
       
       public Trainer makeTrainer(){
              return new Evolver(epf.makeParameters());
       }
       
       public void setDataset(Dataset dataset){
              this.epf.setDataset(dataset);
       }
       
}