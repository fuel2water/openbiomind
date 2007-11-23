package classification.evolution.gp.operators;

public abstract class LogicOperator extends InternalOperator{
    
       public static final float TRUE=1.0f;
       public static final float FALSE=-1.0f;
	
	   protected boolean toBoolean(float value){
		         return value>0.0f;
	   }
	   
	   protected float toFloat(boolean value){
		         if (value){
		        	return TRUE;
		         }
		         return FALSE;
	   }
	   
}