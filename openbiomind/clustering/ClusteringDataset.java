package clustering;

import java.io.*;
import java.util.*;

import classification.Classifier;

import dataset.*;
import task.*;

/**
 * Stores and operates clustering (i.e., non-categorial) data.
 * @author Lucio
 *
 */
public class ClusteringDataset{
    
       public enum ResultTransform {MUTIC,MOBRA}; 
    
       private List<String> columnNames=new ArrayList<String>();
       private List<ClusterElement> elements=new ArrayList<ClusterElement>();
       private Map<String,String> f2d=new HashMap<String,String>();
    
       /**
        * Creates a clustering dataset using data defined elsewhere (usually a MUTIC or MOBRA transform).
        * @param header
        */
       public ClusteringDataset(List<String> columnNames,List<ClusterElement> elements){
              this.columnNames=columnNames;
              this.elements=elements;
       }
       
       /**
        * MUTIC transformation on classification results.
        * @param results
        */
       private void MUTICMaker(List<ClassificationTaskResult> results,Dataset dataset){
           
               Set<String> featureSet=new HashSet<String>();
               List<UtilityComputer> ucs=new ArrayList<UtilityComputer>();
                 
               for (int i=0;i<results.size();i++){
                   
                   ClassificationTaskResult result=results.get(i);
                   UtilityComputer uc=new UtilityComputer();
                   
                   this.columnNames.add(String.valueOf(i));
                   uc.accountResult(result);
                   featureSet.addAll(uc.getFeatures());
                   ucs.add(uc);
                   //System.out.println(uc.getFeatures().size());
               }
               this.copyDescriptions(dataset, featureSet);
               for (String feature:featureSet){
                 
                   ArrayList<Float> fv=new ArrayList<Float>();
                 
                   for (UtilityComputer uc:ucs){
                       fv.add(uc.getUtilityOf(feature));
                   }
                   elements.add(new ClusterElement(feature,fv));
               }
       }
       
       /**
        * Creates a composite key using two features.
        * @param f1
        * @param f2
        */
       private static String compositeKey(String f1,String  f2){
               return f1+","+f2;
       }
      
       /**
        * Operates MOBRA transformation over classification results.
        * @param results
        */
       public void MOBRAMaker(List<ClassificationTaskResult> results,Dataset dataset){
           
              Map<String,Integer> coocMap=new HashMap<String,Integer>();
              Set<String> featureSet=new HashSet<String>();
              int count=0;
          
              for (ClassificationTaskResult result:results){
                  for (FoldResult fold:result.getFolds()){
                      for (Classifier classifier:fold.getEnsemble().getComponents()){
                          count+=1;
                          for (String f1:classifier.featureSet()){
                              featureSet.add(f1);
                              for (String f2:classifier.featureSet()){
                                 
                                  String key=compositeKey(f1,f2);
                                 
                                  if (!coocMap.containsKey(key)){
                                     coocMap.put(key,0);
                                  }
                                  coocMap.put(key,coocMap.get(key)+1);
                              }
                          }
                      }
                  }
              }
              this.copyDescriptions(dataset, featureSet);
              for (String f1:featureSet){
                 
                 ArrayList<Float> fv=new ArrayList<Float>();
                 
                 for (String f2:featureSet){
                                 
                     String key=compositeKey(f1,f2);
                     
                     if (!coocMap.containsKey(key)){
                        fv.add(0.0f);
                        continue;
                     }
                     fv.add(coocMap.get(key)*100.0f/count);
                 }
                 this.columnNames.add(f1);
                 elements.add(new ClusterElement(f1,fv));
             }
       }
       
       /**
        * Constructor encapsulating transforms on classification outputs.
        * @param results
        * @param transform
        */
       public ClusteringDataset(Dataset dataset,List<ClassificationTaskResult> results,ResultTransform transform){
              if (transform==ResultTransform.MUTIC){
                 MUTICMaker(results,dataset);
                 return;
              }
              MOBRAMaker(results,dataset);
       }
       
       /**
        * Uploads a pre-existing dataset from a reader.
        * @param reader
        */
       public ClusteringDataset(Reader reader) throws IOException{
           
              BufferedReader buffer=new BufferedReader(reader);
              String[] colParsing=buffer.readLine().split("\t");
              
              for (int i=2;i<colParsing.length;i++){
                  columnNames.add(colParsing[i]);
              }
              for (String line=buffer.readLine();line!=null;line=buffer.readLine()){
                  
                  String[] cols=line.split("\t");
                  
                  if (cols.length!=colParsing.length){
                     continue;
                  }
                  
                  String feature=cols[0];
                  
                  f2d.put(feature,cols[1]);
                  
                  List<Float> vector=new ArrayList<Float>();
                  
                  for (int i=2;i<colParsing.length;i++){
                      vector.add(Float.valueOf(cols[i]));
                  }
                  this.elements.add(new ClusterElement(feature,vector));
              }
       }

       /**
        * Copies feature descriptions to the corresponding map of this object.
        * @param dataset
        */
       private void copyDescriptions(Dataset dataset,Set<String> featureSet){
               for (String feature:featureSet){
                   f2d.put(feature,dataset.getDescription(feature));
               }
       }
       
       /**
        * Constructor using a categorial dataset (read either horizontally of vertically) as a basis for clustering data.
        * @param dataset
        * @param orientation
        */
       public ClusteringDataset(Dataset dataset,boolean orientation){
              if (orientation){
                 for (Entity entity:dataset.getEntities()){
                     this.columnNames.add(entity.getId());
                 }
                 for (String feature:dataset.getFeatures()){
                     
                   List<Float> vector=new ArrayList<Float>();
                     
                   for (Entity entity:dataset.getEntities()){
                       vector.add(entity.get(feature));
                   }
                   elements.add(new ClusterElement(feature,vector));
                   f2d.put(feature,dataset.getDescription(feature));
                 }
                 return;
              }
              this.columnNames.addAll(dataset.getFeatures());
              for (Entity entity:dataset.getEntities()){
                  
                  List<Float> vector=new ArrayList<Float>();
                  
                  for (String feature:dataset.getFeatures()){
                      vector.add(entity.get(feature));
                  }
                  elements.add(new ClusterElement(entity.getId(),vector));
              }
       }
       
       public List<ClusterElement> getElements(){
              return this.elements;
       }
       
       private Map<String,ClusterElement> id2Element=new HashMap<String,ClusterElement>();
       
       public ClusterElement getElement(String id){
              if (id2Element.size()==0){
                 for (ClusterElement element:elements){
                     id2Element.put(element.getID(),element);
                 }
              }
              return id2Element.get(id);
       }
       
       public String describe(String feature){
              if (!f2d.containsKey(feature)){
                 return "";
              }
              return f2d.get(feature);
       }
       
       public List<String> getColumnNames(){
              return this.columnNames;
       }
       
       /**
        * Dumps a textual tabular representation of this dataset using a given writer.
        * @param writer
        * @throws IOException
        */
       public void write(Writer writer) throws IOException{
              writer.write("\t");
              for (String colName:this.columnNames){
                  writer.write("\t"+colName);
              }
              writer.write("\n");
              for (ClusterElement element:this.elements){
                  writer.write(element.getID());
                  writer.write("\t"+describe(element.getID()));
                  for (Float value:element.getValues()){
                      writer.write("\t"+value);
                  }
                  writer.write("\n");
              }
       }
}