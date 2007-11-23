package dataset;

public class SAMRanking extends FeatureRanking{
    
       public SAMRanking(int numberOfFeatures){
              super(numberOfFeatures);     
       }
    
       protected float qualityOf(String feature,Dataset dataset){
                 return dataset.computeSAMFor(feature);
       }
}