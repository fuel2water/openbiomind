package classification.evolution.gp.moses;

import java.util.List;

import dataset.Entity;
import classification.evolution.*;

public class MosesLikeFitnessEvaluator implements FitnessEvaluator{

       private static MosesLikeFitnessEvaluator instance;

       public float evaluateFitness(Evolvable evolvable,List<Entity> entities){
      
              int hits=0;
             
              for (Entity entity:entities){
                  if (evolvable.evaluate(entity.getMap())==entity.getExpected()){
                     hits+=1;
                  }
              }
              return hits*1.0f-0.5f*evolvable.size();
       } 
      
       public static MosesLikeFitnessEvaluator getInstance(){
              if (instance==null){
                 instance=new MosesLikeFitnessEvaluator();
              }
              return instance;
       }
      
}