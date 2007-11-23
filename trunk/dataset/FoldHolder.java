package dataset;

import java.io.*;
import java.util.*;

/**
 * Container for train-test validation folds.
 * @author Lucio
 *
 */
public class FoldHolder{
	  
	   public static String EXTENSION=".tab";
	   public static String TRAIN_PREFIX="train";
	   public static String TEST_PREFIX="test";

	   /**
	    * Name convention method for verifying is a dataset is train or test.
	    * @param fileName
	    * @return
	    */
	   private static boolean xtractTrainess(String fileName){
		       return fileName.contains(TRAIN_PREFIX);
	   }
	   
       /**
        * Name convention method for extracting the number of the corresponding validation fold of a datafile.
        * @param fileName
        * @param isTrain
        * @return
        */
	   private static int xtractFoldNumber(String fileName,boolean isTrain){
    	   
    	       String prefix=isTrain?TRAIN_PREFIX:TEST_PREFIX;
    	       
               //System.out.println(fileName);
               //System.out.println(prefix);
    	       return Integer.valueOf(fileName.split(prefix)[1].replace(EXTENSION,""));    	       
       }
	
       /**
        * Simple internal container class.
        * @author Lucio
        *
        */
	   class Fold{
    	   
             private Dataset train;
             private Dataset test;
             
             void setTrain(Dataset train){
            	  this.train=train;
             }

             void setTest(Dataset test){
            	  this.test=test;
             }
             
             Dataset getTrain(){
            	  return this.train;
             }
             
             Dataset getTest(){
            	     return this.test;
             }
       }
       
       private Map<Integer,Fold> fold2Pair=new HashMap<Integer,Fold>();
       
	   /**
	    * Loads train-test folds from a given data directory.
	    * @param dataDir
	    * @param targetCategory
	    * @throws IOException
	    */
       public FoldHolder(String dataDir,String targetCategory) throws IOException{
		   
		      File directory=new File(dataDir);
		      
		      for (File datafile:directory.listFiles()){
		    	  
		    	  String path=datafile.getAbsolutePath();
                  String fileName=datafile.getName();
                  
		    	  //System.out.println("Loading "+fileName);
		    	  if (!fileName.endsWith(EXTENSION)){
		    		 continue;
		    	  }
		    	  
		    	  boolean isTrain=xtractTrainess(fileName);
		    	  int foldNumber=xtractFoldNumber(fileName,isTrain);
		    	  
		    	  if (!fold2Pair.containsKey(foldNumber)){
		    		 this.fold2Pair.put(foldNumber,new Fold());
		    	  }
                  
                  FileReader reader=new FileReader(path);
                  
		    	  if (isTrain){
		    		 this.fold2Pair.get(foldNumber).setTrain(new Dataset(reader,targetCategory));
		    	  }
		    	  else {
		    		   fold2Pair.get(foldNumber).setTest(new Dataset(reader,targetCategory));
		    	  }
		      }
	   }
       
       public FoldHolder(ArrayList<ArrayList<Dataset>> folds){
    	      for (int i=0;i<folds.size();i++){
    	    	  fold2Pair.put(i,new Fold());
    	    	  fold2Pair.get(i).setTrain(folds.get(i).get(0));
    	    	  fold2Pair.get(i).setTest(folds.get(i).get(1));
    	      }
       }
       
       /**
        * Due to historical reasons, this method returns datasets/folds in a nested list structure used by other classes.
        * Maybe in the future I'll make FoldHolder the standard for those classes too.
        * @return
        */
       public ArrayList<ArrayList<Dataset>> getFolds(){
    	      
    	      ArrayList<ArrayList<Dataset>> output=new ArrayList<ArrayList<Dataset>>();
    	      List<Integer> foldNumbers=new ArrayList<Integer>(fold2Pair.keySet());
    	      
    	      Collections.sort(foldNumbers);
    	      for (int foldNumber:foldNumbers){
                  //System.out.println("Adding fold "+foldNumber);
    	    	  
    	    	  ArrayList<Dataset> current=new ArrayList<Dataset>();
    	    	  
    	    	  current.add(fold2Pair.get(foldNumber).getTrain());
                  //System.out.println(current.get(0).getEntities().size()+" entities");
    	    	  current.add(fold2Pair.get(foldNumber).getTest());
                  //System.out.println(current.get(1).getEntities().size()+" entities");
    	    	  output.add(current);
    	      }
    	      return output;
       }

       /**
       * Dumps the folds in the specified directory, following a simple naming convention for train-test pairs.
       * @param dirName
       */
       public void saveIn(String dirName) throws IOException{
    	   
    	      ArrayList<ArrayList<Dataset>> folds=getFolds();
    	   
              for (int i=0;i<folds.size();i++){
             
            	 List<Dataset> fold=folds.get(i);

                 fold.get(0).saveAs(dirName+"/train"+i+".tab");
                 fold.get(1).saveAs(dirName+"/test"+i+".tab");
             }
      }
      
       
}