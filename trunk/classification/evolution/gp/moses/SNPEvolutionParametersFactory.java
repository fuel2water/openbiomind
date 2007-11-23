package classification.evolution.gp.moses;

import java.util.*;

import dataset.*;
import classification.evolution.*;
import classification.evolution.gp.*;
import classification.evolution.gp.conventional.AccuracyFitnessEvaluator;

public class SNPEvolutionParametersFactory implements EvolutionParametersFactory{

       private static SNPEvolutionParametersFactory instance=new SNPEvolutionParametersFactory();
       
       private List<String> features=new ArrayList<String>();        
      
       private SNPEvolutionParametersFactory(){
       }

       public void setDataset(Dataset dataset){
              this.features=dataset.getFeatures();
       }

       public static SNPEvolutionParametersFactory getInstance(){
              return instance;
       }

       public EvolutionParameters makeParameters(){
              return new EvolutionParameters(new SimpleAutomataFactory(new SNPOperatorFactory(features),0.01f,100),AccuracyFitnessEvaluator.getInstance());
       }

}