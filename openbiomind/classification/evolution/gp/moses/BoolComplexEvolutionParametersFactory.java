package classification.evolution.gp.moses;

import java.util.*;

import dataset.*;
import classification.evolution.*;
import classification.evolution.gp.*;
import classification.evolution.gp.conventional.AccuracyFitnessEvaluator;

public class BoolComplexEvolutionParametersFactory implements EvolutionParametersFactory{

       private static BoolComplexEvolutionParametersFactory instance=new BoolComplexEvolutionParametersFactory();
      
       private HashMap<String,Float> f2m;
      
       private BoolComplexEvolutionParametersFactory(){
       }

       public void setDataset(Dataset dataset){
              f2m=new HashMap<String,Float>();
              for (String f:dataset.getFeatures()){
             
                  ArrayList<Float> values=new ArrayList<Float>();
                 
                  for (Entity e:dataset.getEntities()){
                      values.add(e.get(f));
                  }
                  Collections.sort(values);
                  f2m.put(f,values.get(values.size()/2));
              }
       }

       public static BoolComplexEvolutionParametersFactory getInstance(){
              return instance;
       }

       public EvolutionParameters makeParameters(){
              return new EvolutionParameters(new SimpleAutomataFactory(new MosesLikeOperatorFactory(f2m),0.01f,100),AccuracyFitnessEvaluator.getInstance());
       }

}