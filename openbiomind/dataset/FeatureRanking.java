package dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Supplies ranks of features of a predefined length for given datasets.
 * @author Lœcio
 *
 */
public abstract class FeatureRanking{
	
	   private int numberOfFeatures;
	
       /**
        * Sets the length of ranks to be supplied.
        * @param numberOfFeatures
        */
	   public FeatureRanking(int numberOfFeatures){
    	      this.numberOfFeatures=numberOfFeatures;
       }
	
	   /**
        * Implements the particular measurement of feature quality in the children classes.
        * @param feature
        * @param entities
        * @return
        */
	   protected abstract float qualityOf(String feature,Dataset dataset);
	
	   /**
	    * Supplies the feature rank for a given dataset.
	    * @param dataset
	    * @return
	    */
	   public List<String> rankingFor(Dataset dataset){

              HashMap<Float,List<String>> d2fs=new HashMap<Float,List<String>>();
              
              for (String f:dataset.getFeatures()){
                  float quality=this.qualityOf(f,dataset);
                 
                  if (!d2fs.containsKey(quality)){
                     d2fs.put(quality,new ArrayList<String>());
                  }
                  d2fs.get(quality).add(f);
              }
             
              ArrayList<Float> values=new ArrayList<Float>(d2fs.keySet());
             
              Collections.sort(values);
              Collections.reverse(values);
             
              List<String> output=new ArrayList<String>();
             
              for (float d:values){
                  for (String f:d2fs.get(d)){
                      output.add(f);
                      if (output.size()==numberOfFeatures){
                         return output;
                      }
                  }
              }
              return output;
	   }
}