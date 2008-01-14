package dataset;

import java.io.*;
import java.util.*;

import util.*;

/**
 * Implements a dataset, that it, a set of case and control entities characterized by numeric feature vectors.
 * @author emac
 *
 */
public class Dataset{

	   private static final int SAM_SHUFFLES=100;
	
       private List<Entity> entities=new ArrayList<Entity>();
       private List<String> features=new ArrayList<String>();
       private Map<String,String> f2d=new HashMap<String,String>();

       public Dataset(){
       }
      
       public Dataset(List<Entity> entities){
              this.entities=entities;
              
              Set<String> featureSet=new HashSet<String>();
              
              for (Entity e:this.entities){
                  featureSet.addAll(e.getMap().keySet());
              }
              this.features=new ArrayList<String>(featureSet);
              Collections.sort(this.features);
       }
       
       public Dataset(Map<String,String> f2d){
    	      this.f2d=f2d;
              this.features=new ArrayList<String>(f2d.keySet());
       }
      
       /**
        * Most used constructor, loading the dataset from a file an using a supplied target category as case.
        * @param fileName
        * @param targetCategory
        * @throws IOException
        */
       public Dataset(Reader externalReader,String targetCategory) throws IOException{
      
              BufferedReader reader=new BufferedReader(externalReader);
              String[] ids=reader.readLine().split("\t");
              String[] labels=reader.readLine().split("\t",-1);
             
              for (int i=2;i<ids.length;i++){
                  
                  String id=ids[i];
                  String category=labels[i];
                  
                  entities.add(new Entity(id,category,targetCategory));
              }
              while (true){
             
                    String line=reader.readLine();
                   
                    if (line==null){
                       break;
                    }
                    
                    String[] cols=line.split("\t");
                    if (cols.length<2){
                       continue;
                    }
                    String feature=cols[0];
                   
                   features.add(feature);
                   f2d.put(feature,cols[1]);
                   for (int i=0;i<entities.size();i++){
                       entities.get(i).put(feature,Float.valueOf(cols[i+2]));
                   }
             }
             reader.close();
      }

       public String getDescription(String f){
             return f2d.get(f);
      }
       
       public void setDescription(String feature,String description){
              f2d.put(feature,description);
      }
       
       /**
       * Returns the average of a given feature in cases or controls.
       * @param f
       * @param label
       * @return
       */
      private float average(String f,boolean label){

    	      float sum=0.0f;
    	  
    	      for (Entity e:this.entities){
    	    	  if (e.getExpected()==label){
    	    	     sum+=e.get(f);
    	    	  }
    	      }
    	      return sum/this.entities.size();
      }
      
      /**
       * Returnns the standard deviation of given feature across all entities.
       * @param f
       * @return
       */
      private float stdev(String f){

    	      float sum=0.0f;
    	  
    	      for (Entity e:this.entities){
    	    	  sum+=e.get(f);
    	      }
    	      
    	      float avg=sum/this.entities.size();
    	      float sumout=0.0f;
    	      
    	      for (Entity e:this.entities){
    	    	  
    	    	  float diff=e.get(f)-avg;
    	    	  
    	    	  sumout+=diff*diff;
    	      }
    	      return (float)Math.sqrt(sumout/this.entities.size());
      }
      
      /**
       * Label saving during SAM shuffles.
       * @return
       */
      private List<Boolean> saveLabels(){
    	  
    	      ArrayList<Boolean> output=new ArrayList<Boolean>();
    	      
    	      for (Entity e:this.entities){
    	    	  output.add(e.getExpected());
    	      }
    	      return output;
      }
      
      /**
       * Randomly shuffles category labels among entities.
       *
       */
      private void shuffleLabels(){
    	      for (int i=0;i<this.entities.size();i++){
    	    	  
    	    	  int j=Randomizer.getInstance().natural(this.entities.size());
    	    	  int k=Randomizer.getInstance().natural(this.entities.size());
                  boolean saver=this.entities.get(j).getExpected();
                  
                  this.entities.get(j).setExpected(this.entities.get(k).getExpected());
                  this.entities.get(k).setExpected(saver);
    	      }
      }
      
      /**
       * Restores original labels after SAM shufflings.
       * @param labels
       */
      private void restoreLabels(List<Boolean> labels){
    	      for (int i=0;i<this.entities.size();i++){
    	    	  this.entities.get(i).setExpected(labels.get(i));
    	      }
      }
      
      /**
       * Computes a simplified SAM (Significance Analysis of Microarray) for the given feature.
       * @param f
       */
      public float computeSAMFor(String f){
    	  
    	     float observed=(average(f,true)-average(f,false))/stdev(f);
    	     float sum=0.0f;
    	     
    	     for (int i=0;i<SAM_SHUFFLES;i++){
    	    	 
    	    	 List<Boolean> labels=saveLabels();
    	    	 
    	    	 shuffleLabels();
    	    	 sum+=(average(f,true)-average(f,false))/stdev(f);
    	    	 restoreLabels(labels);
    	     }
    	     return Math.abs(observed-sum/SAM_SHUFFLES);
      }
      
      /**
       * Computes simple differentiation among categories for a single feature in the dataset.
       * @param f
       * @return
       */
      public float computeDifferentiationFor(String f){
             
             float sumpos=0.0f;
             float sumneg=0.0f;
             int npos=0;
             int nneg=0;
             float lower=this.entities.get(0).get(f);
             float upper=this.entities.get(0).get(f);
                 
             for (Entity e:entities){
            	 if (e.get(f)<lower){
            		lower=e.get(f);
            	 }
            	 if (e.get(f)>upper){
            		upper=e.get(f);
            	 }
                 if (e.getExpected()){
                    sumpos+=e.get(f);
                    npos+=1;
                 }
                 else {
                      sumneg+=e.get(f);
                      nneg+=1;
                 }
             }
             
             float gap=upper-lower;
             
             if (gap==0){
            	return 0.0f;
             }
             return Math.abs(sumpos/npos-sumneg/nneg)/gap;
      }
      
      /**
       * Returns the top n features most differentiated among categories.
       * @param topN
       * @return
       */
      public List<String> topDifferentiated(int topN){
      
             HashMap<Float,List<String>> d2fs=new HashMap<Float,List<String>>();
             
             for (String f:features){

                 float diff=this.computeDifferentiationFor(f);
                 
                 if (!d2fs.containsKey(diff)){
                    d2fs.put(diff,new ArrayList<String>());
                 }
                 d2fs.get(diff).add(f);
             }
             
             ArrayList<Float> values=new ArrayList<Float>(d2fs.keySet());
             
             Collections.sort(values);
             
             List<String> output=new ArrayList<String>();
             
             for (float d:values){
                 for (String f:d2fs.get(d)){
                     output.add(f);
                     if (output.size()==topN){
                        return output;
                     }
                 }
             }
             return output;
      }

      public void select(List<String> selected){
             features=selected;
      }

      public List<String> getFeatures(){
             return features;
      }

      public int numberOf(boolean expected){
      
             int output=0;
             
             for (Entity entity:entities){
                 if (entity.getExpected()==expected){
                    output+=1;
                 }
             }
             return output;
      }
      
      public List<Entity> getEntities(){
             return entities;
      }

      public void add(Entity entity){
             entities.add(entity);
      }
      
      public ArrayList<ArrayList<Dataset>> foldelize(int numberOfFolds){
      
             ArrayList<ArrayList<Dataset>> output=new ArrayList<ArrayList<Dataset>>();
             
             for (int i=0;i<numberOfFolds;i++){
             
                 Dataset train=new Dataset(f2d);
                 Dataset test=new Dataset(f2d);
                 
                 for (int j=0;j<entities.size();j++){
                     if (j%numberOfFolds==i){
                        test.add(entities.get(j));
                     }
                     else {
                          train.add(entities.get(j));
                     }
                 }
                 
                 ArrayList<Dataset> fold=new ArrayList<Dataset>();
                 
                 fold.add(train);
                 fold.add(test);
                 output.add(fold);
             }
             return output;
      }
      
      public void transform(){
    	     for (String feature:features){
    	    	 
    	    	 Distribution distribution=new Distribution();
    	    	 
    	    	 for (Entity entity:entities){
    	    		 distribution.account(entity.get(feature));
    	    	 }
    	    	 
    	    	 float average=distribution.average();
    	    	 float stdev=distribution.standardDeviation();
    	    	 
    	    	 for (Entity entity:entities){
    	    		 entity.put(feature,(entity.get(feature)-average)/stdev);
    	    	 }
    	     }
      }

      /**
       * Save this dataset in a simple tabular format.
       * @param fileName
       * @throws IOException
       */
      public void saveAs(String fileName) throws IOException{
             
             FileWriter writer=new FileWriter(fileName);
             
             writer.write("\t");
             for (Entity entity:entities){
                 writer.write("\t"+entity.getId());
             }
             writer.write("\n");
             writer.write("\t");
             for (Entity entity:entities){
                 writer.write("\t"+entity.getCategory());
             }
             writer.write("\n");
             for (String feature:features){
                 writer.write(feature+"\t");
                 if (f2d.containsKey(feature)){
                    writer.write(f2d.get(feature));
                 }
                 for (Entity entity:entities){
                     writer.write("\t"+entity.get(feature));
                 }
                 writer.write("\n");
             }
             writer.close();
      }
      
      /**
       * Writes this dataset in a simple tabular format.
       * @param fileName
       * @throws IOException
       */
      public void write(Writer writer) throws IOException{
             writer.write("\t");
             for (Entity entity:entities){
                 writer.write("\t"+entity.getId());
             }
             writer.write("\n");
             writer.write("\t");
             for (Entity entity:entities){
                 writer.write("\t"+entity.getCategory());
             }
             writer.write("\n");
             for (String feature:features){
                 writer.write(feature+"\t");
                 if (f2d.containsKey(feature)){
                    //System.out.println("Writing "+feature);
                    writer.write(f2d.get(feature));
                 }
                 for (Entity entity:entities){
                     writer.write("\t"+entity.get(feature));
                 }
                 writer.write("\n");
             }
      }
      
      public static void main(String[] args){
    	  
             Dataset dataset=null;
             String fileName=args[0];
    	  
    	     try {
                 FileReader reader=new FileReader(args[0]);
                 
    	         dataset=new Dataset(reader,args[1]);
    	     }
    	     catch (IOException e){
    	    	   System.err.println("Unable to read dataset from file "+fileName);
    	     }
    	     
    	     dataset.transform();
    	     
             try {
    	         dataset.saveAs(args[2]);
             }
             catch (IOException e){
  	    	       System.err.println("Unable to save dataset in file "+args[2]);
             }
      }
      
}