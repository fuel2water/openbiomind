package clustering;

import java.util.List;

import snps.SNPFeature;

public class SNPMetric implements SimilarityMetric{
    
      public float computeSimilarity(List<Float> v1,List<Float> v2){
          
             float sum=0.0f;
             
             for (int i=0;i<v1.size();i++){
                 
                 if (v1.get(i)==v2.get(i)){
                    sum+=1.0f;
                    continue;
                 }
                 if ((v1.get(i)==SNPFeature.AA_VALUE)&&(v2.get(i)==SNPFeature.BB_VALUE)){
                    sum+=1.0f;
                    continue;
                 }
                 if ((v1.get(i)==SNPFeature.BB_VALUE)&&(v2.get(i)==SNPFeature.AA_VALUE)){
                    sum+=1.0f;
                    continue;
                 }
             }
             return sum/v1.size();
      }
      
}