package clustering;

import java.util.List;

public interface SimilarityMetric{
    
       public float computeSimilarity(List<Float> v1,List<Float> v2);
}