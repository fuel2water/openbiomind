package task;

import java.io.*;
import java.util.*;

import dataset.*;
import snps.SNPFoldelizer;

/**
 * "Foldelizes" and feature-selects a SNP dataset, saving it in numeric format.
 * @author Lucio
 *
 */
class FoldSelectSNPs{
    
      public static void main(String[] args){
             if (args.length!=6){
                System.out.println("Usage: java task.FoldSelectSNPs <input file> <number of folds> <number of features> <output directory> <target category> <shuffle>");
                return;
             }
          
             String infile=args[0];
             int nFolds=Integer.valueOf(args[1]);
             int nFeatures=Integer.valueOf(args[2]);
             String outdir=args[3];
             String targetCategory=args[4];
             String shuffle=args[5];
             SNPFoldelizer foldelizer=null;
             
             try {
                 FileReader reader=new FileReader(infile);
                 
                 foldelizer=new SNPFoldelizer(reader,nFolds,nFeatures,targetCategory,shuffle.equals("on"));
                 reader.close();
             }
             catch (IOException e){
                   System.out.println("Error loading input file "+infile);
             }
             try {
                 
                 File checker=new File(outdir);
                 
                 if (!checker.exists()){
                    checker.mkdir();
                 }
                 
                 List<List<Dataset>> folds=foldelizer.getFolds();
                 List<Dataset> testList=new ArrayList<Dataset>();
                 
                 for (int i=0;i<folds.size();i++){
                     
                     List<Dataset> fold=folds.get(i);
                     FileWriter trainWriter=new FileWriter(outdir+"/"+FoldHolder.TRAIN_PREFIX+i+FoldHolder.EXTENSION);
                     
                     fold.get(0).write(trainWriter);
                     trainWriter.close();
                     
                     FileWriter testWriter=new FileWriter(outdir+"/"+FoldHolder.TEST_PREFIX+i+FoldHolder.EXTENSION);
                     
                     fold.get(1).write(testWriter);
                     testWriter.close();
                     testList.add(fold.get(1));
                 }
                 
                 FileWriter baseWriter=new FileWriter(outdir+"/base_dataset"+FoldHolder.EXTENSION);
                 
                 foldelizer.getBaseDataset().write(baseWriter);
                 baseWriter.close();
             }
             catch (IOException e){
                   System.out.println("Error saving output files at directory "+outdir);
             }
      }
    
}