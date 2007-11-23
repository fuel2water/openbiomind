package snps;

import dataset.Entity;

/**
 * Operates SNP samples as a façade to the parent SNP dataset.
 * @author Lucio
 *
 */
public class SNPSample{
    
      private boolean expected;
      private String category;
      private SNPDataset dataset;
      private String id;
      
      public SNPSample(SNPDataset dataset,String id,String category,String targetCategory){
             this.dataset=dataset;
             this.id=id;
             this.category=category;
             this.expected=category.equals(targetCategory);
      }
    
      public void setExpected(boolean value){
             this.expected=value;
      }
      
      /** 
       * Converts this sample into an entity of a numeric dataset.
       */
      public Entity toEntity(){
          
             Entity output=new Entity(this.id,this.category,this.expected);
             
             for (String snp:this.dataset.listSNPs()){
                 output.put(snp,dataset.getNumeric(id,snp));
             }
             return output;
      }
      
      public String getCategory(){
             return this.category;
      }
      
      public String getID(){
             return this.id;
      }
      
      public boolean isCase(){
             return expected;
      }
      
      public boolean isHetero(String snp){
             return dataset.isHetero(id,snp);
      }
      
      public boolean isHomo(String snp){
             return dataset.isHomo(id,snp);
      }
      
      public boolean getExpected(){
             return expected;
      }
}