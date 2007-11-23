package task;

import java.io.*;
import java.util.*;

import classification.ConfusionMatrix;
import util.LineTagger;

/**
 * Container for results of classification tasks.
 * @author Lucio
 *
 */
public class ClassificationTaskResult{
	
	  private List<FoldResult> folds=new ArrayList<FoldResult>();
	  private ConfusionMatrix globalTrain;
	  private ConfusionMatrix globalTest;
	
	  /**
       * Loads from a file results of an individual classification task. 
       * @param fileName
       * @throws IOException
	   */
      public ClassificationTaskResult(String fileName) throws IOException{
		  
             LineTagger lines=new LineTagger(fileName);
             
		     for (LineTagger foldBlock:lines.fuzzyLineParse("Fold","Global")){
		    	 folds.add(new FoldResult(foldBlock));
		     }
		     
		     List<LineTagger> globalMatrices=lines.fuzzyLineParse("Global");
		     
		     globalTrain=new ConfusionMatrix(globalMatrices.get(0));
		     globalTest=new ConfusionMatrix(globalMatrices.get(1));
	  }
	  
	  public List<FoldResult> getFolds(){
		     return folds;
	  }
      
      public ConfusionMatrix getGlobalTrainMatrix(){
             return this.globalTrain;
      }
      
      public ConfusionMatrix getGlobalTestMatrix(){
             return this.globalTest;
      }
}