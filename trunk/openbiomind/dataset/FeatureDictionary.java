package dataset;

import java.io.*;
import java.util.*;

public class FeatureDictionary{
    
       private Map<String,String> data=new HashMap<String,String>();
    
       public FeatureDictionary(Reader reader) throws IOException{
           
              BufferedReader buffer=new BufferedReader(reader);
              
              for (String line=buffer.readLine();line!=null;line=buffer.readLine()){
                  
                  String[] cols=line.split("\t");
                  
                  this.data.put(cols[0],cols[1]);
              }
       }
       
       public FeatureDictionary(Dataset dataset){
              for (String feature:dataset.getFeatures()){
                  this.data.put(feature,dataset.getDescription(feature));
              }
       }
       
       public FeatureDictionary(){

       }
       
       public String getDescriptionOf(String feature){
              if (this.data.containsKey(feature)){
                 return this.data.get(feature);
              }
              return "";
       }
}