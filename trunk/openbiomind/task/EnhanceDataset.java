package task;

import java.io.*;
import java.util.*;

import dataset.*;

/**
 * Enhances a dataset with gene-category information from Gene Ontology.
 * @author Lucio
 *
 */
class EnhanceDataset{
    
      private static final Set<String> mandatoryOptions=new HashSet<String>();
      private static final Set<String> optionalOptions=new HashSet<String>();
      private static final String INPUT_DATASET_OPTION="-d";
      private static final String OUTPUT_DATASET_OPTION="-e";
    
      static {
             mandatoryOptions.add(INPUT_DATASET_OPTION);
             mandatoryOptions.add(OUTPUT_DATASET_OPTION);
             optionalOptions.add("-"+PipelineParameters.ONTOLOGY_ASSOCIATION_FILE_PROPERTY);
             optionalOptions.add("-"+PipelineParameters.ONTOLOGY_DESCRIPTION_FILE_PROPERTY);
      }
      
      public static void execute(PipelineParameters parameters){
          
             OntologyStorer ontologyStorer=null;
             
             try {
                 
                 String oboFileName=parameters.getOntologyDescriptionFile();
                 InputStream descriptionStream=ClassLoader.getSystemResourceAsStream(oboFileName);
                 
                 if (descriptionStream==null){
                    descriptionStream=new FileInputStream(oboFileName);
                 }
                 
                 InputStreamReader descriptionReader=new InputStreamReader(descriptionStream);
                 //FileReader descriptionReader=new FileReader(oboFileName);
                 String associationFileName=parameters.getOntologyAssociationFile();
                 
                 InputStream associationStream=ClassLoader.getSystemResourceAsStream(associationFileName);
                 
                 if (associationStream==null){
                     associationStream=new FileInputStream(associationFileName);
                 }
                 InputStreamReader associationReader=new InputStreamReader(associationStream);
                 //FileReader associationReader=new FileReader(associationFileName);
                 
                 ontologyStorer=new OntologyStorer(descriptionReader,associationReader);
                 descriptionReader.close();
                 associationReader.close();
             }
             catch (IOException e){
                   System.err.println("Error while loading Gene Ontology information");
             }
             
             Dataset original=null;
             
             try {
                 
                 String datasetFileName=parameters.getDatasetPath();
                 FileReader reader=new FileReader(datasetFileName);
                 
                 original=new Dataset(reader,"");
                 reader.close();
             }
             catch (IOException e){
                   System.err.println("Error while loading original dataset.");
             }
             
             Dataset enhanced=ontologyStorer.makeEnhanced(original);
             
             try {
                 
                 String outputFile=parameters.getEnhancedDatasetPath();
                 FileWriter writer=new FileWriter(outputFile);
                 
                 enhanced.write(writer);
                 writer.close();
             }
             catch (IOException e){
                   System.err.println("Error while writing enhanced dataset.");
             }
      }
      
      /**
       * Command-line oriented main function.
       */
      public static void main(String[] args){
          
             OptionManager options=new OptionManager(mandatoryOptions,optionalOptions,args);
             boolean approved=true;
             String errors=options.makeErrorMessages();
             
             if (!errors.equals("")){
                System.err.println(errors);
                approved=false;
             }
             if (!approved){
                System.err.println("Usage: java task.EnhanceDataset <"+INPUT_DATASET_OPTION+" original dataset> <"+OUTPUT_DATASET_OPTION+" enhanced dataset> [-"+PipelineParameters.ONTOLOGY_DESCRIPTION_FILE_PROPERTY+" ontology description file] [-"+PipelineParameters.ONTOLOGY_ASSOCIATION_FILE_PROPERTY+" ontology association file]");
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
                    properties.setProperty(option,options.getOption(option));      
                 }
             }
             
             PipelineParameters parameters=new PipelineParameters(properties,args);
             parameters.setDatasetPath(options.getOption(INPUT_DATASET_OPTION));
             parameters.setEnhancedDatasetPath(options.getOption(OUTPUT_DATASET_OPTION));
             execute(parameters);
      }

}