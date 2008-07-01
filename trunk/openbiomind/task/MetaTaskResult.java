package task;

import java.io.*;
import java.util.*;

import classification.*;
import util.Distribution;

/**
 * Loads and analyzes the result of a metatask.
 * @author Lucio
 *
 */
public class MetaTaskResult{
	
	  ClassificationTaskResult endResult;
	  List<ClassificationTaskResult> taskResults=new ArrayList<ClassificationTaskResult>(); 
	
	  public MetaTaskResult(String dirName) throws IOException{
             add(dirName);
	  }
	  
      public MetaTaskResult(){
          
      }
      
      public Distribution computeModelSizesDistribution(){
          
             Distribution output=new Distribution();
             
             for (ClassificationTaskResult result:taskResults){
                 for (FoldResult fold:result.getFolds()){
                     for (Classifier model:fold.getEnsemble().getComponents()){
                         output.account((float)model.size());
                     }
                 }
             }
             return output;
      }
      
      public Distribution computeTestAccuraciesDistribution(){
          
             Distribution output=new Distribution();
             
             for (ClassificationTaskResult result:taskResults){
                 //System.out.println(result.getGlobalTestMatrix().accuracy());
                 output.account(result.getGlobalTestMatrix().accuracy());
             }
             return output;
      }
      
      public Distribution computeTrainAccuraciesDistribution(){
          
             Distribution output=new Distribution();
             
             for (ClassificationTaskResult result:taskResults){
                 output.account(result.getGlobalTrainMatrix().accuracy());
             }
             return output;
      }
      
      public void add(String dirName) throws IOException{
          
             File dir=new File(dirName);
             
             for (File file:dir.listFiles()){
                 if (!file.getName().startsWith("out")){
                    continue;
                 }
                 if (file.getName().equals("outfinal.txt")){
                    endResult=new ClassificationTaskResult(file.getAbsolutePath());
                    continue;
                 }
                 taskResults.add(new ClassificationTaskResult(file.getAbsolutePath())); 
             }
      }

      
	  public List<ClassificationTaskResult> getTaskResults(){
		     return taskResults;
	  }
}