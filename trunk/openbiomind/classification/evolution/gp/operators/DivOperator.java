package classification.evolution.gp.operators;

import java.io.*;

public class DivOperator extends InternalOperator{
	   
       public static final String OPERATOR_NAME="div";
       
       public static DivOperator instance=new DivOperator();

       public static DivOperator getInstance(){
              return instance;
       }
       
       private DivOperator(){
       }
       
	   public float compute(float a,float b){
		      if (b==0.0f){
		    	 return 0.0f;
		      }
		      return a/b;
	   }
	   
	   public boolean isUnary(){
		      return false;
	   }
	   
	   public void write(Writer writer) throws IOException{
		      writer.write("div");
	   }
}