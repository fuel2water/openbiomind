package classification.evolution.gp.operators;

import java.io.*;
import java.util.Map;

public class DirectInputOperator extends LeafOperator{
	
	   public static final String OPERATOR_NAME="input";
       
	   private String feature;
	
	   /**
        * Bizarre operator that threats a string either as a model file line or as a feature name. Occasionally I will
        * think of a better solution for this. 
        * @param line
	    */
       public DirectInputOperator(String line){
		      String[] cols=line.trim().split(" ");
              
              if (cols.length>=2){
                 this.feature=cols[1];
                 return;
              }
              this.feature=line;
	   }
	   
	   public float compute(Map<String,Float> feature2value){
		      return feature2value.get(feature);
	   }
	   
	   public void write(Writer writer) throws IOException{
		      writer.write(OPERATOR_NAME+" "+feature);
	   }
}