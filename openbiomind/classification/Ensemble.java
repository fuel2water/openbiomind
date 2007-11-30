package classification;

import java.io.*;
import java.util.*;

import classification.evolution.gp.SimpleAutomaton;
import classification.evolution.snps.PatternStrengthClassifier;
import dataset.Entity;
import util.LineTagger;

/**
 * Implements an ensemble of classification models.
 * @author Lucio
 *
 */
public class Ensemble implements Classifier{

      private ArrayList<Classifier> data=new ArrayList<Classifier>(); // Model storage.
      private float topAccuracy=0.0f; // Used for smart-adding models based on accuracy and size.

      /**
       * Constructs the ensemble based on a tagged sequence of lines representing models.
       * @param lines
       */
      public Ensemble(LineTagger lines){
    	     for (LineTagger modelBlock:lines.fuzzyLineParse("Model")){
                 if (SimpleAutomaton.isAutomatonBlock(modelBlock)){
    	    	    data.add(new SimpleAutomaton(modelBlock));
                 }
                 else {
                      //System.out.println("snps");
                      data.add(new PatternStrengthClassifier(modelBlock));
                 }
    	     }
      }
      
      public Ensemble(){
    	  
      }
      
      public Set<String> featureSet(){
    	  
    	     Set<String> output=new HashSet<String>();
    	  
    	     for (Classifier classifier:this.data){
    	    	 output.addAll(classifier.featureSet());
    	     }
    	     return output;
      }
      
      /**
       * Returns all models in the ensemble.
       * @return
       */
      public List<Classifier> getComponents(){
             return data;
      }

      /**
       * Writes a textual representation of this ensemble.
       */
      public void write(Writer writer) throws IOException{
            for (int i=0;i<data.size();i++){
                writer.write("Model #"+i+":\n");
                data.get(i).write(writer);
            }
      }

      /**
       * Classifies an element.
       */
      public boolean evaluate(Map<String,Float> f2v){
      
             int pos=0;
             int neg=0;
             
             for (Classifier c:data){
                 if (c.evaluate(f2v)){
                    pos+=1;
                 }
                 else {
                      neg+=1;
                 }
             }
             return pos>neg;
      }

      public Classifier get(int index){
             return data.get(index);
      }

      public boolean equals(Classifier other){
             if (other.size()!=size()){
                return false;
             }
             for (int i=0;i<size();i++){
                 if (!((Ensemble)other).get(i).equals(data.get(i))){
                    return false;
                 }
             }
             return true;
      }

      public void smartAdd(Ensemble e,List<Entity> cases){
             for (Classifier c:e.getComponents()){
             
                 float accuracy=(new ConfusionMatrix(c,cases)).accuracy();
                 
                 if (accuracy>topAccuracy){
                    topAccuracy=accuracy;
                    data=new ArrayList<Classifier>();
                    data.add(c);
                    continue;
                 }
                 if (accuracy==topAccuracy){
                    if (c.size()<data.get(0).size()){
                       data=new ArrayList<Classifier>();
                       data.add(c);
                       continue;
                    }
                    if (c.size()==data.get(0).size()){
                    
                       boolean original=true;
                    
                       for (Classifier c2:data){
                           if (c2.equals(c)){
                              original=false;
                           }
                       }
                       if (original){
                          data.add(c);
                       }
                    }
                 }
             }
      }

      public int size(){
             return data.size();
      }

      public void add(Classifier c){
             data.add(c);
      }

}