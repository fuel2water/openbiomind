package classification.evolution.gp.operators;

import java.io.*;
import java.util.Map;

import util.Randomizer;

public class ConstantOperator extends LeafOperator{
	
       public static final String OPERATOR_NAME="const";
       
	   private float value;
	
       public ConstantOperator(String line){
           
              String[] cols=line.split(" ");
              
              value=Float.valueOf(cols[cols.length-1]);
       }
       
       public ConstantOperator(){
		      value=Randomizer.getInstance().gaussianReal();
	   }
	   
	   public float compute(Map<String,Float> feature2value){
		      return value;
	   }
	   
	   public void write(Writer writer) throws IOException{
		      writer.write("const "+value);
	   }
}