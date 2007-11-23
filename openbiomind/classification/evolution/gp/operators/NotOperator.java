package classification.evolution.gp.operators;

import java.io.*;

public class NotOperator extends LogicOperator{
	
	   public static final String OPERATOR_NAME="not";
       
	   private static NotOperator instance=new NotOperator();

	   public static NotOperator getInstance(){
		      return instance;
	   }
	   
	   private NotOperator(){
	   }
	   
	   public float compute(float a,float b){
		      return toFloat(!toBoolean(a));
	   }
	   
	   public boolean isUnary(){
		      return true;
	   }
	   
	   public void write(Writer writer) throws IOException{
		      writer.write("not");
	   }
}