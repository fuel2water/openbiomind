package dataset; 

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random; 
import java.util.Set;

//import sun.net.dns.ResolverConfiguration.Options;
import task.MetaTask; 
import task.MetaTaskResult;
import task.UtilityComputer;

public class GPRanking extends FeatureRanking {
	//private Dataset currentDataset; 
	private HashMap<String, Float> features = new HashMap<String, Float>(); // the ranked features 
	private String targetCategory;
	private int numberOfTasks;
	private String gpType; 
	private String datasetDir; 
	private String tmpOutputDir; // the pathway to a temporary output directory
	private float avgTrainPerformance; 
	private int numberOfFeatures; 
	
	public float getTrainPerformance() {
		return avgTrainPerformance; 
	}
	
	
	private void getGPRankings(Dataset dataset, FoldHolder foldHolder, String outputDir, String targetCategory, int numberOfTasks, String gpType) {
	
		
		// generate a random output directory
        File tester=new File(outputDir);
        if (tester.exists()) {
        	tester.delete(); 
        	tester.mkdir();
        }
        
		MetaTask runner = new MetaTask(foldHolder.getFolds(), numberOfTasks, outputDir, MetaTask.getEvolutionParametersFactory(gpType), false); 
		
		try {
			runner.execute();
		} catch (IOException e) {
			System.err.println("Error while dumping results."); 
			e.printStackTrace(); 
		}
		
		String outputFile = outputDir + ".txt"; 
		// now call utilityComputer to come up with a tmp file of the top ranked genes 
        try {
	        MetaTaskResult result = new MetaTaskResult(outputDir);
            //this.currentDataset = dataset; 
            this.avgTrainPerformance = result.computeTrainAccuraciesDistribution().average();
            
            UtilityComputer utilityComputer=new UtilityComputer(result,dataset);
			
			// String outputFile = "tmp" + String.valueOf(randomIndex) + ".txt"; 
			
			try {
                utilityComputer.saveAs(outputFile);
            }
            catch (IOException e){
                  System.err.println("Error while saving feature utilities at "+outputFile);
            }
		} 
		catch (IOException e) {
        	System.err.println("Error while dumping results.");
        	e.printStackTrace(); 
        }
		
		// parse the gene ranking file outputted by UtilityComputer
		try {
			BufferedReader in = new BufferedReader(new FileReader(outputFile)); 
			String strLine; 
			
			// Read file line by line
			while ((strLine = in.readLine()) != null) {
				// Print the content on the console
				System.out.println(strLine); 
				String [] tabSplitArray = null;
				tabSplitArray = strLine.split("\t");
				
				String geneName = tabSplitArray[0];
				String utility = tabSplitArray[1]; 
				
				features.put(geneName, Float.valueOf(utility));
				//System.out.println("Gene Name: " + geneName);
				//System.out.println("Utility: " + utility); 
			}
		} catch (FileNotFoundException e) {
			System.out.println("The temporary results output file not found"); 
		} catch (IOException e) {
			System.out.println("Error reading the result output file"); 
		}
		

	}
	
	public GPRanking(int numberOfFeatures, String datasetPath, FoldHolder foldHolder, String outputDir, String targetCategory, int numberOfTasks, String gpType) {
		super(numberOfFeatures);
		
		this.numberOfFeatures = numberOfFeatures; 
        Dataset dataset; 
		try {
			FileReader reader = new FileReader(datasetPath);
			dataset=new Dataset(reader,targetCategory);
			getGPRankings(dataset, foldHolder, outputDir, targetCategory, numberOfTasks, gpType); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public GPRanking(int numberOfFeatures, Dataset dataset, FoldHolder foldHolder, String outputDir, String targetCategory, int numberOfTasks, String gpType) {
		super(numberOfFeatures);
		
		this.numberOfFeatures = numberOfFeatures; 
		getGPRankings(dataset, foldHolder, outputDir, targetCategory, numberOfTasks, gpType); 
	}
	
	protected float qualityOf(String feature, Dataset dataset) {
		/*if (dataset.getFeatures()!= this.currentDataset.getFeatures()) {
			throw new RuntimeException("The dataset given is not the right dataset"); 
		}*/
		
		//System.out.println("Feature requested: " + feature); 
		
		if (features.containsKey(feature)) {
			//System.out.println("Feature found"); 
			return features.get(feature); 
		}
		else {
			//System.out.println("Feature not found");
			return 0; 
		}
	}
	   public List<String> rankingFor(Dataset dataset){

           HashMap<Float,List<String>> d2fs=new HashMap<Float,List<String>>();
           
           for (String f:dataset.getFeatures()){
               float quality=this.qualityOf(f,dataset);
              
               if (quality > 0.0) {
	               if (!d2fs.containsKey(quality)){
	                  d2fs.put(quality,new ArrayList<String>());
	               }
	               d2fs.get(quality).add(f);
               } else {
            	   //System.out.println("Feature not added!"); 
               }
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
	
	
	public static void main(String[] args) {
		// GPRanking gpRanking = new GPRanking(0, "datafiles/ext.go.pir.varm126.tab", "datafiles/varm126-3/", "", 50, "boolsimple");
      }
		
	  
		
	}
