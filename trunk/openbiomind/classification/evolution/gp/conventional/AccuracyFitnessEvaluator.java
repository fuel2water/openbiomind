package classification.evolution.gp.conventional;

import java.util.List;

import classification.evolution.*;
import classification.ConfusionMatrix;
import dataset.Entity;

public class AccuracyFitnessEvaluator implements FitnessEvaluator{
    
       private static AccuracyFitnessEvaluator instance=new AccuracyFitnessEvaluator();
      
       public static AccuracyFitnessEvaluator getInstance(){
              return instance;
       }
      
       private AccuracyFitnessEvaluator(){
           
       }
    
       public float evaluateFitness(Evolvable evolvable,List<Entity> entities){
          
              ConfusionMatrix stats=new ConfusionMatrix(evolvable,entities);
             
              //return stats.balancedAccuracy();
              return stats.accuracy();
       }
    
}