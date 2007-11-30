package task;

import java.io.*;

import util.Distribution;

class SNPPermutationAnalysis{

      private static void compareStats(Distribution normal,Distribution shuffled,String metric){
              System.out.println("Comparing stats for metric: "+metric);
              System.out.println("\tNon-Shuffled\tShuffled");
              System.out.println("Average:\t"+normal.average()+"\t"+shuffled.average()+"\n");
              System.out.println("Std. dev:\t"+normal.standardDeviation()+"\t"+shuffled.standardDeviation());
              System.out.println("p-value: "+Distribution.computePValueFor(normal,shuffled));
      }
    
      public static void main(String[] args){
             if (args.length!=2){
                System.out.println("Usage: java task.SNPPermutationAnalysis <original results> <shuffled results>");
                return;
             }
             
             String baseDir=args[0];
             String shuffledDir=args[1];
             
             try {
                 
                 MetaTaskResult baseResult=new MetaTaskResult(baseDir);
                 MetaTaskResult shuffleResult=new MetaTaskResult();
                  
                 File dirProbe=new File(shuffledDir);
                  
                 for (File subdir:dirProbe.listFiles()){
                     if (!subdir.isDirectory()){
                        continue;
                     }
                     shuffleResult.add(subdir.getAbsolutePath());
                 }
                 compareStats(baseResult.computeTrainAccuraciesDistribution(),shuffleResult.computeTrainAccuraciesDistribution(),"Train Accuracies");
                 compareStats(baseResult.computeTestAccuraciesDistribution(),shuffleResult.computeTestAccuraciesDistribution(),"Test Accuracies");
                 compareStats(baseResult.computeModelSizesDistribution(),shuffleResult.computeModelSizesDistribution(),"Model Sizes");
                 
             }
             catch (IOException e){
                 System.err.println("Error loading metatask results");
             }
      }

}