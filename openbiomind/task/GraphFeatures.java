package task;

import java.io.*;
import java.util.*;

import clustering.*;

/**
 * Draws a graph (in Dot Language) of the most important features of a metatask, showing co-occurrence and co-expression
 * relations between them.
 * @author Lucio
 *
 */
class GraphFeatures{
    
      private static final Set<String> mandatoryOptions=new HashSet<String>();
      private static final Set<String> optionalOptions=new HashSet<String>();
      public static final String HORIZONTAL_DATASET_OPTION="-h";
      public static final String MOBRA_DATASET_OPTION="-m";
      public static final String UTILITY_FILE_OPTION="-u";
      public static final String OUTPUT_FILE_OPTION="-o";
    
      static {
             mandatoryOptions.add(HORIZONTAL_DATASET_OPTION);
             mandatoryOptions.add(MOBRA_DATASET_OPTION);
             mandatoryOptions.add(UTILITY_FILE_OPTION);
             mandatoryOptions.add(OUTPUT_FILE_OPTION);
             optionalOptions.add("-"+PipelineParameters.MAX_NODES_GRAPH_PROPERTY);
             optionalOptions.add("-"+PipelineParameters.MAX_COOC_EDGES_GRAPH_PROPERTY);
             optionalOptions.add("-"+PipelineParameters.MAX_COEX_EDGES_GRAPH_PROPERTY);      
      }
      
      /**
       * Container class for feature data.
       * @author emac
       *
       */
      class FeatureInfo{
          
            String featureName,featureDescription;
            float featureUtility;
            int uRank,diffRank,samRank;
            
            public FeatureInfo(String line){
                   //System.out.println(line);
                
                   String[] cols=line.split("\t");
                   
                   featureName=cols[0];
                   if (cols.length>=6){
                      featureDescription=cols[5];
                   }
                   else {
                        featureDescription="";
                   }
                   featureUtility=Float.valueOf(cols[1]);
                   uRank=Integer.valueOf(cols[2]);
                   diffRank=Integer.valueOf(cols[3]);
                   samRank=Integer.valueOf(cols[4]);
            }
        
      }
      
      class FeatureCooc{
          
            String feature;
            float cooc;
            
      }
      
      /**
       * Manages the co-occurrence rank of the useful features.
       * @author Lucio
       *
       */
      class FeatureCoocRank{
          
            List<Float> values;
            List<Set<String>> features=new ArrayList<Set<String>>();
            
            public FeatureCoocRank(Map<Float,Set<String>> cmap){
                
                   this.values=new ArrayList<Float>(cmap.keySet());
                   
                   Collections.sort(this.values);
                   Collections.reverse(this.values);
                   for (Float v:this.values){
                       features.add(cmap.get(v));
                   }
            }
      }
      
      /**
       * Internal class for loading and manipulating utility-related values.
       * @author Lucio.
       *
       */
      class UtilityTable{
          
            List<FeatureInfo> features=new ArrayList<FeatureInfo>();
            Map<String,FeatureInfo> f2r=new HashMap<String,FeatureInfo>();
          
            public UtilityTable(Reader reader) throws IOException{
                
                   BufferedReader buffer=new BufferedReader(reader);
                   
                   for (String line=buffer.readLine();line!=null;line=buffer.readLine()){
                       if (line.trim().equals("")){
                          continue;
                       }
                       
                       FeatureInfo record=new FeatureInfo(line);
                       
                       features.add(record);
                       f2r.put(record.featureName,record);
                   }
            }
            
      }

      class CoocMap{
          
            Map<String,FeatureCoocRank> mastermap=new HashMap<String,FeatureCoocRank>(); 
          
            public CoocMap(Reader reader) throws IOException{
                
                   BufferedReader buffer=new BufferedReader(reader);
                   String[] firstCols=buffer.readLine().split("\t");

                   for (String line=buffer.readLine();line!=null;line=buffer.readLine()){
                       
                       String[] cols=line.split("\t");
                       String f1=cols[0];
                       Map<Float,Set<String>> rankMap=new HashMap<Float,Set<String>>();                         

                       for (int i=2;i<cols.length;i++){
                           
                           String f2=firstCols[i];
                           
                           if (f1.equals(f2)){
                              //System.out.println(f1+" "+f2);
                              continue;
                           }
                           //System.out.println(f2);
                           
                           float v=Float.valueOf(cols[i]);
                           
                           if (v==0.0f){
                              continue;
                           }
                           
                           //System.out.println(v);
                           
                           if (!rankMap.containsKey(v)){
                              rankMap.put(v,new HashSet<String>());
                           }
                           rankMap.get(v).add(f2);
                       }
                       mastermap.put(f1,new FeatureCoocRank(rankMap));
                   }
            }
            
            public CoocMap(ClusteringDataset cdata,UtilityTable ut){
                
                   CosineMetric gauger=new CosineMetric();
                   //SNPMetric gauger=new SNPMetric();
                
                   for (ClusterElement e1:cdata.getElements()){
                       if (!ut.f2r.containsKey(e1.getID())){
                          continue;
                       }
                       
                       Map<Float,Set<String>> rankMap=new HashMap<Float,Set<String>>();
                       
                       for (ClusterElement e2:cdata.getElements()){
                           if (e1.getID().equals(e2.getID())){
                              continue;
                           }
                           if (!ut.f2r.containsKey(e2.getID())){
                              continue;
                           }
                           
                           float v=gauger.computeSimilarity(e1.getValues(),e2.getValues());

                           if (!rankMap.containsKey(v)){
                              rankMap.put(v,new HashSet<String>());
                           }
                           rankMap.get(v).add(e2.getID());
                       }
                       mastermap.put(e1.getID(),new FeatureCoocRank(rankMap));
                   }
            }
      }
      
      private PipelineParameters parameters;
      private UtilityTable ut;
      private CoocMap cm;
      private CoocMap coex;
      
      /**
       * Contructor, where all cross-relation structures are computed.
       * @param parameters
       */
      public GraphFeatures(PipelineParameters parameters){
             this.parameters=parameters;
             System.out.println("Loading "+parameters.getUtilityFile());
             try {
                 
                 FileReader reader=new FileReader(parameters.getUtilityFile());
                 
                 ut=new UtilityTable(reader);
                 reader.close();
             }
             catch (IOException e){
                   System.err.println("Error while loading feature utility file.");
             }
             System.out.println("Loading "+parameters.getMobraDatasetPath());
             try {
                 
                 FileReader reader=new FileReader(parameters.getMobraDatasetPath());
                 
                 cm=new CoocMap(reader);
                 reader.close();
             }
             catch (IOException e){
                   System.err.println("Error while loading feature utility file.");
             }
             System.out.println("Loading "+parameters.getHorizontalDatasetPath());
             try {
                 
                 FileReader reader=new FileReader(parameters.getHorizontalDatasetPath());
                 
                 coex=new CoocMap(new ClusteringDataset(reader),this.ut);
                 reader.close();
             }
             catch (IOException e){
                   System.err.println("Error while loading feature utility file.");
             }
      }
      
      /**
       * Writes information for a given feature in a Dot-coded graph node.
       * @param feature
       * @param writer
       */
      private void writeNode(String feature,Writer writer) throws IOException{
              writer.write("\""+feature+"\" [\n");
              
              FeatureInfo featureRecord=this.ut.f2r.get(feature);
              
              writer.write("label = \"{ "+feature+" | "+featureRecord.featureDescription+" | {"+
                              featureRecord.featureUtility+"% | "+featureRecord.uRank+" | "+
                              featureRecord.diffRank+" | "+featureRecord.samRank+"} }\",\n");
              writer.write("shape = \"record\"\n");
              writer.write("];\n");
      }
      
      /**
       * Dumping of a Dot-specified graph of the relations according to the parameters on number of edges.
       * @param fileName
       */
      public void write(Writer writer) throws IOException{
          
             Set<String> clinked=new HashSet<String>();
             Set<String> xlinked=new HashSet<String>();
             Set<String> useful=new HashSet<String>();
             Set<String> mentioned=new HashSet<String>();
             int nu=Math.min(this.parameters.getTopNUseful(),this.ut.features.size());
          
             writer.write("graph G {\n");
             for (int i=0;i<nu;i++){
                 
                 String f1=this.ut.features.get(i).featureName;
                 
                 useful.add(f1);
                 writeNode(f1,writer);
                 
                 int nc=Math.min(this.parameters.getTopNCooc(),this.cm.mastermap.get(f1).features.size());
                 
                 for (int j=0;j<nc;j++){
                     for (String f2:this.cm.mastermap.get(f1).features.get(j)){
                         //if (f1.equals(f2)){
                         //   continue;
                         //}
                         
                         String link="";
                         
                         if (f1.compareTo(f2)>0){
                            link=f1+"\" -- \""+f2;
                         }
                         else {
                              link=f2+"\" -- \""+f1;
                         }
                         if (clinked.contains(link)){
                            continue;
                         }
                         mentioned.add(f2);
                         clinked.add(link);
                         writer.write("\""+link+"\" [label=\""+this.cm.mastermap.get(f1).values.get(j)+"%\"];\n");
                     }
                 }

                 if (this.coex.mastermap.get(f1)!=null){

                     int ne=Math.min(this.parameters.getTopNCoex(),this.coex.mastermap.get(f1).features.size());
                     
                     for (int j=0;j<ne;j++){
                         for (String f2:this.coex.mastermap.get(f1).features.get(j)){
                             
                             String link="";
                             
                             if (f1.compareTo(f2)>0){
                                link=f1+"\" -- \""+f2;
                             }
                             else {
                                  link=f2+"\" -- \""+f1;
                             }
                             if (xlinked.contains(link)){
                                continue;
                             }
                             mentioned.add(f2);
                             xlinked.add(link);
                             writer.write("\""+link+"\" [label=\""+this.coex.mastermap.get(f1).values.get(j)+"\",style=dashed];\n");
                         }
                     }
                 }
                 for (String m:mentioned){
                     if (useful.contains(m)){
                        continue;
                     }
                     writeNode(m,writer);
                 }
             }
             writer.write("}\n");
      }
      
      /**
       * Static encapsulating actual graphmaking.
       * @param parameters
       */
      public static void makeGraph(PipelineParameters parameters){
             GraphFeatures executor=new GraphFeatures(parameters);
             
             try {
                 
                 FileWriter writer=new FileWriter(parameters.getGraphFilePath());
                 
                 executor.write(writer);
                 writer.close();
             }
             catch (IOException e){
                   System.err.println("Error while dumping graph file of feature relations.");
             }
      }
      
      /**
       * Processing of command-line parameters.
       * @param args
       */
      public static void main(String[] args){
          
             OptionManager options=new OptionManager(mandatoryOptions,optionalOptions,args);
             boolean approved=true;
             String errors=options.makeErrorMessages();
             
             if (!errors.equals("")){
                System.err.println(errors);
                approved=false;
             }
             if (!approved){
                System.err.println("Usage: java task.GraphFeatures <-h horizontal dataset> <-m mobra dataset> <-u utility file> <-o output file> [-"+
                                   PipelineParameters.MAX_NODES_GRAPH_PROPERTY+" max nodes] [-"+
                                   PipelineParameters.MAX_COOC_EDGES_GRAPH_PROPERTY+" max co-occurence edges] [-"+
                                   PipelineParameters.MAX_COEX_EDGES_GRAPH_PROPERTY+" max co-expression edges]");
                return;
             }

             Properties properties=new Properties();
             
             try {

                 InputStream inStream=ClassLoader.getSystemResourceAsStream(CompletePipeline.PIPELINE_PROPERTIES_FILE);
             
                 properties.load(inStream);
                 inStream.close();
             }
             catch (IOException e){
                   System.err.println("Error loading properties file.");
             }
             for (String option:options.getOptionalSet()){
                 if (options.containsOption(option)){
                    properties.setProperty(option.replace("-",""),options.getOption(option));      
                 }
             }
             
             PipelineParameters parameters=new PipelineParameters(properties,args);
             parameters.setHorizontalDatasetPath(options.getOption(HORIZONTAL_DATASET_OPTION));
             parameters.setMobraDatasetPath(options.getOption(MOBRA_DATASET_OPTION));
             parameters.setUtilityFile(options.getOption(UTILITY_FILE_OPTION));
             parameters.setGraphFilePath(options.getOption(OUTPUT_FILE_OPTION));
             
             makeGraph(parameters);
      }
}