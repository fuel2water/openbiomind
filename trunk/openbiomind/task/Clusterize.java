package task;

import java.io.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import clustering.*;

/**
 * "Class-command" for clusterization.
 * @author Lucio
 *
 */
public class Clusterize{
    
       private static final Set<String> mandatoryOptions=new HashSet<String>();
       private static final Set<String> optionalOptions=new HashSet<String>();
       public static final String CLUSTERING_DATASET_OPTION="-d";
       public static final String OUTPUT_FILE_OPTION="-o";
    
       static {
              mandatoryOptions.add(CLUSTERING_DATASET_OPTION);
              mandatoryOptions.add(OUTPUT_FILE_OPTION);
              optionalOptions.add("-"+PipelineParameters.DATASET_CLUSTERING_METRIC_PROPERTY);
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
                 System.err.println("Usage: java task.Clusterize <-d clustering dataset> <-o output file> [-"+
                                    PipelineParameters.DATASET_CLUSTERING_METRIC_PROPERTY+" "+SimilarityMetricFactory.COSINE_NAME+"|"+SimilarityMetricFactory.EUCLIDIAN_NAME+"|"+SimilarityMetricFactory.SNP_NAME+"]");
                 System.exit(0);
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
             
             OmniClustering clustering=null;

             try {
                  
                 FileReader reader=new FileReader(options.getOption(CLUSTERING_DATASET_OPTION));
                 ClusteringDataset data=new ClusteringDataset(reader);

                 clustering=new OmniClustering(data,SimilarityMetricFactory.getInstance().makeSimilarityMetric(properties.getProperty(PipelineParameters.DATASET_CLUSTERING_METRIC_PROPERTY)));
                 reader.close();
             }
             catch (Exception e){
                   System.err.println("Unable to load clustering data.");
                   e.printStackTrace();
             }
              
             try {
                  
                 FileWriter writer=new FileWriter(options.getOption(OUTPUT_FILE_OPTION));
              
                 clustering.write(writer);
                 writer.close();
             }
             catch (IOException e){
                   System.err.println("Unable to save clustering results.");
             }
       }
}