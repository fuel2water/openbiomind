package classification.evolution;

import classification.Classifier;
import dataset.Dataset;

public abstract class Evolvable implements Classifier{

       private float fitness;

       public abstract int size();
       
       public float getFitness(){
              return fitness;
       }

       public void computeFitness(Dataset train,FitnessEvaluator fitnessEvaluator){
              fitness=fitnessEvaluator.evaluateFitness(this,train.getEntities());
       }
       
}