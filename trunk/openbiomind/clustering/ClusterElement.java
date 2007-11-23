package clustering;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Internal class for implementing a cluster element (described by a feature vector), containing some quirks helpful for 
 * Omniclust
 * @author Lucio
 *
 */       
public class ClusterElement{

    private List<Float> data;
    private Set<ClusterElement> connections=new HashSet<ClusterElement>();
    private String id;
    private boolean visited;
    private boolean collapsed;

    public ClusterElement(){

    }

    public ClusterElement(String id,List<Float> vector){
        this.id=id;
        this.data=vector;
    }

    public List<Float> getValues(){
           return this.data;
    }
    
    public void setCollapsed(boolean value){
           this.collapsed=value;
    }
    
    public boolean isCollapsed(){
           return this.collapsed;
    }
    
    public boolean isVisited(){
           return this.visited;
    }
    
    public String getID(){
           return id;
    }
    
    /**
     * Divides all numbers in the feature vector by a common denominator.
     * @param under
     */
     public void divideBy(int under){
        for (int i=0;i<this.data.size();i++){
            data.set(i,data.get(i)/under);
        }
     }

     /**
      * Sums the corresponding numbers from another cluster element to the ones at this cluster element.
      * @param other
      */
     public void sum(ClusterElement other){
         if (this.data==null){
             this.data=new ArrayList<Float>();
             for (Float number:other.data){
                 this.data.add(number);
             }
             return;
         }
         for (int i=0;i<this.data.size();i++){

             float valueHere=this.data.get(i);
             float valueThere=other.data.get(i);

             this.data.set(i,valueHere+valueThere);
         }
     }
             
     /**
      * Recursivelly collects all members of the cluster containing this element.
      * @param elements
      */
     public void transcriptCluster(List<ClusterElement> elements){
         elements.add(this);
         this.visited=true;
         for (ClusterElement c:connections){
             if (c.visited){
                 continue;
             }
             c.transcriptCluster(elements);
         }
     }

     /**
      * Connects this element to another.
      * @param connection
      */
     public void connect(ClusterElement connection){
         connections.add(connection);
         connection.connections.add(this);
     }
}