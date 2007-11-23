package classification.evolution.gp.operators;

import java.io.*;
import java.util.Map;

public class ThresholdInputOperator extends LeafOperator{
	
	   public static final String OPERATOR_NAME="inputThreshold";
	
	   private String feature;
	   private float threshold;
	
	   public ThresholdInputOperator(String feature,float threshold){
		      this.feature=feature;
		      this.threshold=threshold;
	   }
	   
	   public ThresholdInputOperator(String operatorLine){

		      String[] cols=operatorLine.split(" ");
		   
		      this.feature=cols[cols.length-2];
		      this.threshold=Float.valueOf(cols[cols.length-1]);
	   }
	   
	   public float compute(Map<String,Float> feature2value){
		      return feature2value.get(feature)-this.threshold;
	   }
	   
	   public void write(Writer writer) throws IOException{
		      writer.write(OPERATOR_NAME+" "+feature+" "+threshold);
	   }
}