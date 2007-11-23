package task;

import java.io.*;

import snps.SNPDataset;

/**
 * Converts a SNP dataset into a conventional dataset, allowing the use of methods not specific to SNPs. 
 * @author Lucio
 *
 */
class SNP2Numeric{
      
      public static void main(String[] args){
          
             String infile=args[0];
             String outfile=args[1];
             SNPDataset dataset=null;
           
             try {
               
                 FileReader reader=new FileReader(infile);
               
                 dataset=new SNPDataset(reader,"");
                 reader.close();
             }
             catch (IOException e){
                   System.err.println("Error loading dataset "+infile);
             }
           
             try {
                 
                 FileWriter writer=new FileWriter(outfile);
                 
                 dataset.writeAsNumeric(writer);
                 writer.close();
             }
             catch (IOException e){
                   System.err.println("Error writing converted dataset to file "+outfile);
             }
      }

}