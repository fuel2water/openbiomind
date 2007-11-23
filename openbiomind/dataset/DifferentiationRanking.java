package dataset;

public class DifferentiationRanking extends FeatureRanking{
	
	   public DifferentiationRanking(int numberOfFeatures){
	          super(numberOfFeatures);	   
	   }
	
	   protected float qualityOf(String feature,Dataset dataset){
                 return dataset.computeDifferentiationFor(feature);
	   }
}