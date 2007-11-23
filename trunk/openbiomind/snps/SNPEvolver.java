package snps;

import java.io.*;
import java.util.*;

import util.Randomizer;

/**
 * Uses GA to evolver a pattern-strength classifier able to differentiate cases from controls in the dataset.
 * @author Lucio
 *
 */
public class SNPEvolver{
       
       /**
        * Encapsulates a GA-population of SNPClassifiers.
        * @author Lucio
        *
        */
       class SNPPopulation{
           
             SNPDataset dataset;
             List<SNPClassifier> data=new ArrayList<SNPClassifier>();
           
             public SNPPopulation(SNPDataset dataset){
                    this.dataset=dataset;
             }
           
             public SNPPopulation(int size,SNPDataset dataset){
                    this(dataset);
                 
                    List<String> snps=dataset.listSNPs();
                    
                    while (size()<size){
                          add(new SNPClassifier(snps));
                    }
             }
           
             /**
              * Returns the best classifier - the one with best accuracy and lowest number of SNPs. 
              * @return
              */
             public SNPClassifier getBest(){
                 
                    SNPClassifier output=this.data.get(0);
                 
                    for (SNPClassifier classifier:this.data){
                        if (classifier.getFitness()>output.getFitness()){
                           output=classifier;
                        }
                        if ((classifier.getFitness()==output.getFitness())&&(classifier.size()<output.size())){
                           output=classifier;
                        }
                    }
                    return output;
             }
             
             public void computeFitness(){
                    for (SNPClassifier classifier:this.data){
                        classifier.computeFitness(dataset.getSamples());
                    }
             }
             
             public int size(){
                    return this.data.size();
             }
             
             public void add(SNPClassifier classifier){
                    data.add(classifier);
             }
             
             public SNPClassifier select(){
                 
                    int i=Randomizer.getInstance().natural(size());
                    int j=Randomizer.getInstance().natural(size());
                    
                    if (data.get(i).getFitness()>data.get(j).getFitness()){
                       return data.get(i);
                    }
                    return data.get(j);
             }
             
       }
    
       private SNPFoldResult result;
       
       public SNPEvolver(SNPDataset dataset,int popSize,int generations){
           
              SNPPopulation population=new SNPPopulation(popSize,dataset);
              
              for (int i=0;i<generations;i++){
                  population.computeFitness();
                  
                  SNPPopulation newPop=new SNPPopulation(dataset);
                  SNPClassifier supremor=population.getBest();
                  
                  newPop.add(supremor);
                  System.out.println("Generation "+i+", best fitness "+supremor.getFitness()+", "+supremor.size()+" snps");
                  //System.out.println(supremor.toString());
                  while (newPop.size()<population.size()){
                        newPop.add(new SNPClassifier(population.select(),population.select()));
                  }
                  population=newPop;
              }
              population.computeFitness();
              
              SNPClassifier winner=population.getBest();
              
              result=new SNPFoldResult(winner,winner.evaluate(dataset.getSamples()));
       }
    
       public SNPFoldResult getResult(){
              return this.result;
       }
       
       public static void main(String[] args){
           if (args.length!=3){
              System.out.println("Usage: java snps.Enumerator <dataset> <target category> <output file>");
              return;
           }
        
        
           String fileName=args[0];
           String targetCategory=args[1];
           String outputFile=args[2];
           SNPDataset dataset=null;
           
           try {
               
               FileReader reader=new FileReader(fileName);
               
               dataset=new SNPDataset(reader,targetCategory);
               reader.close();
           }
           catch (IOException e){
                 System.out.println("Error loading dataset "+fileName);
           }
           
           SNPEvolver evolver=new SNPEvolver(dataset,100,50);
           
           try {
               
               FileWriter writer=new FileWriter(outputFile);
               
               evolver.getResult().write(writer);
               writer.close();
           }
           catch (IOException e){
                 System.out.println("Error while writing results to file "+outputFile);
           }
       }
    
}