package util;

import java.io.*;
import java.util.*;

/**
 * Internal class for loading and manipulating utility-related values.
 * @author Lucio.
 *
 */
public class UtilityTable{
    
      private List<FeatureInfo> features=new ArrayList<FeatureInfo>();
      private Map<String,FeatureInfo> f2r=new HashMap<String,FeatureInfo>();
    
      public FeatureInfo getFeature(String feature){
             if (!f2r.containsKey(feature))
                return null;
             return f2r.get(feature);
      }

      public UtilityTable(Reader reader) throws IOException{
                
             BufferedReader buffer=new BufferedReader(reader);
                   
             for (String line=buffer.readLine();line!=null;line=buffer.readLine()){
                 if (line.trim().equals("")){
                    continue;
                 }
                 
                 FeatureInfo record=new FeatureInfo(line);
                 
                 features.add(record);
                 f2r.put(record.featureName,record);
             }
      }
      
}


