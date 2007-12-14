package task;

import java.io.*;
import java.util.*;

import classification.Classifier;
import dataset.Dataset;

/**
 * Computes and retrieves frequency-based feature utility.
 * @author emac
 *
 */
public class UtilityComputer{
	
      private static final Set<String> mandatoryOptions=new HashSet<String>();
      private static final Set<String> optionalOptions=new HashSet<String>();
      private static final String RESULTS_DIRECTORY_OPTION="-r";
      private static final String OUTPUT_FILE_OPTION="-o";
      private static final String BASE_DATASET_OPTION="-d";
    
      static {
             mandatoryOptions.add(RESULTS_DIRECTORY_OPTION);
             mandatoryOptions.add(OUTPUT_FILE_OPTION);
             mandatoryOptions.add(BASE_DATASET_OPTION);
             optionalOptions.add("-"+PipelineParameters.TARGET_CATEGORY_PROPERTY);
      }
      
      private Map<String,Float> d2f=new HashMap<String,Float>(); 
      private Map<String,Float> s2f=new HashMap<String,Float>(); 
	  private int models=0;
	  private Map<String,Integer> feature2Count=new HashMap<String,Integer>();
	  private Map<String,Integer> diff2Rank=new HashMap<String,Integer>();
	  private Map<String,Integer> sam2Rank=new HashMap<String,Integer>();
	  private Map<String,String> f2d=new HashMap<String,String>();
	
	  /**
	   * Empty constructor for MUTIC transformation.
	   *
	   */
	  public UtilityComputer(){
	  }
	  
      /**
       * Computes a rank map from a feature-to-value map.
       */
	  private static Map<String,Integer> makeRankMap(Map<String,Float> f2v){
		  
		      Map<Float,ArrayList<String>> v2f=new HashMap<Float,ArrayList<String>>();
		      
		      for (String feature:f2v.keySet()){
		    	  
		    	  float value=f2v.get(feature);
		    	  
		    	  if (!v2f.containsKey(value)){
		    		 v2f.put(value,new ArrayList<String>());
		    	  }
		    	  v2f.get(value).add(feature);
		      }
		      
		      ArrayList<Float> values=new ArrayList<Float>(v2f.keySet());
		      int rank=1;
		      Map<String,Integer> output=new HashMap<String,Integer>();
		      
		      Collections.sort(values);
		      Collections.reverse(values);
		      for (float v:values){
		    	  for (String f:v2f.get(v)){
		    		  output.put(f,rank);
		    	  }
		    	  rank+=1;
		      }
		      return output;
	  }
	  
	  /**
       * Computes feature utility from a metatask result.
       * @param metaResult
       */
	  public UtilityComputer(MetaTaskResult metaResult,Dataset dataset){
    	     for (ClassificationTaskResult result:metaResult.getTaskResults()){
    	    	 this.accountResult(result);
    	     }
    	     for (String feature:dataset.getFeatures()){
    	    	 System.out.println("Computing feature "+feature);
    	    	 d2f.put(feature,dataset.computeDifferentiationFor(feature));
    	    	 s2f.put(feature,dataset.computeSAMFor(feature));
    	    	 f2d.put(feature,dataset.getDescription(feature));
    	     }
    	     diff2Rank=makeRankMap(d2f);
    	     sam2Rank=makeRankMap(s2f);
      }
	  
	  /**
	   * Accounts result from a single task.
	   * @param result
	   */
	  public void accountResult(ClassificationTaskResult result){
		     for (FoldResult fold:result.getFolds()){
		    	 for (Classifier model:fold.getEnsemble().getComponents()){
		    		 models+=1;
		    		 for (String feature:model.featureSet()){
		    			 if (!feature2Count.containsKey(feature)){
		    				feature2Count.put(feature,0);
		    			 }
		    			 feature2Count.put(feature,feature2Count.get(feature)+1);
		    		 }
		    	 }
		     }
	  }
	  
      public int getDiffRankOf(String feature){
             if (!diff2Rank.containsKey(feature)){
                return diff2Rank.size();
             }
             return this.diff2Rank.get(feature);
      }

      public int getSAMRank(String feature){
             if (!this.sam2Rank.containsKey(feature)){
                return this.sam2Rank.size();
             }
             return this.sam2Rank.get(feature);
      }
      
      public float getSAMOf(String feature){
             if (!s2f.containsKey(feature)){
                return 0.0f;
             }
             return this.s2f.get(feature);              
      }
      
      public float getDifferentiationOf(String feature){
             if (!d2f.containsKey(feature)){
                return 0.0f;
             }
             return d2f.get(feature);
      }
      
      /**
       * Returns the utility of a given feature.
       * @param feature
       * @return
       */
	  public float getUtilityOf(String feature){
             if (!feature2Count.containsKey(feature)){
                return 0.0f;
             }
    	     return feature2Count.get(feature)*100.0f/models;
      }
	  
	  /**
	   * Returns the feature set of this utility computer.
	   * @return
	   */
      public Set<String> getFeatures(){
		     return feature2Count.keySet();
	  }
	  
      /**
       * Saves the utility table in a file of given name.
       * @param fileName
       */
      public void saveAs(String fileName) throws IOException{
    	  
    	     Writer writer=new FileWriter(fileName);
    	     Map<Float,ArrayList<String>> v2f=new HashMap<Float,ArrayList<String>>();
    	  
    	     for (String feature:this.feature2Count.keySet()){
    	    	 
    	    	 float utility=this.getUtilityOf(feature);
    	    	 
    	    	 if (!v2f.containsKey(utility)){
    	    		v2f.put(utility,new ArrayList<String>());
    	    	 }
    	    	 v2f.get(utility).add(feature);
    	     }
    	     
    	     ArrayList<Float> sorter=new ArrayList<Float>(v2f.keySet());
    	     int rank=1;
    	    		 
    	     Collections.sort(sorter);
    	     Collections.reverse(sorter);
    	     for (float utility:sorter){
    	    	 for (String feature:v2f.get(utility)){
    	    	     writer.write(feature+"\t"+utility+"\t"+rank+"\t"+this.diff2Rank.get(feature)+"\t"+this.sam2Rank.get(feature)+"\t"+f2d.get(feature)+"\n");
    	    	 }
    	    	 rank+=1;
    	     }
    	     writer.close();
      }

      /**
       * Execution flow.
       * @param parameters
       */
      public static void execute(PipelineParameters parameters){
             String resultDir=parameters.getMetataskOutputPath();
             String outputFile=parameters.getUtilityFile();
             String datasetName=parameters.getDatasetPath();
             String category=parameters.getTargetCategory();
             MetaTaskResult result=null;
             Dataset dataset=null;
             
             try {
                 result=new MetaTaskResult(resultDir);
                 
                 FileReader reader=new FileReader(datasetName);
                 
                 dataset=new Dataset(reader,category);
             }
             catch (IOException e){
                   System.err.println("Error while loading metatask result from "+resultDir);
             }
             
             UtilityComputer utilityComputer=new UtilityComputer(result,dataset);
             
             try {
                 utilityComputer.saveAs(outputFile);
             }
             catch (IOException e){
                   System.err.println("Error while saving feature utilities at "+outputFile);
             }
      }
      
      /**
	   * Computes feature utilities for a given set of task outputs and dumps result in a file.
	   * @param args
	   */
	  public static void main(String[] args){
             OptionManager options=new OptionManager(mandatoryOptions,optionalOptions,args);
             boolean approved=true;
             String errors=options.makeErrorMessages();
             
             if (!errors.equals("")){
                System.out.println(errors);
                approved=false;
             }
             if (!approved){
                System.out.println("Usage: java task.UtilityComputer <-r result dir> <-o output file> <-d base dataset> [-"+
                        PipelineParameters.TARGET_CATEGORY_PROPERTY+" target category]");
		    	return;
		     }
		     
             Properties properties=new Properties();
             
             try {
                 InputStream inStream=ClassLoader.getSystemResourceAsStream(CompletePipeline.PIPELINE_PROPERTIES_FILE);
             
                 properties.load(inStream);
                 inStream.close();
             }
             catch (IOException e){
                   System.err.println("Error loading properties file.");
             }
             for (String option:options.getOptionalSet()){
                 if (options.containsOption(option)){
                    properties.setProperty(option.replace("-",""),options.getOption(option));      
                 }
             }
             
             PipelineParameters parameters=new PipelineParameters(properties,args);
             
             parameters.setMetataskOutputPath(options.getOption(RESULTS_DIRECTORY_OPTION));
             parameters.setUtilityFile(options.getOption(OUTPUT_FILE_OPTION));
             parameters.setDatasetPath(options.getOption(BASE_DATASET_OPTION));
             execute(parameters);
	  }
}