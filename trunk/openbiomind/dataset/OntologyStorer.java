package dataset;

import java.io.*;
import java.util.*;

/**
 * Condenses all ontology-related functionalities, including (and ultimately for) dataset enhancement.
 * @author Lucio
 *
 */
public class OntologyStorer{
    
       private Map<String,Set<String>> gene2terms=new HashMap<String,Set<String>>();
       private Map<String,String> term2description=new HashMap<String,String>();
    
       /**
        * Creates an enhanced version of a given original dataset by adding GO-related features to it.
        * @param original
        * @return
        */
       public Dataset makeEnhanced(Dataset original){
           
              List<Entity> enhancedEntities=new ArrayList<Entity>();
           
              for (Entity entity:original.getEntities()){
                  enhancedEntities.add(entity.replicate());
              }
              
              Map<String,Integer> go2count=new HashMap<String,Integer>();
              
              for (String feature:original.getFeatures()){
                  if (!gene2terms.containsKey(feature)){
                     continue;
                  }
                  for (String go:gene2terms.get(feature)){
                      if (!go2count.containsKey(go)){
                         //System.out.println("Adding go "+go);
                         go2count.put(go,0);
                         for (Entity entity:enhancedEntities){
                             entity.put(go,0.0f);
                         }
                      }
                      go2count.put(go,go2count.get(go)+1);
                      for (int i=0;i<enhancedEntities.size();i++){
                          
                          Entity entity=enhancedEntities.get(i);
                          
                          entity.put(go,entity.get(go)+original.getEntities().get(i).get(feature));
                      }
                  }
              }
              for (Entity entity:enhancedEntities){
                  for (String go:go2count.keySet()){
                      entity.put(go,entity.get(go)/go2count.get(go));
                  }
              }
              
              Dataset output=new Dataset(enhancedEntities);
              
              for (String feature:original.getFeatures()){
                  output.setDescription(feature,original.getDescription(feature));
              }
              for (String go:go2count.keySet()){
                  output.setDescription(go,this.term2description.get(go));
              }
              return output;
       }

       /**
        * Loads goTerms and their descriptions from a given reader, assuming the OBO 1.2 format.
        * @param goTerms
        */
       private void loadTerms(Reader terms) throws IOException{
           
               BufferedReader buffer=new BufferedReader(terms);
               
               for (String line=buffer.readLine();line!=null;line=buffer.readLine()){
                   
                   String[] cols=line.split("\t");
                   String term=cols[0];
                   String description=cols[1];
                   
                   //System.out.println(goName+"->"+description);
                   term2description.put(term,description);
               }
       }
       
       /**
        * Loads the gene->go associations from a given reader assuming the format of the GO associations file.
        * @param goAssociations
        */
       private void loadAssociations(Reader associations) throws IOException{
           
               BufferedReader buffer=new BufferedReader(associations);
               
               for (String line=buffer.readLine();line!=null;line=buffer.readLine()){
                   
                   String[] cols=line.split("\t");
                   String gene=cols[0];
                   String term=cols[1];
                   
                   if (!gene2terms.containsKey(gene)){
                      gene2terms.put(gene,new HashSet<String>());
                   }
                   //System.out.println(geneSymbol+"->"+goTerm);
                   gene2terms.get(gene).add(term);
               }
       }
    
       /**
        * Constructor loading ontological data from readers.
        * @param goTerms
        * @param goAssociations
        * @throws IOException
        */
       public OntologyStorer(Reader terms,Reader associations) throws IOException{
              loadTerms(terms);
              loadAssociations(associations);
       }
    
}