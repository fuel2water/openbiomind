package classification.evolution.gp.moses;

import java.util.*;

import dataset.*;
import classification.evolution.*;
import classification.evolution.gp.*;

public class MosesDirectEvolutionParametersFactory implements EvolutionParametersFactory{

       private static MosesDirectEvolutionParametersFactory instance=new MosesDirectEvolutionParametersFactory();
       
       private List<String> features=new ArrayList<String>();        
      
       private MosesDirectEvolutionParametersFactory(){
       }

       public void setDataset(Dataset dataset){
    	      this.features=dataset.getFeatures();
       }

       public static MosesDirectEvolutionParametersFactory getInstance(){
              return instance;
       }

       public EvolutionParameters makeParameters(){
              return new EvolutionParameters(new SimpleAutomataFactory(new MosesDirectOperatorFactory(features),0.01f,100),MosesLikeFitnessEvaluator.getInstance());
       }

}