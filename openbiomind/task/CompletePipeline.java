package task;

import java.io.*;
import java.util.*;

/**
 * Runs the whole pipeline commonly applied to a categorial dataset - dataset extension, metatask, utility computation,
 * horizontal, vertical, MUTIC and MOBRA clusterings.
 * @author Lucio
 *
 */
class CompletePipeline{
    
      public static final String PIPELINE_PROPERTIES_FILE="pipeline.properties";
      private static final Set<String> mandatoryOptions=new HashSet<String>();
      private static final Set<String> optionalOptions=new HashSet<String>();
      public static final String INPUT_DATASET_OPTION="-d";
      public static final String TEST_DATASET_OPTION="-dt";
      public static final String OUTPUT_DATASET_OPTION="-o";
      public static final String OTHER_PROPERTY_FILE_OPTION="-p";
    
      static {
             mandatoryOptions.add(INPUT_DATASET_OPTION);
             mandatoryOptions.add(OUTPUT_DATASET_OPTION);
             optionalOptions.add(TEST_DATASET_OPTION);
             optionalOptions.add(OTHER_PROPERTY_FILE_OPTION);
             
             Properties properties=new Properties();
             
             try {
                   
                 InputStream inStream=null;
                     
                 inStream=ClassLoader.getSystemResourceAsStream(CompletePipeline.PIPELINE_PROPERTIES_FILE);
                 properties.load(inStream);
                 inStream.close();
             }
             catch (IOException e){
                   System.err.println("Error loading pipeline properties file.");
             }
             for (Object p:properties.keySet()){
                 optionalOptions.add("-"+((String)p));
             }
      }
      
      public static void main(String[] args){
          
             OptionManager options=new OptionManager(mandatoryOptions,optionalOptions,args);
             boolean approved=true;
             String errors=options.makeErrorMessages();
             
             if (!errors.equals("")){
                System.out.println(errors);
                approved=false;
             }
             if (!approved){
                System.out.println("Usage: java task.CompletePipeline <-d dataset file> [-dt test dataset] <-o output file> [-p alternate properties file] [property options]");
                System.exit(0);
             }
             
             Properties properties=new Properties();
             
             try {
                   
                 InputStream inStream=null;
                     
                 if (options.containsOption(OTHER_PROPERTY_FILE_OPTION)){
                    inStream=ClassLoader.getSystemResourceAsStream(OTHER_PROPERTY_FILE_OPTION);
                 }
                 else {
                      inStream=ClassLoader.getSystemResourceAsStream(CompletePipeline.PIPELINE_PROPERTIES_FILE);
                 }
                     
                 properties.load(inStream);
                 inStream.close();
             }
             catch (IOException e){
                   System.err.println("Error loading pipeline properties file.");
             }
             properties.setProperty(PipelineParameters.INPUT_DATASET_PROPERTY,options.getOption(CompletePipeline.INPUT_DATASET_OPTION));
             if (options.containsOption(CompletePipeline.TEST_DATASET_OPTION)){
                properties.setProperty(PipelineParameters.TEST_DATASET_PROPERTY,options.getOption(CompletePipeline.TEST_DATASET_OPTION));
                properties.setProperty(PipelineParameters.IS_FOLDED_PROPERTY,"false");
             }
             properties.setProperty(PipelineParameters.OUTPUT_PATH_PROPERTY,PipelineParameters.getOption(args,"-o"));
             for (Object p:properties.keySet()){
                 if (options.containsOption("-"+((String)p))){
                    properties.setProperty((String)p,options.getOption("-"+p));
                 }
             }
          
             PipelineParameters pipelineParameters=new PipelineParameters(properties,args);
             
             pipelineParameters.execute();
      }
}