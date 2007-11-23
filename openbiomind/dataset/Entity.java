package dataset;

import java.util.HashMap;

/**
 * Stores the feature vector for a given entity in a categorial dataset. (In the most frequent case as OpenBiomind is concerned,
 * stores gene expression values for a given sample/patient.
 * @author Lucio
 *
 */
public class Entity{

       private HashMap<String,Float> feature2Value=new HashMap<String,Float>();
       private boolean expected;
       private String category;
       private String id;

       /**
        * Constructors setting just the "header" (id, category) of the entity.
        * @param id
        * @param category
        * @param targetCategory
        */
       public Entity(String id,String category,String targetCategory){
              this.id=id;
              this.category=category;
              this.expected=(category.equals(targetCategory));
       }
      
       public Entity(String id,String category,boolean expected){
              this.id=id;
              this.category=category;
              this.expected=expected;
       }
      
       /**
        * Returns a deep clone of this entity. (I preferred this instead of a clone() override due to idionsshicratic 
        * reasons concerning clarity.)
        * @return
        */
       public Entity replicate(){
           
              Entity output=new Entity(id,category,"");
              
              output.setExpected(expected);
              for (String feature:feature2Value.keySet()){
                  output.put(feature,get(feature));
              }
              return output;
       }
       
       public void setExpected(boolean value){
    	      this.expected=value;
       }
       
       public HashMap<String,Float> getMap(){
              return feature2Value;
       }
      
       public String getCategory(){
 	      return category;
       }
    
       public String getId(){
 	          return id;
       }
    
       public boolean getExpected(){
              return expected;
       }
      
       public float get(String feature){
              return feature2Value.get(feature);
       }
 
       public void put(String feature,float value){
              feature2Value.put(feature,value);
       }
      
}