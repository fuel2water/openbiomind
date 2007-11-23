package classification.evolution.gp.operators;

import java.io.*;

public class SubOperator extends InternalOperator{
	   
       public static final String OPERATOR_NAME="sub";
       
       public static SubOperator instance=new SubOperator();

       public static SubOperator getInstance(){
              return instance;
       }
       
       private SubOperator(){
       }
       
	   public float compute(float a,float b){
		      return a-b;
	   }
	   
	   public boolean isUnary(){
		      return false;
	   }
	   
	   public void write(Writer writer) throws IOException{
		      writer.write("sub");
	   }
}