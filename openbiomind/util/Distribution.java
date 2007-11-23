package util;

import java.util.*;

public class Distribution{
	
	   Map<Float,Integer> value2Frequency=new HashMap<Float,Integer>(); 
	
	   public void account(float value){
		      if (!this.value2Frequency.containsValue(value)){
		    	 this.value2Frequency.put(value,0);
		      }
		      this.value2Frequency.put(value,this.value2Frequency.get(value)+1);
	   }

	   public float average(){
		   
		      float sum=0.0f;
		      int count=0;
		      
		      for (Float value:value2Frequency.keySet()){
		    	  sum+=value2Frequency.get(value)*value;
		    	  count+=value2Frequency.get(value);
		      }
		      return sum/count;
	   }
	   
	   public float standardDeviation(){

		      float sum=0.0f;
		      int count=0;
		      float avg=average();
		      
		      for (Float value:value2Frequency.keySet()){
		    	  
		    	  float diff=value-avg;
		    	  float square=diff*diff;
		    	  
		    	  sum+=value2Frequency.get(value)*square;
		    	  count+=value2Frequency.get(value);
		      }
		      return (float)Math.sqrt(sum/count);
	   }
	   
}