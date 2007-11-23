package classification.evolution.gp.operators;

import java.io.*;

public class OrOperator extends LogicOperator{
	
	   public static final String OPERATOR_NAME="or";
       
	   private static OrOperator instance=new OrOperator();

	   public static OrOperator getInstance(){
		      return instance;
	   }
	   
	   private OrOperator(){
	   }
	   
	   public float compute(float a,float b){
		      return toFloat(toBoolean(a)||toBoolean(b));
	   }
	   
	   public boolean isUnary(){
		      return false;
	   }
	   
       public void write(Writer writer) throws IOException{
    	      writer.write("or");
       }
       
}