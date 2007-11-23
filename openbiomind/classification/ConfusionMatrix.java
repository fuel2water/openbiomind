package classification;

import java.io.*;
import java.util.List;

import dataset.Entity;
import util.*;

/**
 * Implements a binary confusion matrix and associated stats.
 * @author Lucio
 *
 */
public class ConfusionMatrix{

	  private static float ALPHA=1.0f; // Alpha constant for F-measure computation. Currently giving equal importance to precision and recall.
	
      private int[][] data=new int[2][2];

      public ConfusionMatrix(){
      }

      /**
       * Constructs confusion matrix from sequence of text lines.
       * @param lines
       */
      public ConfusionMatrix(LineTagger lines){
    	  
    	     String[] firstLine=lines.get(1).split("\t");
    	     
    	     data[0][0]=Integer.valueOf(firstLine[0]);
    	     data[0][1]=Integer.valueOf(firstLine[1]);
    	  
    	     String[] secondLine=lines.get(1).split("\t");
    	     
    	     data[1][0]=Integer.valueOf(secondLine[0]);
    	     data[1][1]=Integer.valueOf(secondLine[1]);
      }
      
      /**
       * Constructs the matrix evaluating a list of categorization cases using a given classifier.
       * @param classifier
       * @param entities
       */
      public ConfusionMatrix(Classifier classifier,List<Entity> entities){
             for (Entity entity:entities){
                 /*if (Randomizer.getInstance().logic()){
                    continue;
                 }*/
                 account(entity.getExpected(),classifier.evaluate(entity.getMap()));
             }
      }

      /**
       * "Balanced accuracy", that is, the average of the accuracy among positives and negatives.
       * @return
       */
      public float balancedAccuracy(){
          
             float accneg=data[0][0]*1.0f/(data[0][0]+data[0][1]);
             float accpos=data[1][1]*1.0f/(data[1][0]+data[1][1]);
             
             return (accneg+accpos)/2;
      }
      
      /**
       * Accounts an (expected,computed) tuple in the matrix.
       * @param expected
       * @param computed
       */
      public void account(boolean expected,boolean computed){
             data[expected?1:0][computed?1:0]+=1;
      }

      public float accuracy(){
             return (data[0][0]+data[1][1])*1.0f/(data[0][0]+data[1][1]+data[0][1]+data[1][0]);
      }

      public float precision(){
    	     if (data[0][1]+data[1][1]==0.0f){
    	    	return 0.0f;
    	     }
    	     return data[1][1]*1.0f/(data[0][1]+data[1][1]);
      }
      
      public float recall(){
    	     if (data[1][0]+data[1][1]==0.0f){
    	    	return 0.0f;
    	     }
    	     return data[1][1]*1.0f/(data[1][0]+data[1][1]);
      }
      
      public float fMeasure(){
    	     if (ALPHA*precision()+recall()==0.0f){
    	    	return 0.0f;
    	     }
    	     return ALPHA*precision()*recall()/(ALPHA*precision()+recall());
      }
      
      public String toString(){
          
             StringBuffer buffer=new StringBuffer();

             buffer.append(data[0][0]+"\t"+data[0][1]+"\n");
             buffer.append(data[1][0]+"\t"+data[1][1]+"\n");
             buffer.append("Accuracy: "+accuracy()+"\n");
             return buffer.toString();
      }
      
      public void write(Writer writer) throws IOException{
             writer.write(this.toString());
      }

      public int get(int i,int j){
             return data[i][j];
      }

      public void add(ConfusionMatrix source){
             for (int i=0;i<2;i++){
                 for (int j=0;j<2;j++){
                     data[i][j]+=source.get(i,j);
                 }
             }
      } 
      
}