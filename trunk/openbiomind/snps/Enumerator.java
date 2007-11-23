package snps;

import java.io.*;
import java.util.*;

import classification.ConfusionMatrix;

class Enumerator{
    
    private Map<Float,List<SNPFoldResult>> resultMap=new HashMap<Float,List<SNPFoldResult>>();
    
    public Enumerator(SNPDataset dataset,int maxSize){
        
           float threshold=dataset.getDominantFrequency();
           List<String> allSNPs=new ArrayList<String>(dataset.listSNPs());
           float topAccuracy=threshold;
           //float topOne=0.0f;
           
           System.out.println("Acceptance threshold: "+threshold);
           for (int i=1;i<=maxSize;i++){
               for (Set<String> snps:findSets(allSNPs,i)){
                       
                   SNPClassifier classifier=new SNPClassifier(new ArrayList<String>(snps));
                   ConfusionMatrix matrix=classifier.evaluate(dataset.getSamples());
                   float accuracy=matrix.accuracy();
                   
                   /*if ((i==1)&&(accuracy>topOne)){
                      System.out.println(classifier.toString());
                      System.out.println(matrix.toString());
                      topOne=accuracy;
                   }*/
                   if (accuracy<=threshold){
                      continue;
                   }
                   if (accuracy>=topAccuracy){
                      System.out.println(classifier.toString());
                      System.out.println(matrix.toString());
                      topAccuracy=accuracy;
                   }
                   if (!resultMap.containsKey(accuracy)){
                      resultMap.put(accuracy,new ArrayList<SNPFoldResult>());
                   }
                   resultMap.get(accuracy).add(new SNPFoldResult(classifier,matrix));
               }
           }
    }
    
    /**
     * Dumps all good models through a writer.
     * @param writer
     */
    public void write(Writer writer) throws IOException{
        
           List<Float> accuracies=new ArrayList<Float>(resultMap.keySet());
           
           Collections.sort(accuracies);
           Collections.reverse(accuracies);
           for (Float acc:accuracies){
               writer.write("Models reaching accuracy value of "+acc+"\n");
               for (SNPFoldResult result:resultMap.get(acc)){
                   result.write(writer);
               }
           }
    }
    
    /**
     * Computes all subsets of SNPs of size arity.
     * @param snps
     * @param arity
     * @return
     */
    private List<Set<String>> findSets(List<String> snps,int arity){
        
            List<Set<String>> output=new ArrayList<Set<String>>();
            Set<String> current=new HashSet<String>();
                
            grow(snps,current,0,arity,output);
            return output;
    }
      
    /**
     * Grows recursively a snp set to the desired arity.
     * @param snps
     * @param target
     * @param arity
     * @param output
     */
    private static void grow(List<String> snps,Set<String> seed,int start,int arity,List<Set<String>> target){
        
            Set<String> local=new HashSet<String>();
            
            for (String snp:seed){
                local.add(snp);
            }
            local.add(snps.get(start));
            target.add(local);
            if (local.size()==arity){
               return;
            }
            for (int i=start+1;i<snps.size();i++){
                grow(snps,local,i,arity,target);
            }
    }
    
    public static void main(String[] args){
           if (args.length!=4){
              System.out.println("Usage: java snps.Enumerator <dataset> <target category> <max size> <output file>");
              return;
           }
        
           String fileName=args[0];
           String targetCategory=args[1];
           int maxSize=Integer.valueOf(args[2]);
           String outputFile=args[3];
           SNPDataset dataset=null;
           
           try {
               
               FileReader reader=new FileReader(fileName);
               
               dataset=new SNPDataset(reader,targetCategory);
               reader.close();
           }
           catch (IOException e){
                 System.out.println("Error loading dataset "+fileName);
           }
           
           Enumerator enumerator=new Enumerator(dataset,maxSize);
           
           try {
               
               FileWriter writer=new FileWriter(outputFile);
               
               enumerator.write(writer);
               writer.close();
           }
           catch (IOException e){
                 System.out.println("Error while writing results to file "+outputFile);
           }
    }
}