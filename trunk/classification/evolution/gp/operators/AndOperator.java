package classification.evolution.gp.operators;

import java.io.*;

public class AndOperator extends LogicOperator{
	   
	   public static final String OPERATOR_NAME="and";
       
	   public static AndOperator instance=new AndOperator();

	   public static AndOperator getInstance(){
		      return instance;
	   }
	   
	   private AndOperator(){
	   }
	   
	   public float compute(float a,float b){
		      return toFloat(toBoolean(a)&&toBoolean(b));
	   }
	   
	   public boolean isUnary(){
		      return false;
	   }
	   
	   public void write(Writer writer) throws IOException{
		      writer.write(OPERATOR_NAME);
	   }
}