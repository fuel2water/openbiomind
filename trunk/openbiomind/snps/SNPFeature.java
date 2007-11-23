package snps;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import classification.ConfusionMatrix;

 /**
  * Stores and operates a single SNP feature in the dataset.
  * @author Lucio
  *
  */ 
public class SNPFeature{
          
        public static float AB_VALUE=0.5f;
        public static float BLANK_VALUE=0.0f;
        public static float AA_VALUE=0.25f;
        public static float BB_VALUE=0.75f;
      
        private static char BLANK_CHAR='0';
     
        private String id;
        private String[] data;
        private float differentiation;
        private Map<String,Float> nMap=new HashMap<String,Float>();
      
        /**
         * Constructor computing differentiation. Differentiation based on homozygosis frequency.
         * @param line
         * @param labels
         */
        public SNPFeature(String line,List<SNPSample> samples){
            
               String[] fields=line.split("\t");
               
               this.id=fields[0];
               this.data=new String[fields.length-1];
               
               int pos=0,aaPos=0;
               int neg=0,aaNeg=0;
               
               for (int i=0;i<data.length;i++){
                   data[i]=fields[i+1];
                   if (samples.get(i).isCase()){
                      pos++;
                      if (isHomo(i)){
                         aaPos++;
                      }
                   }
                   else {
                        neg++;
                        if (isHomo(i)){
                           aaNeg++;
                        }
                   }
               }
               
               float ratioPos=aaPos*1.0f/pos;
               float ratioNeg=aaNeg*1.0f/neg;
               
               this.differentiation=ratioPos-ratioNeg;
        }
        
        /**
         * Constructor computing differentiation based on chosen samples. Differentiation based on allele frequency.
         * @param line
         * @param labels
         */
        public SNPFeature(String line,List<SNPSample> samples,boolean[] chosen){
            
               String[] fields=line.split("\t");
               
               this.id=fields[0];
               this.data=new String[fields.length-1];
               ConfusionMatrix matrix=new ConfusionMatrix();
               
               for (int i=0;i<samples.size();i++){
                   data[i]=fields[i+1];
                   if (!chosen[i]){
                      continue;
                   }
                   matrix.account(samples.get(i).getExpected(),this.isHomo(i));
               }
               this.differentiation=matrix.balancedAccuracy();
        }
        
        /**
         * Converts a base value for a given sumple into a float representation.
         * @param i
         * @return
         */
        public float getNumeric(int index){
               if (nMap.size()==0){
                   
                  Set<String> cSet=new HashSet<String>();
                   
                  for (String c:data){
                      //System.out.println(c);
                      cSet.add(c);
                  }

                  //System.out.println(cSet.size());
                  //System.out.println(this.id);
                  List<String> sorter=new ArrayList<String>(cSet);
                  
                  Collections.sort(sorter);
                  
                  for (int i=0;i<sorter.size();i++){
                      if ((sorter.get(i).charAt(0)==BLANK_CHAR)||(sorter.get(i).charAt(1)==BLANK_CHAR)){
                         nMap.put(sorter.get(i),BLANK_VALUE);
                         continue;
                      }
                      if (sorter.get(i).charAt(0)!=sorter.get(i).charAt(1)){
                         nMap.put(sorter.get(i),AB_VALUE);
                         continue;
                      }
                      if (nMap.containsValue(AA_VALUE)){
                         nMap.put(sorter.get(i),BB_VALUE);
                      }
                      else {
                           nMap.put(sorter.get(i),AA_VALUE);
                      }
                      if (nMap.size()==4){
                         break;
                      }
                  }
               }
               return nMap.get(data[index]);
        }
        
        public String getID(){
               return this.id;
        }
        
        public boolean isBlank(int index){
               return (this.data[index].charAt(0)==BLANK_CHAR)||(this.data[index].charAt(1)==BLANK_CHAR);
        }
        
        public boolean isHomo(int index){
               if (isBlank(index)){
                  return false;
               }
               return this.data[index].charAt(0)==this.data[index].charAt(1);
        }
        
        public boolean isHetero(int index){
               if (isBlank(index)){
                  return false;
               }
               return this.data[index].charAt(0)!=this.data[index].charAt(1);
        }
        
        public float getDifferentiation(){
               return this.differentiation;
        }
        
        public void write(Writer writer) throws IOException{
               writer.write(this.id);
               for (String c:this.data){
                   writer.write("\t"+c);
               }
               writer.write("\n");
        }
        
        /**
         * Returns a textual description of the base-to-numeric mapping used by this SNP.
         *
         */
        public String describeMapping(){
            
               StringBuffer buffer=new StringBuffer();
               
               for (String c:this.nMap.keySet()){
                   buffer.append(c+"->"+nMap.get(c)+";");
               }
               return buffer.toString();
        }
        
        /**
         * Writes this feature values in numeric form, as in conventional expression datasets.
         * @param writer
         * @throws IOException
         */
        public void writeAsNumeric(Writer writer) throws IOException{
               writer.write(this.id);
               
               writer.write("\t"+this.describeMapping());
               
               for (String c:data){
                   writer.write("\t"+nMap.get(c));
               }
               writer.write("\n");
        }
            
}
