package classification.evolution.gp.operators;

import java.util.Map;

public abstract class InternalOperator implements Operator{
	
	   public boolean isLeaf(){
		      return false;
	   }
	   
	   public float compute(Map<String,Float> feature2Value){
		      return 0.0f;
	   }
}