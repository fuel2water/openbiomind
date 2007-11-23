package clustering;

import java.util.List;

class EuclideanMetric implements SimilarityMetric{
    
      public float computeSimilarity(List<Float> v1,List<Float> v2){
          
             float sum=0.0f;
             
             for (int i=0;i<v1.size();i++){
                 
                 float diff=v1.get(i)-v2.get(i);
                 
                 sum+=diff*diff;
             }
             return (float)(1.0/(1.0+Math.sqrt(sum)));
      }
      
}