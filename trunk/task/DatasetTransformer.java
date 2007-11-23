package task;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import dataset.*;

/**
 * Applies feature selection and "foldelization" to a given dataset.
 * @author Lucio
 *
 */
class DatasetTransformer{

      private static final Set<String> mandatoryOptions=new HashSet<String>();
      private static final Set<String> optionalOptions=new HashSet<String>();
      private static final String INPUT_DATASET_OPTION="-d";
      private static final String OUTPUT_DIRECTORY_OPTION="-o";
    
      static {
             mandatoryOptions.add(INPUT_DATASET_OPTION);
             mandatoryOptions.add(OUTPUT_DIRECTORY_OPTION);
             optionalOptions.add("-"+PipelineParameters.TARGET_CATEGORY_PROPERTY);
             optionalOptions.add("-"+PipelineParameters.NUMBER_OF_FOLDS_PROPERTY);
             optionalOptions.add("-"+PipelineParameters.TEST_DATASET_PROPERTY);
             optionalOptions.add("-"+PipelineParameters.NUMBER_OF_SELECTED_FEATURES_PROPERTY);
             optionalOptions.add("-"+PipelineParameters.FEATURE_SELECTION_METHOD_PROPERTY);
      }
      
      private ArrayList<ArrayList<Dataset>> folds;
      private FeatureRanking featureRanking;
	
      /**
       * Simple "parameter grabber" constructor.
       * @param folds
       * @param featureRanking
       */
      public DatasetTransformer(ArrayList<ArrayList<Dataset>> folds,FeatureRanking featureRanking){
    	     this.folds=folds;
    	     this.featureRanking=featureRanking;
      }
      
      /**
       * Applies feature selection over folds. Determination of selected features is performed using only the training set.
       *
       */
      public FoldHolder transform(){
             for (int i=0;i<folds.size();i++){
             
            	 List<Dataset> fold=folds.get(i);
                 List<String> features=featureRanking.rankingFor(fold.get(0));
                 
                 fold.get(0).select(features);
                 fold.get(1).select(features);
             }
             return new FoldHolder(folds);
	  }
	
      /**
       * Execution flow.
       * @param parameters
       */
      public static void execute(PipelineParameters parameters){
          
             String targetCategory=parameters.getTargetCategory();
             Dataset dataset1=new Dataset();
             
             try {
                 
                 FileReader reader=new FileReader(parameters.getDatasetPath());
                 
                 dataset1=new Dataset(reader,targetCategory);
                 System.out.printf("%d cases, %d controls\n",dataset1.numberOf(true),dataset1.numberOf(false));
             }
             catch (IOException e){
                   System.err.println("Error while loading training dataset");
             }
             
             int numberOfFolds=0;
             ArrayList<ArrayList<Dataset>> folds;
             
             if (parameters.isFolded()){
                numberOfFolds=parameters.getNumberOfFolds();
                folds=dataset1.foldelize(numberOfFolds);
             }
             else {
                 //System.out.println("Train and test");
             
                Dataset dataset2=new Dataset();
             
                try {
                    
                    FileReader reader=new FileReader(parameters.getTestDatasetPath());
                    
                    dataset2=new Dataset(reader,targetCategory);
                }
                catch (IOException e){
                      System.err.println("Error while loading test dataset");
                }

                ArrayList<Dataset> singleFold=new ArrayList<Dataset>();
                
                singleFold.add(dataset1);
                singleFold.add(dataset2);
                folds=new ArrayList<ArrayList<Dataset>>();
                folds.add(singleFold);
             }
             
             FoldHolder foldHolder=new FoldHolder(folds);
             
             if (parameters.isFeatureSelected()){

                int numberOfSelectedFeatures=parameters.getNumberOfSelectedFeatures();
                String selectionMethod=parameters.getFeatureSelectionMethod();
                FeatureRanking featureRanking=null;
             
                if (selectionMethod.equals("differentiation")){
                   featureRanking=new DifferentiationRanking(numberOfSelectedFeatures);
                }
                else {
                     featureRanking=new SAMRanking(numberOfSelectedFeatures);
                }
                
                DatasetTransformer transformer=new DatasetTransformer(folds,featureRanking);
             
                foldHolder=transformer.transform();
             }
             
             String outdir=parameters.getTransformedDatasetPath();
             File tester=new File(outdir);
             
             if (!tester.exists()){
                tester.mkdir();
             }
             else {
                  if (!tester.isDirectory()){
                     System.out.println("There is already a file called "+outdir);
                     return;
                  }
             }
             
             try {
                 foldHolder.saveIn(outdir);
             }
             catch (IOException e){
                   System.err.println("Unable to save transformed folds.");      
             }
      }
      
      /**
       * Command-like main function.
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
             if (options.containsOption("-"+PipelineParameters.NUMBER_OF_FOLDS_PROPERTY)&&options.containsOption("-"+PipelineParameters.TEST_DATASET_PROPERTY)){
                System.out.println("Mutually exclusive options -"+PipelineParameters.NUMBER_OF_FOLDS_PROPERTY+" and -"+PipelineParameters.TEST_DATASET_PROPERTY+" used");
                approved=false;
             }
             if (!approved){
                System.out.println("Usage: java task.DatasetTransformer <-d dataset> <-o output dir> [-"+
                                   PipelineParameters.TARGET_CATEGORY_PROPERTY+" category] [-"+
                                   PipelineParameters.NUMBER_OF_FOLDS_PROPERTY+" folds|-"+
                                   PipelineParameters.TEST_DATASET_PROPERTY+" test dataset] [-"+
                                   PipelineParameters.NUMBER_OF_SELECTED_FEATURES_PROPERTY+" nf] [-"+
                                   PipelineParameters.FEATURE_SELECTION_METHOD_PROPERTY+" differentiation|SAM]");
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
             if (options.containsOption("-"+PipelineParameters.TEST_DATASET_PROPERTY)){
                properties.setProperty(PipelineParameters.IS_FOLDED_PROPERTY,"false");
             }
             if (options.containsOption("-"+PipelineParameters.FEATURE_SELECTION_METHOD_PROPERTY)||options.containsOption("-"+PipelineParameters.NUMBER_OF_SELECTED_FEATURES_PROPERTY)||options.containsOption("-"+PipelineParameters.TARGET_CATEGORY_PROPERTY)){
                properties.setProperty(PipelineParameters.IS_FEATURE_SELECTED_PROPERTY,"true");
             }
             for (String option:options.getOptionalSet()){
                 if (options.containsOption(option)){
                    properties.setProperty(option.replace("-",""),options.getOption(option));      
                 }
             }
             
             PipelineParameters parameters=new PipelineParameters(properties,args);
             parameters.setDatasetPath(options.getOption(INPUT_DATASET_OPTION));
             parameters.setTransformedDatasetPath(options.getOption(OUTPUT_DIRECTORY_OPTION));
             execute(parameters);
	  }
}