package clustering;

import java.io.IOException;
import java.io.Writer;
import java.util.List;


/**
* Internal class for storing and operating over clusters.
* @author Lucio
*
*/
class Cluster{
       
      private List<ClusterElement> elements;
      private float homogeneity,quality,separation;
      private boolean qualityComputed;
      private ClusterElement centroid;
       
      public Cluster(List<ClusterElement> elements){
             this.elements=elements;
      }
      
      public List<ClusterElement> getElements(){
             return this.elements;
      }
      
      /**
      * Writes a textual representation of this cluster.
      * @param writer
      */
     void write(Writer writer,ClusteringDataset data) throws IOException{
          writer.write("Quality: "+this.quality+"\n");
          writer.write("Homogeneity: "+this.homogeneity+"\n");
          writer.write("Separation: "+this.separation+"\n");
          for (ClusterElement element:this.elements){
              writer.write(element.getID()+"\t"+data.describe(element.getID())+"\n");
          }
     }
     
     /**
      * Returns the centroid of this cluster.
      * @return
      */
     ClusterElement computeCentroid(){
                    if (centroid!=null){
                       return centroid;
                    }
                    centroid=new ClusterElement();
                    for (ClusterElement element:this.elements){
                        centroid.sum(element);
                    }
                    centroid.divideBy(elements.size());
                    return centroid;
     }
     
     /**
      * Computes homogeneity, separation and overall quality for this cluster.
      * @param clusters
      * @param metric
      */
         public float computeQuality(List<Cluster> clusters,SimilarityMetric metric){
                if (this.qualityComputed){
                   return this.quality;
                }
             
                ClusterElement centroid=this.computeCentroid();
                float sum=0.0f;
                
                for (ClusterElement element:elements){
                    sum+=metric.computeSimilarity(centroid.getValues(),element.getValues());
                }
                this.homogeneity=sum/this.elements.size();
                this.separation=Float.MAX_VALUE;
                for (Cluster cluster:clusters){
                    if (cluster==this){
                       continue;
                    }
                    
                    float current=1.0f/(1.0f+metric.computeSimilarity(centroid.getValues(),cluster.computeCentroid().getValues()));
                    
                    if (current<this.separation){
                       this.separation=current;
                    }
                }
                this.quality=this.homogeneity*this.separation;
                this.qualityComputed=true;
                return this.quality;
         }

}

