package util;

/**
  * Container class for feature data.
  * @author emac
  *
*/
public class FeatureInfo{
          
       public String featureName,featureDescription;
       public float featureUtility;
       public int uRank,diffRank,samRank;
            
       public FeatureInfo(String line){
             //System.out.println(line);
                
             String[] cols=line.split("\t");
                   
             featureName=cols[0];
             if (cols.length>=6){
                featureDescription=cols[5];
             }
             else {
                  featureDescription="";
             }
             featureUtility=Float.valueOf(cols[1]);
             uRank=Integer.valueOf(cols[2]);
             diffRank=Integer.valueOf(cols[3]);
             samRank=Integer.valueOf(cols[4]);
       }
        
}
      

