package classification.evolution.gp.conventional;

import java.util.*;

import dataset.*;
import classification.evolution.*;
import classification.evolution.gp.*;

public class ConventionalEvolutionParametersFactory implements EvolutionParametersFactory{

       private static ConventionalEvolutionParametersFactory instance=new ConventionalEvolutionParametersFactory();
      
       private List<String> features;
      
       private ConventionalEvolutionParametersFactory(){
       }

       public void setDataset(Dataset dataset){
    	      features=dataset.getFeatures();
       }

       public static ConventionalEvolutionParametersFactory getInstance(){
              return instance;
       }

       public EvolutionParameters makeParameters(){
              return new EvolutionParameters(new SimpleAutomataFactory(new ConventionalOperatorFactory(features),0.01f,100),ConventionalFitnessEvaluator.getInstance());
       }

}