package task;

import java.util.List;

import classification.*;
import util.LineTagger;

public class FoldResult{
	
	  private Ensemble ensemble;
	  private ConfusionMatrix train;
	  private ConfusionMatrix test;
	
	  public FoldResult(LineTagger lines){
		     ensemble=new Ensemble(lines.fuzzyLineParse("Ensemble","Train").get(0));
		     
		     List<LineTagger> matrices=lines.fuzzyLineParse("Matrix");
		     
		     train=new ConfusionMatrix(matrices.get(0));
		     test=new ConfusionMatrix(matrices.get(1));
	  }
	  
	  public Ensemble getEnsemble(){
		     return ensemble;
	  }
	  
      public ConfusionMatrix getTrainMatrix(){
             return this.train;
      }
      
      public ConfusionMatrix getTestMatrix(){
             return this.test;
      }
}