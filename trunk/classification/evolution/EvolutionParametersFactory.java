package classification.evolution;

import dataset.Dataset;

public interface EvolutionParametersFactory{

       public void setDataset(Dataset dataset);

       public EvolutionParameters makeParameters();

}