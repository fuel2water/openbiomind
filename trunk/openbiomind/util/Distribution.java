package util;

import java.util.*;

public class Distribution{
	
       private static final float[] pvalues={0.5f,0.6f,0.7f,0.8f,0.9f,0.95f,0.99f,0.999f}; 
       private static final float[] tvalues={0.674f,0.842f,1.036f,1.282f,1.645f,1.960f,2.576f,3.291f};
    
	   Map<Float,Integer> value2Frequency=new HashMap<Float,Integer>(); 
	
	   public void account(float value){
		      if (!this.value2Frequency.containsValue(value)){
		    	 this.value2Frequency.put(value,0);
		      }
		      this.value2Frequency.put(value,this.value2Frequency.get(value)+1);
	   }

       /**
        * Returns the number of elements accounted in the distribution.
        * @return
        */
       public int numberOfElements(){
           
              int output=0;
              
              for (Integer count:this.value2Frequency.values()){
                  output+=count;
              }
              return output;
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
	   
       /**
        * Simplified computation of p-value based on t-test.
        * @return
        */
       public static float computePValueFor(Distribution d1,Distribution d2){
           
              float s1=d1.standardDeviation(),s2=d2.standardDeviation();
              float v1=s1*s1,v2=s2*s2;
              int numberOfExamples=Math.min(d1.numberOfElements(),d2.numberOfElements());
              float tvalue=(float)(Math.abs(d1.average()-d2.average())/Math.sqrt((v1+v2)/numberOfExamples));
              
              for (int i=0;i<tvalues.length-1;i++){
                  if ((tvalue>=tvalues[i])&&(tvalue<=tvalues[i+1])){
                     return 1.0f-pvalues[i];
                  }
              }
              return 1.0f-pvalues[pvalues.length-1];
       }
       
}