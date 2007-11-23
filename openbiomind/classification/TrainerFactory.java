package classification;

import dataset.Dataset;

public interface TrainerFactory{
    
       public Trainer makeTrainer();
       
       public void setDataset(Dataset dataset);
}