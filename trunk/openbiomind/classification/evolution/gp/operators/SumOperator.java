package classification.evolution.gp.operators;

import java.io.*;

public class SumOperator extends InternalOperator{
	   
	   public static final String OPERATOR_NAME="sum";
       
	   public static SumOperator instance=new SumOperator();

	   public static SumOperator getInstance(){
		      return instance;
	   }
	   
	   private SumOperator(){
	   }
	   
	   public float compute(float a,float b){
		      return a+b;
	   }
	   
	   public boolean isUnary(){
		      return false;
	   }
	   
	   public void write(Writer writer) throws IOException{
		      writer.write("sum");
	   }
}