package snps;

import java.io.*;
import java.util.*;

import dataset.*;
import util.Randomizer;

/**
 * Divides a SNP dataset in folds, feature selects it and converts to numeric format.
 * @author Lucio
 *
 */
public class SNPFoldelizer{
    
       private List<List<Dataset>> folds=new ArrayList<List<Dataset>>();
       private Dataset baseDataset;
    
       private static List<String> line2List(String line){
           
               String[] cols=line.split("\t");
               List<String> output=new ArrayList<String>();
               
               for (int i=1;i<cols.length;i++){
                   output.add(cols[i]);
               }
               return output;
       }
       
       public SNPFoldelizer(Reader reader,int nFolds,int nFeatures,String targetCategory,boolean shuffle) throws IOException{
           
              BufferedReader buffer=new BufferedReader(reader);
              List<String> ids=line2List(buffer.readLine());
              List<String> labels=line2List(buffer.readLine());
              
              if (shuffle){
                 for (int i=0;i<labels.size();i++){
                     
                     int j=Randomizer.getInstance().natural(labels.size());
                     int k=Randomizer.getInstance().natural(labels.size());
                     String saver=labels.get(j);
                     
                     labels.set(j,labels.get(k));
                     labels.set(k,saver);
                 }
              }
              
              int[] foldMap=new int[ids.size()];
              List<SNPDataset> snpDatasets=new ArrayList<SNPDataset>();
              boolean[][] foldMasks=new boolean[nFolds][foldMap.length];
              
              for (int i=0;i<foldMap.length;i++){
                  foldMap[i]=i%nFolds;
              }
              for (int i=0;i<foldMap.length;i++){
                  
                  int j=Randomizer.getInstance().natural(foldMap.length);
                  int k=Randomizer.getInstance().natural(foldMap.length);
                  int saver=foldMap[j];
                  
                  foldMap[j]=foldMap[k];
                  foldMap[k]=saver;
              }
              for (int i=0;i<nFolds;i++){
                  for (int j=0;j<foldMap.length;j++){
                      foldMasks[i][j]=(foldMap[j]!=i);
                  }
              }
              for (int i=0;i<nFolds;i++){
                  snpDatasets.add(new SNPDataset(ids,labels,targetCategory,nFeatures));
              }
              
              int count=0;
              
              for (String line=buffer.readLine();line!=null;line=buffer.readLine()){
                  for (int i=0;i<nFolds;i++){
                      
                      SNPFeature feature=new SNPFeature(line,snpDatasets.get(i).getSamples(),foldMasks[i]);
                      
                      snpDatasets.get(i).addFeature(feature);
                  }
                  count+=1;
                  /*if (count==1000){
                     break;
                  }*/
              }
              
              Map<String,SNPFeature> i2f=new HashMap<String,SNPFeature>();              
              
              for (int i=0;i<nFolds;i++){
                  for (SNPFeature f:snpDatasets.get(i).getFeatures()){
                      i2f.put(f.getID(),f);
                  }
                  folds.add(snpDatasets.get(i).numericSplit(foldMasks[i]));
              }
              
              SNPDataset baseSNPs=new SNPDataset(snpDatasets.get(0).getSamples(),i2f.values());
              List<Entity> entities=new ArrayList<Entity>();
              
              for (SNPSample s:baseSNPs.getSamples()){
                  entities.add(s.toEntity());
              }
              this.baseDataset=new Dataset(entities);
       }
       
       public Dataset getBaseDataset(){
              return this.baseDataset;
       }
       
       public List<List<Dataset>> getFolds(){
              return this.folds;
       }
       
}