package task;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

import clustering.ClusteringDataset;
import dataset.Dataset;

/**
 * Command-class for treating/consisting all existing clustering-related transforms operating on categorial datasets or
 * classification results.
 * @author Lucio
 *
 */
public class ClusteringTransformer{
    
      private static final Set<String> mandatoryOptions=new HashSet<String>();
      private static final Set<String> optionalOptions=new HashSet<String>();
      public static final String INPUT_DATASET_OPTION="-d";
      public static final String OUTPUT_DATASET_OPTION="-o";
      public static final String TRANSFORM_OPTION="-t";
      public static final String METATASK_RESULTS_PATH_OPTION="-p";
    
      static {
             mandatoryOptions.add(INPUT_DATASET_OPTION);
             mandatoryOptions.add(OUTPUT_DATASET_OPTION);
             mandatoryOptions.add(TRANSFORM_OPTION);
             optionalOptions.add(METATASK_RESULTS_PATH_OPTION);
      }
      
      public static String DATASET_HORIZONTAL="horizontal";
      public static String DATASET_VERTICAL="vertical";
      public static String MOBRA="MOBRA";
      public static String MUTIC="MUTIC";
    
      public static void main(String[] args){
          
             //System.out.println("1");
             OptionManager options=new OptionManager(mandatoryOptions,optionalOptions,args);
             boolean approved=true;
             String errors=options.makeErrorMessages();
             
             //System.out.println("2");
             if (!errors.equals("")){
                System.err.println(errors);
                approved=false;
             }
             //System.out.println("3");
             if (approved){
                if (!options.containsOption(METATASK_RESULTS_PATH_OPTION)){
                   if (options.getOption(TRANSFORM_OPTION).equals(MOBRA)||options.getOption(TRANSFORM_OPTION).equals(MUTIC)){
                      System.err.println("MOBRA and MUTIC options are not applicable to categorial datasets. Please inform a metatask output dir using option "+METATASK_RESULTS_PATH_OPTION);
                      approved=false;
                   }
                }
             }
             //System.out.println("4");
             if (!approved){
                System.err.println("Usage: java task.ClusteringTransformer <-d dataset file> <-o output file> <-t transform> [-p metatask results dir]");
                System.exit(-1);
             }
             //System.out.println("5");
             
             String transformName=options.getOption(TRANSFORM_OPTION);
             ClusteringDataset clusteringDataset=null; 
             Dataset dataset=null;
             String fileName=options.getOption(INPUT_DATASET_OPTION);

             //System.out.println("6");            
             try {
                
                 FileReader reader=new FileReader(fileName);
                
                 dataset=new Dataset(reader,"");
                 reader.close();
             }
             catch (IOException e){
                   System.err.println("Error while loading dataset "+fileName);
                   System.exit(-1);
             }
             //System.out.println("7");
                
             if (transformName.equals(DATASET_HORIZONTAL)||transformName.equals(DATASET_VERTICAL)){
                 
                clusteringDataset=new ClusteringDataset(dataset,transformName.equals(DATASET_HORIZONTAL)); 
             }
             //System.out.println("8");
             if (transformName.equals(MOBRA)||transformName.equals(MUTIC)){
                 
                MetaTaskResult result=null;
                String path=options.getOption(METATASK_RESULTS_PATH_OPTION);
                
                try {
                    result=new MetaTaskResult(path);
                }
                catch (IOException e){
                      System.err.println("Error loading metatask results at directory "+path);
                      System.exit(-1);
                }
                
                clusteringDataset=new ClusteringDataset(dataset,result.getTaskResults(),transformName.equals(MOBRA)?ClusteringDataset.ResultTransform.MOBRA:ClusteringDataset.ResultTransform.MUTIC);
             }
             //System.out.println("9");
             if (clusteringDataset==null){
                System.err.println("It seems that you are requesting an invalid transform. Valid transforms are "+DATASET_HORIZONTAL+","+DATASET_VERTICAL+","+MOBRA+" and "+MUTIC);
                System.exit(-1);
                return;
             }
             //System.out.println("10");
             
             String clusterDatasetName=options.getOption(OUTPUT_DATASET_OPTION);
             
             try {
                 
                 FileWriter writer=new FileWriter(clusterDatasetName);
                 
                 clusteringDataset.write(writer);
                 writer.close();
             }
             catch (IOException e){
                   System.err.println("Error while writing clustering dataset "+clusterDatasetName);
                   System.exit(-1);
             }
             //System.out.println("11");
      }
}
