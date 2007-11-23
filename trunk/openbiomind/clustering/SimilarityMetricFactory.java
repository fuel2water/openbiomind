package clustering;

public class SimilarityMetricFactory{
    
       public static final String EUCLIDIAN_NAME="Euclidean";
       public static final String COSINE_NAME="Cosine";
 
       public static final SimilarityMetricFactory instance=new SimilarityMetricFactory();
       
       private SimilarityMetricFactory(){
       }
       
       public static SimilarityMetricFactory getInstance(){
              return instance;
       }
       
       public SimilarityMetric makeSimilarityMetric(String metricName){
              if (metricName.equals(EUCLIDIAN_NAME)){
                 return new EuclideanMetric();
              }
              if (metricName.equals(COSINE_NAME)){
                 return new CosineMetric();
              }
              return null;
       }
}