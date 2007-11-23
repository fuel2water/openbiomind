package task;

import java.io.*;
import java.util.*;

import classification.*;
import classification.evolution.*;
import classification.local.*;
import classification.evolution.snps.*;
import classification.evolution.gp.conventional.ConventionalEvolutionParametersFactory;
import classification.evolution.gp.moses.*;
import dataset.*;
import util.*;



class MetaTask{

      private static final Set<String> mandatoryOptions=new HashSet<String>();
      private static final Set<String> optionalOptions=new HashSet<String>();
      private static final String INPUT_DATASET_OPTION="-d";
      private static final String OUTPUT_DIRECTORY_OPTION="-o";
    
      static {
             mandatoryOptions.add(INPUT_DATASET_OPTION);
             mandatoryOptions.add(OUTPUT_DIRECTORY_OPTION);
             optionalOptions.add("-"+PipelineParameters.TARGET_CATEGORY_PROPERTY);
             optionalOptions.add("-"+PipelineParameters.NUMBER_OF_TASKS_PROPERTY);
             optionalOptions.add("-"+PipelineParameters.CLASSIFICATION_METHOD_PROPERTY);
      }
      
	  private static final Map<String,TrainerFactory> s2epf=new HashMap<String,TrainerFactory>();
	
	  static {
		     //s2epf.put("mosesdirect",MosesDirectEvolutionParametersFactory.getInstance());
             s2epf.put("boolsimple",new EvolverFactory(MosesLikeEvolutionParametersFactory.getInstance()));
             s2epf.put("boolcomplex",new EvolverFactory(BoolComplexEvolutionParametersFactory.getInstance()));
             s2epf.put("conventional",new EvolverFactory(ConventionalEvolutionParametersFactory.getInstance()));
             s2epf.put("snp",new EvolverFactory(SNPEvolutionParametersFactory.getInstance()));
             s2epf.put("snpga",new EvolverFactory(SNPGAEvolutionParametersFactory.getInstance()));
             s2epf.put("snplocal",new SNPLocalFactory());
	  }
	
      public static TrainerFactory getEvolutionParametersFactory(String label){
             return s2epf.get(label);
      }
      
      private boolean completed;
      private int numberOfTasks;
      private ArrayList<ArrayList<Dataset>> folds;
      private String outputPath;
      private List<Ensemble> exf=new ArrayList<Ensemble>();
      private TrainerFactory trainerFab;
      
      public MetaTask(ArrayList<ArrayList<Dataset>> folds,int numberOfTasks,String outputPath,TrainerFactory trainerFab){
             completed=false;
             this.numberOfTasks=numberOfTasks;
             this.folds=folds;
             this.outputPath=outputPath;
             this.trainerFab=trainerFab;
      }

      /**
       * Executes a single task and dumps results.
       * @param index
       * @throws IOException
       */
      private void singleTask(int index) throws IOException{
                  
              DoubleWriter outfile=new DoubleWriter(outputPath+"/out"+index+".txt");
              ConfusionMatrix globalTrain=new ConfusionMatrix();
              ConfusionMatrix globalTest=new ConfusionMatrix();
      
              for (int i=0;i<folds.size();i++){
            	  
            	  List<Dataset> fold=folds.get(i);
            	  
                  Dataset train=fold.get(0);
                  trainerFab.setDataset(train);
      
                  Trainer trainer=trainerFab.makeTrainer();
              
                  Ensemble classifier=trainer.train(train);
                  
                  outfile.write("Fold #"+i+":");
                  outfile.newLine();
                  outfile.write("Ensemble:");
                  outfile.newLine();
                  classifier.write(outfile);
                  exf.get(folds.indexOf(fold)).smartAdd(classifier,train.getEntities());
                  outfile.write("Train Matrix: ");
                  outfile.newLine();
                  
                  ConfusionMatrix foldTrain=new ConfusionMatrix(classifier,train.getEntities());
                  
                  foldTrain.write(outfile);
                  globalTrain.add(foldTrain);
                  outfile.write("Test Matrix: ");
                  outfile.newLine();
                  
                  ConfusionMatrix foldTest=new ConfusionMatrix(classifier,fold.get(1).getEntities());
                  
                  foldTest.write(outfile);
                  globalTest.add(foldTest);
              }
              outfile.write("Global Train: ");
              outfile.newLine();
              globalTrain.write(outfile);
              outfile.write("Global Test: ");
              outfile.newLine();
              globalTest.write(outfile);
              outfile.close();
      }
      
      private void dumpFinalResults() throws IOException{
      
              DoubleWriter outfile=new DoubleWriter(outputPath+"/outfinal.txt");
              ConfusionMatrix globalTrain=new ConfusionMatrix();
              ConfusionMatrix globalTest=new ConfusionMatrix();
              
              for (int i=0;i<folds.size();i++){
                  outfile.write("Fold "+i+":\n");
                  outfile.write("Ensemble:\n");
                  exf.get(i).write(outfile);
                  
                  ConfusionMatrix localTrain=new ConfusionMatrix(exf.get(i),folds.get(i).get(0).getEntities());
                  
                  outfile.write("Train Matrix:\n");
                  localTrain.write(outfile);
                  globalTrain.add(localTrain);

                  ConfusionMatrix localTest=new ConfusionMatrix(exf.get(i),folds.get(i).get(1).getEntities());
                  
                  outfile.write("Test Matrix:\n");
                  localTest.write(outfile);
                  globalTest.add(localTest);
              }
              outfile.write("Global Train Matrix:\n");
              globalTrain.write(outfile);
              outfile.write("Global Test Matrix:\n");
              globalTest.write(outfile);
              outfile.close();
      }

      /**
       * Executes the metatask.
       * @throws IOException
       */
      public void execute() throws IOException{
             if (completed){
                return;
             }
             for (int i=0;i<folds.size();i++){
                 exf.add(new Ensemble());
             }
             
             File tester=new File(this.outputPath);
             
             if (!tester.exists()){
            	tester.mkdir();
             }
             for (int i=0;i<numberOfTasks;i++){
                 singleTask(i);
             }
             dumpFinalResults();
             completed=true;
      }
      
      /**
       * Computes a string of option values for each classification method.
       * @return
       */
      public static String methodsToString(){
          
             StringBuffer buffer=new StringBuffer();
             
             for (String method:s2epf.keySet()){
                 buffer.append(" ");
                 buffer.append(method);
             }
             return buffer.toString().trim().replace(" ","|");
      }
      
      /**
       * Executes the metatask using the given parameter package.
       * @param parameters
       */
      public static void execute(PipelineParameters parameters){

             int numberOfTasks=parameters.getNumberOfTasks();
             //System.out.println(numberOfTasks);
             String datasetDir=parameters.getTransformedDatasetPath();
             //System.out.println(datasetDir);
             String targetCategory=parameters.getTargetCategory();
             FoldHolder foldHolder=null;
             
             try {
                 foldHolder=new FoldHolder(datasetDir,targetCategory);
             }
             catch (IOException e){
                   System.err.println("Unable to load datasets from the data dir.");
             }
             String outputDir=parameters.getMetataskOutputPath();
             String gpType=parameters.getClassificationMethod();
             
             MetaTask runner=new MetaTask(foldHolder.getFolds(),numberOfTasks,outputDir,s2epf.get(gpType));
             
             try {
                 runner.execute();
             }
             catch (IOException e){
                   System.err.println("Error while dumping results.");
                   e.printStackTrace();
             }
      }
      
      /**
       * Command-line triggering of a metatask.
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
                System.out.println("Usage: java task.MetaTask <-d dataset directory> <-o outpath> [-"+
                        PipelineParameters.NUMBER_OF_TASKS_PROPERTY+" number of tasks] [-"+
                        PipelineParameters.TARGET_CATEGORY_PROPERTY+" case category] [-"+
                        PipelineParameters.CLASSIFICATION_METHOD_PROPERTY+" "+methodsToString()+"]");
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
             
             parameters.setTransformedDatasetPath(options.getOption(INPUT_DATASET_OPTION));
             parameters.setMetataskOutputPath(options.getOption(OUTPUT_DIRECTORY_OPTION));
             execute(parameters);
      }

}