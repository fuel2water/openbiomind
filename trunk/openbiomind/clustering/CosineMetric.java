package clustering;

import java.util.List;

public class CosineMetric implements SimilarityMetric{
    
      public float computeSimilarity(List<Float> v1,List<Float> v2){
          
             float sumOver=0.0f,sum1=0.0f,sum2=0.0f;
             
             for (int i=0;i<v1.size();i++){
                 sumOver+=v1.get(i)*v2.get(i);
                 sum1+=v1.get(i)*v1.get(i);
                 sum2+=v2.get(i)*v2.get(i);
             }
             
             float under=sum1*sum2;
             
             if (under==0.0f){
                return 0.0f;
             }
             return (float)(sumOver/Math.sqrt(under));
      }
      
}