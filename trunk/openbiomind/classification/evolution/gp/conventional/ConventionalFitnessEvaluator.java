package classification.evolution.gp.conventional;

import java.util.List;

import classification.evolution.*;
import classification.ConfusionMatrix;
import dataset.Entity;

public class ConventionalFitnessEvaluator implements FitnessEvaluator{
	
	   private static ConventionalFitnessEvaluator instance=new ConventionalFitnessEvaluator();
	  
	   public static ConventionalFitnessEvaluator getInstance(){
		      return instance;
	   }
	  
	   private ConventionalFitnessEvaluator(){
		   
	   }
	
       public float evaluateFitness(Evolvable evolvable,List<Entity> entities){
		  
		      ConfusionMatrix stats=new ConfusionMatrix(evolvable,entities);
		     
		      return stats.fMeasure();
	   }
	
}