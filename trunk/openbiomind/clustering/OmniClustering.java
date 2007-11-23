package clustering;

import java.io.*;
import java.util.*;

/**
 * Clusterizes a set of elements according to the Omniclust algorithm.
 * @author Lucio
 *
 */
public class OmniClustering{
    
       private ClusteringDataset data;
       private List<Cluster> results=new ArrayList<Cluster>();
       
       /**
        * Constructor, where data is preprocessed and clusterized.
        * @param dataset
        * @param metric
        */
       public OmniClustering(ClusteringDataset data,SimilarityMetric metric){
              this.data=data;
           
              List<ClusterElement> vectors=data.getElements();
              int count=0;
              
              for (ClusterElement v1:vectors){
                  /*if (v1.isCollapsed()){
                     continue;
                  }*/
                  count+=1;
                  
                  float maxSimilarity=0.0f;
                  List<ClusterElement> winner=new ArrayList<ClusterElement>();
                  List<ClusterElement> replicants=new ArrayList<ClusterElement>();
                  
                  for (ClusterElement v2:vectors){
                      /*if (v2.isCollapsed()){
                         continue;
                      }*/
                      if (v1.getID().equals(v2.getID())){
                         continue;
                      }
                      
                      float current=metric.computeSimilarity(v1.getValues(),v2.getValues());
                      
                      if (current==1.0f){
                         v2.setCollapsed(true);
                         replicants.add(v2);
                         continue;
                      }
                      if ((current>maxSimilarity)||(winner==null)){
                         maxSimilarity=current;
                         winner=new ArrayList<ClusterElement>();
                      }
                      if (current==maxSimilarity){
                         winner.add(v2);
                      }
                  }
                  winner.addAll(replicants);
                  for (ClusterElement v2:winner){
                      v1.connect(v2);
                      System.out.println("Clustering element "+count+" of "+vectors.size()+": "+v1.getID()+"->"+v2.getID()+" ("+maxSimilarity+")");
                  }
              }
              transcriptClusters(vectors,metric);
       }
       
       /**
        * Constructor loading a pre-computed clustering.
        * @param data
        * @param reader
        */
       public OmniClustering(ClusteringDataset data,Reader reader) throws IOException{
              this.data=data;
              
              BufferedReader buffer=new BufferedReader(reader);
              List<ClusterElement> acc=new ArrayList<ClusterElement>();
              
              for (String line=buffer.readLine();line!=null;line=buffer.readLine()){
                  if (line.contains("Cluster #")){
                     if (acc.size()>0){
                        results.add(new Cluster(acc));
                        acc=new ArrayList<ClusterElement>();
                     }
                     continue;
                  }
                  if (line.contains(": ")){
                     continue;
                  }
                  if (line.trim().equals("")){
                     continue;
                  }
                  
                  String[] cols=line.split("\t");
                  
                  /*if (cols.length<2){
                     continue;
                  }*/
                  
                  String id=cols[0];
                  //System.out.println(id);
                  ClusterElement element=new ClusterElement(id,data.getElement(id).getValues());
                  
                  acc.add(element);
              }
              results.add(new Cluster(acc));
       }
       
       public List<Cluster> getResults(){
              return this.results;
       }
       
       /**
        * Returns the total number of clusters in this clustering.
        * @return
        */
       public int totalClusters(){
              return this.results.size();
       }
       
       /**
        * Returns the total number of cluster elements in all clusters.
        * @return
        */
       public int totalElements(){
              return this.data.getElements().size();
       }
       
       /**
        * Returns the dimensionality of the feature vectors describing cluster elements. (Therefore there is the assumption
        * that all of them have the same dimensionality.) 
        * @return
        */
       public int vectorLength(){
              return data.getElements().get(0).getValues().size();
       }
       
       /**
        * Scans connections between cluster elements in order to determine the resulting clusters.
        * @param vectors
        */
       private void transcriptClusters(List<ClusterElement> vectors,SimilarityMetric metric){
           
               List<Cluster> clusters=new ArrayList<Cluster>();
           
               for (ClusterElement v:vectors){
                   if (v.isVisited()){
                      continue;
                   }
                   
                   List<ClusterElement> elements=new ArrayList<ClusterElement>();
                   
                   v.transcriptCluster(elements);
                   clusters.add(new Cluster(elements));
               }
               
               Map<Float,List<Cluster>> quality2Clusters=new HashMap<Float,List<Cluster>>();
               
               for (Cluster cluster:clusters){
                   
                   float quality=cluster.computeQuality(clusters,metric);
                   
                   if (!quality2Clusters.containsKey(quality)){
                      quality2Clusters.put(quality,new ArrayList<Cluster>());
                   }
                   quality2Clusters.get(quality).add(cluster);
               }
               
               List<Float> qualities=new ArrayList<Float>(quality2Clusters.keySet());
               
               Collections.sort(qualities);
               Collections.reverse(qualities);
               for (Float q:qualities){
                   for (Cluster c:quality2Clusters.get(q)){
                       results.add(c);
                   }
               }
       }
       
       /**
        * Returns the clustering dataset that gave origin to this clustering.
        */
       public ClusteringDataset getData(){
              return this.data;
       }
       
       /**
        * Writes a textual representation of this clustering into a given writer.
        * @param writer
        * @throws IOException
        */
       public void write(FileWriter writer) throws IOException{
              for (int i=0;i<results.size();i++){
                  
                  Cluster cluster=results.get(i);
                  
                  writer.write("Cluster #"+(i+1)+"\n");
                  cluster.write(writer,data);
                  writer.write("\n");
              }
       }
}