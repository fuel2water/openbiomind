package task;

import java.io.*;
import java.util.*;

class MetaTaskResult{
	
	  ClassificationTaskResult endResult;
	  List<ClassificationTaskResult> taskResults=new ArrayList<ClassificationTaskResult>(); 
	
	  public MetaTaskResult(String dirName) throws IOException{
		  
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