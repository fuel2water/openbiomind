package classification.evolution;

import java.util.List;

import dataset.Entity;

public interface FitnessEvaluator{

       public float evaluateFitness(Evolvable evolvable,List<Entity> entities);

}