package task;

import java.io.File;
import java.util.*;

/**
 * Internal class for interpreting and storing the long list of parameters.
 * @author Lucio
 *
 */
class PipelineParameters{
      
        public static final String IS_FOLDED_PROPERTY="isFolded";
        public static final String TARGET_CATEGORY_PROPERTY="targetCategory";
        public static final String INPUT_DATASET_PROPERTY="inputDataset";
        public static final String TEST_DATASET_PROPERTY="testDataset";
        public static final String NUMBER_OF_FOLDS_PROPERTY="numberOfFolds";
        public static final String DATASET_CLUSTERING_METRIC_PROPERTY="datasetClusteringMetric";
        public static final String NUMBER_OF_TASKS_PROPERTY="numberOfTasks";
        public static final String OUTPUT_PATH_PROPERTY="outputPath";
        public static final String FEATURE_SELECTION_METHOD_PROPERTY="featureSelectionMethod";
        public static final String IS_FEATURE_SELECTED_PROPERTY="isFeatureSelected";
        public static final String NUMBER_OF_SELECTED_FEATURES_PROPERTY="numberOfSelectedFeatures";
        public static final String CLASSIFICATION_METHOD_PROPERTY="classificationMethod";
        public static final String ONTOLOGY_ASSOCIATION_FILE_PROPERTY="ontologyAssociationFile";
        public static final String ONTOLOGY_DESCRIPTION_FILE_PROPERTY="ontologyDescriptionFile";
        public static final String MAX_NODES_GRAPH_PROPERTY="topNUseful";
        public static final String MAX_COOC_EDGES_GRAPH_PROPERTY="topNCooc";
        public static final String MAX_COEX_EDGES_GRAPH_PROPERTY="topNCoex";      
        public static final String MIN_EDGES_GRAPH_PROPERTY="minEdges";      
        public static final String CLUSTERING_COLOR_PROPERTY="clusteringColors"; 
        public static final String METATASK_SHUFFLING="metataskShuffling";
        public static final String SNP_SELECTION_SHUFFLE="snpSelectionShuffle";

        private static final String TRANSFORMED_DATASET_SUBDIR="transformedDataset";
        private static final String METATASK_SUBDIR="metatask";
        private static final String UTILITY_FILE="featureUtility.txt";
        private static final String ENHANCED_DATASET="enhanced_dataset.txt";
        private static final String ENHANCED_TEST_DATASET="enhanced_test_dataset.txt";
        private static final String MUTIC_DATASET="mutic_dataset.txt";
        private static final String MUTIC_IMAGE="mutic.png";
        private static final String MUTIC_OUTPUT="mutic_output.txt";
        private static final String MOBRA_DATASET="mobra_dataset.txt";
        private static final String MOBRA_IMAGE="mobra.png";
        private static final String MOBRA_OUTPUT="mobra_output.txt";
        private static final String HORIZONTAL_DATASET="horizontal_dataset.txt";
        private static final String HORIZONTAL_OUTPUT="horizontal_output.txt";
        private static final String HORIZONTAL_IMAGE="horizontal.png";
        private static final String VERTICAL_DATASET="vertical_dataset.txt";
        private static final String VERTICAL_IMAGE="vertical.png";
        private static final String VERTICAL_OUTPUT="vertical_output.txt";
        private static final String GRAPH_FILE="graph.dot";
        
        private int numberOfTrials; 
        private String RFEOutputPath; 
        private String graphFilePath;
        private String targetCategory;
        private String datasetClusteringMetric;
        private String modelClusteringMetric;
        private int numberOfTasks;
        private String outputPath;
        private String datasetPath;
        private String testDatasetPath;
        private boolean isFolded;
        private String featureSelectionMethod;
        private boolean isFeatureSelected;
        private int numberOfFolds;
        private int numberOfSelectedFeatures;
        private String classificationMethod;
        private String ontologyDescriptionFile;
        private String ontologyAssociationFile;
        private String metaTaskOutputPath;
        private String transformedDatasetPath;
        private String enhancedDatasetPath;
        private String enhancedTestDatasetPath;
        private String utilityFile;
        private String horizontalDatasetPath;
        private String mobraDatasetPath;
        private int topNUseful;
        private int topNCooc;
        private int topNCoex;
        private String snp2gene;
        private String snpUtilityDir;
        private boolean shuffleMetatask;
        private boolean snpSelectionShuffle;
	private Float RFEEliminationRate;
	private int minimumNumberOfEdges;

        private static void testDir(String path){
            
                File tester=new File(path);
             
                if (!tester.exists()){
                   tester.mkdir();
                }
                else {
                     if (!tester.isDirectory()){
                        System.out.println("There is already a file called "+path);
                        return;
                     }
                }
        }
        
        public static String getOption(String[] args,String option){
                for (int i=0;i<args.length;i++){
                    
                    String arg=args[i];
                    
                    if (arg.equals(option)){
                       return args[i+1];
                    }
                }
                return "";
        }
        
        public static boolean hasOption(String[] args,String option){
                for (String arg:args){
                    if (arg.equals(option)){
                       return true;
                    }
                }
                return false;
        }
        
        public static void overrideProperties(String[] args,Properties properties){

                Enumeration propertyNames=properties.propertyNames();
                
                while (propertyNames.hasMoreElements()){
                    
                      String p=(String)propertyNames.nextElement();
                      
                      if (hasOption(args,"-"+p)){
                         properties.setProperty(p,getOption(args,"-"+p));
                      }
                }
        }
        
        public PipelineParameters(){
            
        }
        
        public PipelineParameters(Properties properties,String[] args){
               overrideProperties(args,properties);
               this.datasetPath=properties.getProperty(INPUT_DATASET_PROPERTY);
               targetCategory=properties.getProperty(TARGET_CATEGORY_PROPERTY);
               classificationMethod=properties.getProperty(CLASSIFICATION_METHOD_PROPERTY);
               this.ontologyAssociationFile=properties.getProperty(ONTOLOGY_ASSOCIATION_FILE_PROPERTY);
               this.ontologyDescriptionFile=properties.getProperty(ONTOLOGY_DESCRIPTION_FILE_PROPERTY);
               isFolded=Boolean.valueOf(properties.getProperty(IS_FOLDED_PROPERTY));
               if (isFolded){
                  this.numberOfFolds=Integer.valueOf(properties.getProperty(NUMBER_OF_FOLDS_PROPERTY));
               }
               else {
                    this.testDatasetPath=properties.getProperty(TEST_DATASET_PROPERTY);
               }
               isFeatureSelected=Boolean.valueOf(properties.getProperty(IS_FEATURE_SELECTED_PROPERTY));
               if (this.isFeatureSelected){
                  featureSelectionMethod=properties.getProperty(FEATURE_SELECTION_METHOD_PROPERTY);
                  this.numberOfSelectedFeatures=Integer.valueOf(properties.getProperty(NUMBER_OF_SELECTED_FEATURES_PROPERTY));
               }
               datasetClusteringMetric=properties.getProperty(DATASET_CLUSTERING_METRIC_PROPERTY);
               modelClusteringMetric=properties.getProperty(DATASET_CLUSTERING_METRIC_PROPERTY);
               numberOfTasks=Integer.valueOf(properties.getProperty(NUMBER_OF_TASKS_PROPERTY));
               outputPath=properties.getProperty(OUTPUT_PATH_PROPERTY);
               this.minimumNumberOfEdges=Integer.valueOf(properties.getProperty(PipelineParameters.MIN_EDGES_GRAPH_PROPERTY));
               this.topNCoex=Integer.valueOf(properties.getProperty(PipelineParameters.MAX_COEX_EDGES_GRAPH_PROPERTY));
               this.topNUseful=Integer.valueOf(properties.getProperty(PipelineParameters.MAX_NODES_GRAPH_PROPERTY));
               this.topNCooc=Integer.valueOf(properties.getProperty(PipelineParameters.MAX_COOC_EDGES_GRAPH_PROPERTY));
               this.enhancedDatasetPath=(this.outputPath+"/"+ENHANCED_DATASET).replace("//","/");
               this.enhancedTestDatasetPath=(this.outputPath+"/"+ENHANCED_TEST_DATASET).replace("//","/");
               this.metaTaskOutputPath=(this.outputPath+"/"+METATASK_SUBDIR).replace("//","/");
               this.transformedDatasetPath=(this.outputPath+"/"+TRANSFORMED_DATASET_SUBDIR).replace("//","/");
               this.utilityFile=this.outputPath+"/"+UTILITY_FILE;
               this.horizontalDatasetPath=this.outputPath+"/"+PipelineParameters.HORIZONTAL_DATASET;
               this.mobraDatasetPath=this.outputPath+"/"+PipelineParameters.MOBRA_DATASET;
               this.graphFilePath=this.outputPath+"/"+PipelineParameters.GRAPH_FILE;
               this.shuffleMetatask=properties.getProperty(METATASK_SHUFFLING).equals("on");
               this.snpSelectionShuffle=properties.getProperty(SNP_SELECTION_SHUFFLE).equals("on");
        }

        public int getMinimumNumberOfEdges(){
               return minimumNumberOfEdges;
        }

        public void setMininumNumberOfEdges(int minimumNumberOfEdges){
               this.minimumNumberOfEdges=minimumNumberOfEdges;
        }

        public boolean getSNPSelectionShuffle(){
               return this.snpSelectionShuffle;
        }
        
        public  String[] makeClusteringTransformParameters(String dirFile,String transform,String outfile){
                if (transform.equals(ClusteringTransformer.MOBRA)||transform.equals(ClusteringTransformer.MUTIC)){
                    String[] output=new String[8];
                   
                    output[0]=ClusteringTransformer.INPUT_DATASET_OPTION;
                    output[1]=dirFile;
                    output[2]=ClusteringTransformer.TRANSFORM_OPTION;
                    output[3]=transform;
                    output[4]=ClusteringTransformer.OUTPUT_DATASET_OPTION;
                    output[5]=outfile;
                    output[6]=ClusteringTransformer.METATASK_RESULTS_PATH_OPTION;
                    output[7]=this.metaTaskOutputPath;
                    return output;
                }
                String[] output=new String[6];
               
                output[0]=ClusteringTransformer.INPUT_DATASET_OPTION;
                output[1]=dirFile;
                output[2]=ClusteringTransformer.TRANSFORM_OPTION;
                output[3]=transform;
                output[4]=ClusteringTransformer.OUTPUT_DATASET_OPTION;
                output[5]=outfile;
                return output;
        }
        
        private String[] makeClusteringParameters(String clusteringDataset,String clusteringMetric,String outFile){
            
                String[] output=new String[6];
               
                output[0]=Clusterize.CLUSTERING_DATASET_OPTION;
                output[1]=this.outputPath+"/"+clusteringDataset;
                output[2]=Clusterize.OUTPUT_FILE_OPTION;
                output[3]=this.outputPath+"/"+outFile;
                output[4]="-"+PipelineParameters.DATASET_CLUSTERING_METRIC_PROPERTY;
                output[5]=clusteringMetric;
                return output;
        }
        
        private String[] makeViewClustersParameters(String clusteringDataset,String clusteringResult,String imageFile,String colorScheme){
            
                String[] output=new String[8];
               
                output[0]=ViewClusters.CLUSTERING_DATASET_OPTION;
                output[1]=this.outputPath+"/"+clusteringDataset;
                output[2]=ViewClusters.CLUSTERING_RESULT_OPTION;
                output[3]=this.outputPath+"/"+clusteringResult;
                output[4]=ViewClusters.OUTPUT_FILE_OPTION;
                output[5]=this.outputPath+"/"+imageFile;
                output[6]="-"+PipelineParameters.CLUSTERING_COLOR_PROPERTY;
                output[7]=colorScheme;
                return output;
        }
        
		public void setRFEEliminationRate(String option) {
			if (option != null)
				this.RFEEliminationRate = Float.valueOf(option); 
		}
		
		public float getRFEEliminationRate() {
			if (RFEEliminationRate != null)
				return RFEEliminationRate;
			else
				return 1; 
		}
        
        public String getFeatureSelectionMethod(){
               return this.featureSelectionMethod;
        }
        
        public int getNumberOfFolds(){
               return this.numberOfFolds;
        }
        
        public void setSNPUtilityDir(String value){
               this.snpUtilityDir=value;
        }
        
        public String getSNPUtilityDir(){
               return this.snpUtilityDir;
        }
        
        public void setSNP2Gene(String value){
               this.snp2gene=value;
        }
        
        public String getSNP2Gene(){
               return this.snp2gene;
        }
        
        public int getNumberOfSelectedFeatures(){
               return this.numberOfSelectedFeatures;
        }
        
        public String getTestDatasetPath(){
               return this.testDatasetPath;
        }
        
        public String getTransformedDatasetPath(){
               return this.transformedDatasetPath;
        }
        
        public boolean isFeatureSelected(){
               return this.isFeatureSelected;
        }
        
        public void setTransformedDatasetPath(String path){
               this.transformedDatasetPath=path;
        }
        
        public void setRFEOutputPath(String path) {
        	this.RFEOutputPath=path; 
        }
        
        public String getRFEOutputPath() {
        	return this.RFEOutputPath; 
        }
        
        public boolean metataskShuffling(){
               return this.shuffleMetatask;
        }
        
        public boolean isFolded(){
               return this.isFolded;
        }
        
        public void setGraphFilePath(String value){
               this.graphFilePath=value;
        }
        
        public String getGraphFilePath(){
               return this.graphFilePath;
        }
        
        public int getTopNUseful(){
               return this.topNUseful;
        }
        
        public int getTopNCooc(){
               return this.topNCooc;
        }
        
        public int getTopNCoex(){
               return this.topNCoex;
        }
        
        public void setEnhancedDatasetPath(String value){
               this.enhancedDatasetPath=value;
        }
        
        public void setMobraDatasetPath(String value){
               this.mobraDatasetPath=value;
        }
        
        public String getMobraDatasetPath(){
               return this.mobraDatasetPath;
        }
        
        public void setDatasetPath(String value){
               this.datasetPath=value;
        }
        
        public String getTargetCategory(){
               return this.targetCategory;
        }
        
        public String getEnhancedDatasetPath(){
               return this.enhancedDatasetPath;
        }
        
        public void setHorizontalDatasetPath(String value){
               this.horizontalDatasetPath=value;
        }
        
        public String getHorizontalDatasetPath(){
               return this.horizontalDatasetPath;
        }
        
        public void setFolded(boolean value){
               this.isFolded=value;
        }
        
        public void setUtilityFile(String value){
               this.utilityFile=value;
        }
        
        public String getUtilityFile(){
               return this.utilityFile;
        }
        
        public String getDatasetPath(){
               return this.datasetPath;
        }
        
        public String getOntologyAssociationFile(){
               return this.ontologyAssociationFile;
        }
        
        public String getOntologyDescriptionFile(){
               return this.ontologyDescriptionFile;
        }
        
        public void setOntologyAssociationFile(String value){
               this.ontologyAssociationFile=value;
        }
        
        public void setOntologyDescriptionFile(String value){
               this.ontologyDescriptionFile=value;
        }
        
        public int getNumberOfTasks(){
               return this.numberOfTasks;
        }
        
        public void setNumberOfTrials(String numberOfTrials) {
        	if (numberOfTrials != null)
        		this.numberOfTrials = Integer.valueOf(numberOfTrials);
        	else
        		this.numberOfTrials = 1; 
        }
        
        public int getNumberOfTrials() {
        	return this.numberOfTrials;
        }
        
        public void setMetataskOutputPath(String value){
               this.metaTaskOutputPath=value; 
        }
        
        public String getMetataskOutputPath(){
               return this.metaTaskOutputPath;
        }
        
        public String getClassificationMethod(){
               return this.classificationMethod;
        }
        
        public void execute(){
               testDir(outputPath);
               testDir(this.metaTaskOutputPath);
               EnhanceDataset.execute(this);
               if (!this.isFolded){
                   
                  PipelineParameters testEnhancementParameters=new PipelineParameters();
                   
                  testEnhancementParameters.setDatasetPath(this.testDatasetPath);
                  testEnhancementParameters.setEnhancedDatasetPath(this.enhancedTestDatasetPath);
                  testEnhancementParameters.setOntologyAssociationFile(this.ontologyAssociationFile);
                  testEnhancementParameters.setOntologyDescriptionFile(this.ontologyDescriptionFile);
                  EnhanceDataset.execute(testEnhancementParameters);
               }
               DatasetTransformer.execute(this);
               MetaTask.execute(this);
               UtilityComputer.execute(this);
               ClusteringTransformer.main(this.makeClusteringTransformParameters(this.enhancedDatasetPath,ClusteringTransformer.DATASET_HORIZONTAL,this.outputPath+"/"+HORIZONTAL_DATASET));
               Clusterize.main(this.makeClusteringParameters(HORIZONTAL_DATASET,this.datasetClusteringMetric,HORIZONTAL_OUTPUT));
               ViewClusters.main(this.makeViewClustersParameters(HORIZONTAL_DATASET,HORIZONTAL_OUTPUT,HORIZONTAL_IMAGE,ViewClusters.DEFAULT_COLORS));
               ClusteringTransformer.main(this.makeClusteringTransformParameters(this.enhancedDatasetPath,ClusteringTransformer.DATASET_VERTICAL,this.outputPath+"/"+VERTICAL_DATASET));
               Clusterize.main(this.makeClusteringParameters(VERTICAL_DATASET,this.datasetClusteringMetric,VERTICAL_OUTPUT));
               ViewClusters.main(this.makeViewClustersParameters(VERTICAL_DATASET,VERTICAL_OUTPUT,VERTICAL_IMAGE,ViewClusters.DEFAULT_COLORS));
               ClusteringTransformer.main(this.makeClusteringTransformParameters(this.enhancedDatasetPath,ClusteringTransformer.MUTIC,this.outputPath+"/"+MUTIC_DATASET));
               Clusterize.main(this.makeClusteringParameters(MUTIC_DATASET,this.modelClusteringMetric,MUTIC_OUTPUT));
               ViewClusters.main(this.makeViewClustersParameters(MUTIC_DATASET,MUTIC_OUTPUT,MUTIC_IMAGE,ViewClusters.MONO_COLORS));
               ClusteringTransformer.main(this.makeClusteringTransformParameters(this.enhancedDatasetPath,ClusteringTransformer.MOBRA,this.mobraDatasetPath));
               Clusterize.main(this.makeClusteringParameters(MOBRA_DATASET,this.modelClusteringMetric,MOBRA_OUTPUT));
               SimpleGraph.makeGraph(this);
               ViewClusters.main(this.makeViewClustersParameters(MOBRA_DATASET,MOBRA_OUTPUT,MOBRA_IMAGE,ViewClusters.MONO_COLORS));
        }

}
