package classification.evolution.gp.operators;

import java.io.*;

public class MulOperator extends InternalOperator{
	   
	   public static final String OPERATOR_NAME="mul";
       
	   public static MulOperator instance=new MulOperator();

	   public static MulOperator getInstance(){
		      return instance;
	   }
	   
	   private MulOperator(){
	   }
	   
	   public float compute(float a,float b){
		      return a*b;
	   }
	   
	   public boolean isUnary(){
		      return false;
	   }
	   
	   public void write(Writer writer) throws IOException{
		      writer.write("mul");
	   }
}