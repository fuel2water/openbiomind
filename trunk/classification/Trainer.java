package classification;

import dataset.Dataset;

public interface Trainer{
    
       public Ensemble train(Dataset dataset);
       
}