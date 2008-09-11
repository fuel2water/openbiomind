package task;

import java.io.*; 
import java.util.*; 

import dataset.Dataset;
import dataset.FeatureRanking;
import dataset.FoldHolder;
import dataset.GPRanking;

public class RecursiveFeatureElim {
	private static final Set<String> mandatoryOptions = new HashSet<String>();
	private static final Set<String> optionalOptions = new HashSet<String>();
	private static final String INPUT_DATASET_OPTION = "-d";
	private static final String OUTPUT_DIRECTORY_OPTION = "-o"; 
	private static final String INPUT_FOLDDIRECTORY_OPTION = "-f"; 
	private static final String INPUT_TRIALNUM_OPTION ="-n"; 
	private static final String INPUT_SELECTIONCUTOFF_OPTION = "-e"; 
	
	static {
		mandatoryOptions.add(INPUT_DATASET_OPTION);
		mandatoryOptions.add(OUTPUT_DIRECTORY_OPTION);
		mandatoryOptions.add(INPUT_FOLDDIRECTORY_OPTION); 
		optionalOptions.add("-"+PipelineParameters.TARGET_CATEGORY_PROPERTY);
		optionalOptions.add("-"+PipelineParameters.NUMBER_OF_TASKS_PROPERTY);
		optionalOptions.add("-"+PipelineParameters.CLASSIFICATION_METHOD_PROPERTY); 
		optionalOptions.add(INPUT_TRIALNUM_OPTION);
		optionalOptions.add(INPUT_SELECTIONCUTOFF_OPTION); 
	}
	
	private FeatureRanking gpRanking; 
	private float RFEEliminationRate; 
	private String datasetPath;
	private String foldDir;
	private String targetCategory;
	private String outputDir; 
	private int numberOfTasks;
	private int numberOfTrials; 
	private String gpType; 
	private ArrayList<HashMap <String, String>> trialRunResults = new ArrayList<HashMap <String, String>>(); 
	
	FoldHolder foldHolder = null; 
	
	private static int getFeatureCount(FoldHolder foldHolder) {
		return foldHolder.getFolds().get(0).get(0).getFeatures().size();
	}
	
	public static void execute(PipelineParameters parameters) {
		int numberOfTasks = parameters.getNumberOfTasks();
		int numberOfTrials = parameters.getNumberOfTrials(); 
		float RFEEliminationRate = parameters.getRFEEliminationRate(); 
		String datasetPath = parameters.getDatasetPath();
		String foldDir = parameters.getTransformedDatasetPath();  
		String targetCategory = parameters.getTargetCategory(); 
		String outputDir = parameters.getRFEOutputPath();
		String gpType = parameters.getClassificationMethod(); 
		
		RecursiveFeatureElim rfe = new RecursiveFeatureElim(datasetPath, foldDir, outputDir, targetCategory, numberOfTasks, numberOfTrials, RFEEliminationRate, gpType); 
	}
	
	private void singleTrial(int index) throws IOException {
		float lastPerformance = 0; 
		float performance = 0; 
		float bestPerformance = 0; 
		
		String outputTrialDir = outputDir + "/trial"+index; 
		File outputDirHandle = new File(outputTrialDir);
		if (! outputDirHandle.exists())
			outputDirHandle.mkdir();
		
		FileReader reader = null; 
		// set up the foldHolder to hold the folds
		try {
			foldHolder = new FoldHolder(foldDir, targetCategory);
            reader=new FileReader(datasetPath);
		} catch (IOException e) {
			System.err.println("Unable to load datasets from the data dir."); 
		}
        Dataset dataset=new Dataset(reader,targetCategory);
		int numberOfFeatures = getFeatureCount(foldHolder); 
		int lastNumberOfFeatures = 0; 
		int bestNumberOfFeatures = 0; 
		
		int round = 1; 
		int retrials = 0; 
		
		FoldHolder lastFoldHolder = null;
		
		while ((performance >= lastPerformance) || (retrials < 2)) {

			
			// set up a GPRanking
			System.out.println("Running recursive feature elimination round " + round + ": " + getFeatureCount(foldHolder) + " features ..."); 
			
			// blank out excessive output
			OutputStream stream = new ByteArrayOutputStream();
			PrintStream out = new PrintStream(stream); 
			PrintStream origOut = System.out;
			System.setOut(out); 
			
			numberOfFeatures = (int) (getFeatureCount(foldHolder) * RFEEliminationRate); 
			gpRanking = new GPRanking(numberOfFeatures, dataset, foldHolder, outputTrialDir+"/"+Integer.toString(round), "", numberOfTasks, gpType);
			
			lastPerformance = performance;
			performance = ((GPRanking)(gpRanking)).getTrainPerformance();
			
			// revert back to system output
			
			if ( (performance >= lastPerformance) && (numberOfFeatures != lastNumberOfFeatures) ) {

				// now re-transform the dataset with the reduced feature-set and save the results
				lastFoldHolder = foldHolder; 
				DatasetTransformer transformer=new DatasetTransformer(foldHolder.getFolds(), gpRanking);
		        foldHolder=transformer.transform();
		       
		        
		        System.setOut(origOut);
				System.out.println("Round " + round + " completed; Average GP Performance: " + performance);
				
		        String transfromedFoldDir = outputTrialDir+"/"+Integer.toString(round+1) +"-transformed"; 
		        File tester=new File(transfromedFoldDir);
		        tester.mkdir();
		        try {
		            foldHolder.saveIn(transfromedFoldDir);
		        }
		        catch (IOException e){
		              System.err.println("Unable to save transformed folds.");
		              e.printStackTrace(); 
		        }
		        
				round++; 
		        retrials = 0; 
		        
		        bestPerformance = performance; 
				lastNumberOfFeatures = numberOfFeatures;
				bestNumberOfFeatures = numberOfFeatures; 
				
			} else  {
				System.setOut(origOut); 
				
				if (performance < lastPerformance) { 
					System.out.println("This round's performance: " + performance + " is not higher than previous performance");
					// the standard-bearer of performance is not the current performance but the last performance
					performance = lastPerformance;
					lastPerformance = 1;  // force recursion
					numberOfFeatures = 0; 
				} else { 
					System.out.println("This round's feature-set: " + numberOfFeatures + " is not reduced from that of the previous round");
				
				}
				
				retrials++;
				
				System.out.println("Backtrack and re-try the last round, re-trial (" + retrials +") ...");
				foldHolder = lastFoldHolder; 
			}
			
			
		}
		
		System.out.println("Recursive feature elimination is completed.");
		System.out.println("The best performance is recored by RFE trial round " + Integer.toString(round-1));
		System.out.println("You can find the selected genes by this round in " + outputTrialDir + "/" + (round-1) + ".txt" + " and the transformed dataset in " + outputTrialDir + "/" + (round-1) + "-transformed/"); 
		
		// also save the results to trialRunResults
		HashMap<String, String> trialMap = new HashMap<String, String>();
		System.out.println("best performance: " + bestPerformance); 
		trialMap.put("performance", Float.toString(bestPerformance));
		trialMap.put("round", outputTrialDir + "/" + (round-1) + ".txt"); 
		trialMap.put("feature", String.valueOf(bestNumberOfFeatures)); 
		trialRunResults.add(trialMap); 
	}
	
    /**
     * Executes the metatask.
     */
	public void execute() {
		try {
			File outputDirHandle = new File(outputDir);
			if (! outputDirHandle.exists())
				outputDirHandle.mkdir();
			
			for (int index=0;index<numberOfTrials;index++) {
				System.out.println("RFE Trial#" + index +":"); 
				singleTrial(index); 
			}
			
			// display the final results
			if (numberOfTrials > 1) {
				float bestResult = 0;
				String bestPath = ""; 
				int bestFeatures = 0; 
				
				for (int i=0;i<numberOfTrials;i++) {
					float currentResult = Float.valueOf(trialRunResults.get(i).get("performance"));
					
					if (currentResult > bestResult) {
						bestResult = currentResult;
						bestPath = trialRunResults.get(i).get("round"); 
						bestFeatures = Integer.valueOf(trialRunResults.get(i).get("feature")); 
					} else if (currentResult == bestResult) {
						int currentFeatures = Integer.valueOf(trialRunResults.get(i).get("feature")); 
						
						if (currentFeatures < bestFeatures) {
							bestPath = trialRunResults.get(i).get("round");
							bestFeatures = currentFeatures; 
						}
					}
				}
				
				System.out.println("The best feature set is found in " + bestPath + ". Its performance was: " + bestResult + ". Its feature-set size was: " + bestFeatures); 
			}
			
		} catch (IOException e) {
			System.err.println("There was an error either writing to file/folder during the RFE trial(s).");
            System.exit(-1);
		}
		
	}
	
	public RecursiveFeatureElim(String datasetPath, String foldDir, String outputDir, String targetCategory, int numberOfTasks, int numberOfTrials, float RFEEliminationRate, String gpType) {
		this.datasetPath = datasetPath;
		this.foldDir = foldDir;
		this.outputDir = outputDir;
		this.RFEEliminationRate = RFEEliminationRate; 
		this.targetCategory = targetCategory;
		this.numberOfTasks = numberOfTasks;
		this.numberOfTrials = numberOfTrials; 
		this.gpType = gpType; 
		
		execute(); 
	}
	
	public static void main(String[] args) {
		OptionManager options = new OptionManager(mandatoryOptions, optionalOptions, args); 
		boolean approved = true;
		String errors = options.makeErrorMessages();
		
		if (!errors.equals("")){
			System.err.println(errors);
			approved=false; 
		}
		if (!approved) {
			System.err.println("Usage: java task.RecursiveFeatureElim <-f fold directory> <-d base dataset> <-o outpath> [-" +
								PipelineParameters.NUMBER_OF_TASKS_PROPERTY+" number of tasks] [-"+
								PipelineParameters.TARGET_CATEGORY_PROPERTY+" case category] [-"+
								PipelineParameters.CLASSIFICATION_METHOD_PROPERTY+" "+MetaTask.methodsToString()+ "] [" +
								INPUT_TRIALNUM_OPTION + " number of trials] [" + 
								INPUT_SELECTIONCUTOFF_OPTION + " cut off percentage]"
								); 
			return; 
		}
		
		Properties properties=new Properties(); 
		
		try {
			InputStream inStream=ClassLoader.getSystemResourceAsStream(CompletePipeline.PIPELINE_PROPERTIES_FILE);
			
			properties.load(inStream);
			inStream.close();
		}
		catch (IOException e) {
			System.err.println("Error loading properties file.");
		}
		for (String option:options.getOptionalSet()) {
			if (options.containsOption(option)) {
				properties.setProperty(option.replace("-",""), options.getOption(option)); 
			}
		}
		
		PipelineParameters parameters = new PipelineParameters(properties, args); 
		
		parameters.setDatasetPath(options.getOption(INPUT_DATASET_OPTION)); 
		parameters.setTransformedDatasetPath(options.getOption(INPUT_FOLDDIRECTORY_OPTION));
		parameters.setRFEOutputPath(options.getOption(OUTPUT_DIRECTORY_OPTION));
		parameters.setNumberOfTrials(options.getOption(INPUT_TRIALNUM_OPTION));
		parameters.setRFEEliminationRate(options.getOption(INPUT_SELECTIONCUTOFF_OPTION)); 
		execute(parameters); 
    }
}
