package task;

import java.io.*;
import java.util.*;

import dataset.*;
import snps.SNPFoldelizer;

/**
 * "Foldelizes" and feature-selects a SNP dataset, saving it in numeric format.
 * @author Lucio
 *
 */
class FoldSelectSNPs{
    
      private static final Set<String> mandatoryOptions=new HashSet<String>();
      private static final Set<String> optionalOptions=new HashSet<String>();
      private static final String INPUT_DATASET_OPTION="-d";
      private static final String OUTPUT_DIRECTORY_OPTION="-o";
    
      static {
             mandatoryOptions.add(INPUT_DATASET_OPTION);
             mandatoryOptions.add(OUTPUT_DIRECTORY_OPTION);
             optionalOptions.add("-"+PipelineParameters.TARGET_CATEGORY_PROPERTY);
             optionalOptions.add("-"+PipelineParameters.NUMBER_OF_SELECTED_FEATURES_PROPERTY);
             optionalOptions.add("-"+PipelineParameters.SNP_SELECTION_SHUFFLE);
             optionalOptions.add("-"+PipelineParameters.NUMBER_OF_FOLDS_PROPERTY);
      }
      
      public static void main(String[] args){
          
             OptionManager options=new OptionManager(mandatoryOptions,optionalOptions,args);
             String errors=options.makeErrorMessages();
             
             if (!errors.equals("")){
                System.out.println(errors);
                System.out.println("Usage: java task.FoldSelectSNPs <-d snp dataset> <-o outpath> [-"+
                        PipelineParameters.TARGET_CATEGORY_PROPERTY+" case category] [-"+
                        PipelineParameters.NUMBER_OF_SELECTED_FEATURES_PROPERTY+" number of selected SNPs] [-"+
                        PipelineParameters.NUMBER_OF_FOLDS_PROPERTY+" number of folds] [-"+
                        PipelineParameters.SNP_SELECTION_SHUFFLE+" on|off]");
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
             properties.setProperty(PipelineParameters.IS_FEATURE_SELECTED_PROPERTY,"true");
             for (String option:options.getOptionalSet()){
                 if (options.containsOption(option)){
                    properties.setProperty(option.replace("-",""),options.getOption(option));      
                 }
             }
             
             PipelineParameters parameters=new PipelineParameters(properties,args);

             
             parameters.setDatasetPath(options.getOption(INPUT_DATASET_OPTION));
             parameters.setTransformedDatasetPath(options.getOption(OUTPUT_DIRECTORY_OPTION));
             String infile=parameters.getDatasetPath();
             System.out.println("Infile: "+infile);
             int nFolds=parameters.getNumberOfFolds();
             System.out.println("nFolds: "+nFolds);
             int nFeatures=parameters.getNumberOfSelectedFeatures();
             System.out.println("nFeatures: "+nFeatures);
             String outdir=parameters.getTransformedDatasetPath();
             System.out.println("outdir: "+outdir);
             String targetCategory=parameters.getTargetCategory();
             System.out.println("targetCategory: "+targetCategory);
             boolean shuffle=parameters.getSNPSelectionShuffle();
             System.out.println("shuffle: "+shuffle);
             SNPFoldelizer foldelizer=null;
             
             try {
                 FileReader reader=new FileReader(infile);
                 
                 foldelizer=new SNPFoldelizer(reader,nFolds,nFeatures,targetCategory,shuffle);
                 reader.close();
             }
             catch (IOException e){
                   System.out.println("Error loading input file "+infile);
             }
             try {
                 
                 File checker=new File(outdir);
                 
                 if (!checker.exists()){
                    checker.mkdir();
                 }
                 
                 List<List<Dataset>> folds=foldelizer.getFolds();
                 List<Dataset> testList=new ArrayList<Dataset>();
                 
                 for (int i=0;i<folds.size();i++){
                     
                     List<Dataset> fold=folds.get(i);
                     FileWriter trainWriter=new FileWriter(outdir+"/"+FoldHolder.TRAIN_PREFIX+i+FoldHolder.EXTENSION);
                     
                     fold.get(0).write(trainWriter);
                     trainWriter.close();
                     
                     FileWriter testWriter=new FileWriter(outdir+"/"+FoldHolder.TEST_PREFIX+i+FoldHolder.EXTENSION);
                     
                     fold.get(1).write(testWriter);
                     testWriter.close();
                     testList.add(fold.get(1));
                 }
                 
                 FileWriter baseWriter=new FileWriter(outdir+"/base_dataset"+FoldHolder.EXTENSION);
                 
                 foldelizer.getBaseDataset().write(baseWriter);
                 baseWriter.close();
             }
             catch (IOException e){
                   System.out.println("Error saving output files at directory "+outdir);
             }
      }
    
}