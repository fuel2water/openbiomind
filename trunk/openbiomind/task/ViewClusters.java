package task;

import java.io.*;
import java.util.*;

import clustering.*;

class ViewClusters{
    
      private static final Set<String> mandatoryOptions=new HashSet<String>();
      private static final Set<String> optionalOptions=new HashSet<String>();
      public static final String CLUSTERING_DATASET_OPTION="-d";
      public static final String CLUSTERING_RESULT_OPTION="-r";
      public static final String OUTPUT_FILE_OPTION="-o";
      public static final String CLUSTERING_COLOR_OPTION="-"+PipelineParameters.CLUSTERING_COLOR_PROPERTY;

      public static final String DEFAULT_COLORS="traditional"; 
      public static final String MONO_COLORS="mono"; 
    
      static {
             mandatoryOptions.add(CLUSTERING_DATASET_OPTION);
             mandatoryOptions.add(CLUSTERING_RESULT_OPTION);
             mandatoryOptions.add(OUTPUT_FILE_OPTION);
             optionalOptions.add(CLUSTERING_COLOR_OPTION);
      }
      
      public static void main(String[] args){
          
             OptionManager options=new OptionManager(mandatoryOptions,optionalOptions,args);
             boolean approved=true;
             String errors=options.makeErrorMessages();
             
             if (!errors.equals("")){
                System.err.println(errors);
                approved=false;
             }
             if (!approved){
                System.err.println("Usage: java task.ViewClusters <-d clustering dataset> <-r clustering result> <-o image file> ["+CLUSTERING_COLOR_OPTION+" traditional|mono]");
                System.exit(-1);
             }

             Properties properties=new Properties();
             
             try {
                 InputStream inStream=ClassLoader.getSystemResourceAsStream(CompletePipeline.PIPELINE_PROPERTIES_FILE);
             
                 properties.load(inStream);
                 inStream.close();
             }
             catch (IOException e){
                   System.err.println("Error loading properties file.");
                   System.exit(-1);
             }
             for (String option:options.getOptionalSet()){
                 if (!options.containsOption(option)){
                    options.setOption(option,properties.getProperty(option.replace("-","")));      
                 }
             }
             
             System.out.println("Commencing clustering imaging");

             ClusteringDataset dataset=null;
             
             try {
                 
                 FileReader reader=new FileReader(options.getOption(CLUSTERING_DATASET_OPTION));
                 
                 System.out.println("Loading "+options.getOption(CLUSTERING_DATASET_OPTION));
                 dataset=new ClusteringDataset(reader);
             }
             catch (IOException e){
                   System.err.println("Error while loading clustering dataset"+options.getOption(CLUSTERING_DATASET_OPTION));
                   System.exit(-1);
             }
             
             OmniClustering clustering=null;
             
             try {
                 
                 FileReader reader=new FileReader(options.getOption(CLUSTERING_RESULT_OPTION));
                 
                 System.out.println("Loading "+options.getOption(CLUSTERING_RESULT_OPTION));
                 clustering=new OmniClustering(dataset,reader);
             }
             catch (IOException e){
                   System.err.println("Error while loading ");
                   System.exit(-1);
             }
             System.out.println("Processing image");
             
             ClusterImage image=new ClusterImage(clustering,options.getOption(CLUSTERING_COLOR_OPTION).equals(DEFAULT_COLORS));
             
             try {
                 System.out.println("Saving image as "+options.getOption(OUTPUT_FILE_OPTION));
                 image.writePNG(new File(options.getOption(OUTPUT_FILE_OPTION)));
             }
             catch (IOException e){
                   System.err.println("Error while saving clustering image"+options.getOption(OUTPUT_FILE_OPTION));
                   System.exit(-1);
             }
      }
}