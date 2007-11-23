package classification.evolution.gp.operators;

public abstract class LeafOperator implements Operator{
	
	   public boolean isLeaf(){
		      return true;
	   }
	   
	   public boolean isUnary(){
		      return true;
	   }

	   public float compute(float a,float b){
		      return 0.0f;
	   }
}