package task;

import java.io.*;
import java.util.*;

import clustering.*;
import util.*;

/**
 * Draws a graph (in Dot Language) of the most important features of a metatask, showing co-occurrence and co-expression
 * relations between them.
 * @author Lucio
 *
 */
class SimpleGraph{
    
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
      }

      class Edge{

            String f1,f2;
            float value;
            boolean isCooc;

            Edge(String f1,String f2,float value,boolean isCooc){
                this.f1=f1;
                this.f2=f2;
                this.value=value;
                this.isCooc=isCooc;
            }

      }

      private List<Edge> crunchMOBRAData(Reader reader,int topn) throws IOException{

              BufferedReader buffer=new BufferedReader(reader);
              String[] firstCols=buffer.readLine().split("\t");
              List<Edge> output=new ArrayList<Edge>();

              for (String line=buffer.readLine();line!=null;line=buffer.readLine()){

                  String[] cols=line.split("\t");
                  String f1=cols[0];

                  for (int i=2;i<cols.length;i++){

                      String f2=firstCols[i];

                      if (f1.compareTo(f2)>=0){
                         continue;
                      }

                      float cooc=Float.valueOf(cols[i]);
                      Edge edge=new Edge(f1,f2,cooc,true);

                      if (output.size()<topn){
                         output.add(edge);
                         continue;
                      }

                      int smallest=0;

                      for (int j=1;j<output.size();j++)
                          if (output.get(j).value<output.get(smallest).value)
                             smallest=j;
                      if (output.get(smallest).value<edge.value)
                         output.set(smallest,edge);
                  }
              }
              return output;
      }

      private void describeNode(Writer writer,String nodeName) throws IOException{
              writer.write("\""+nodeName+"\" [\n");

              FeatureInfo fi=ut.getFeature(nodeName);

              writer.write("label = \"{"+nodeName+" | "+fi.featureDescription+" | {"+fi.featureUtility+"% | "+fi.uRank+" | "+fi.diffRank+" | "+fi.samRank+"}}\",\n");
              writer.write("shape = \"record\"\n");
              writer.write("];\n");
      }

      /**
       * Dumping of a Dot-specified graph of the relations according to the parameters on number of edges.
       * @param fileName
       */
      public void write(Writer writer) throws IOException{
             writer.write("graph G {\n");

             Set<String> described=new HashSet<String>();

             for (Edge edge:topEdges){
                 if (!described.contains(edge.f1)){
                    describeNode(writer,edge.f1);
                    described.add(edge.f1);
                 }
                 if (!described.contains(edge.f2)){
                    describeNode(writer,edge.f2);
                    described.add(edge.f2);
                 }
                 if (edge.isCooc)
                    writer.write(edge.f1+" -- "+edge.f2+"\n");
                 else writer.write(edge.f1+" -- "+edge.f2+" [style=dashed]\n");
             }
             writer.write("}\n");
      }

      private List<Edge> topEdges;
      private PipelineParameters parameters;
      private UtilityTable ut;

      private float computeCoex(String f1,String f2,ClusteringDataset horizontal){

              List<Float> v1s=horizontal.getElement(f1).getValues();
              List<Float> v2s=horizontal.getElement(f2).getValues();
              float sum=0.0f;

              for (int i=0;i<v1s.size();i++){
                  sum+=Math.min(v1s.get(i),v2s.get(i))/Math.max(v1s.get(i),v2s.get(i));
              }
              return sum/v1s.size();
      }

      private void stitcher(ClusteringDataset horizontal){

              Set<String> group=new HashSet<String>();
              boolean growth=true;              

              while (growth){
                      growth=false;
		      for (Edge edge:topEdges){
		          if (group.size()==0){
		             group.add(edge.f1);
		             group.add(edge.f2);
                             growth=true;
		             continue;
		          }
		          if (group.contains(edge.f1)&&group.contains(edge.f2)){
                             continue;
                          }
		          if (group.contains(edge.f1)||group.contains(edge.f2)){
		             group.add(edge.f1);
		             group.add(edge.f2);
                             growth=true;
		          }
		      }
              }
              for (String e:group)
                  System.out.println(e);

              Set<String> rest=new HashSet<String>();

	      for (Edge edge:topEdges){
                  if (!group.contains(edge.f1))
                     rest.add(edge.f1);
                  if (!group.contains(edge.f2))
                     rest.add(edge.f2);
              }
              if (rest.size()==0)
                 return;

              Edge winner=null;

              for (String f1:group)
                  for (String f2:rest){
                      if (winner==null){
                         winner=new Edge(f1,f2,computeCoex(f1,f2,horizontal),false);
                         continue;
                      }

                      Edge candidate=new Edge(f1,f2,computeCoex(f1,f2,horizontal),false);

                      if (candidate.value>winner.value)
                         winner=candidate;
                  }                      
              topEdges.add(winner);
              stitcher(horizontal);
      }

      public SimpleGraph(PipelineParameters parameters){
             this.parameters=parameters;
             System.out.println("Loading "+parameters.getUtilityFile());
             try {
                 
                 FileReader reader=new FileReader(parameters.getUtilityFile());
                 
                 ut=new UtilityTable(reader);
                 reader.close();
             }
             catch (IOException e){
                   System.err.println("Error while loading feature utility file.");
                   System.exit(-1);
             }
             System.out.println("Loading "+parameters.getMobraDatasetPath());
             try {
                 
                 FileReader reader=new FileReader(parameters.getMobraDatasetPath());
                 
                 topEdges=crunchMOBRAData(reader,10);
                 reader.close();
             }
             catch (IOException e){
                   System.err.println("Error while loading feature utility file.");
                   System.exit(-1);
             }
             System.out.println("Loading "+parameters.getHorizontalDatasetPath());
             try {
                 
                 FileReader reader=new FileReader(parameters.getHorizontalDatasetPath());
                 
                 stitcher(new ClusteringDataset(reader));
                 reader.close();
             }
             catch (IOException e){
                   System.err.println("Error while loading feature utility file.");
                   System.exit(-1);
             }
      }      

      /**
       * Static encapsulating actual graphmaking.
       * @param parameters
       */
      public static void makeGraph(PipelineParameters parameters){

             SimpleGraph executor=new SimpleGraph(parameters);

             try {
                 
                 FileWriter writer=new FileWriter(parameters.getGraphFilePath());
                 
                 executor.write(writer);
                 writer.close();
             }
             catch (IOException e){
                   System.err.println("Error while dumping graph file of feature relations.");
                   System.exit(-1);
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
                System.err.println("Usage: java task.SimpleGraph <-h horizontal dataset> <-m mobra dataset> <-u utility file> <-o output file>");
                System.exit(-1);
             }

             Properties properties=new Properties();
             
             try {

                 InputStream inStream=ClassLoader.getSystemResourceAsStream(CompletePipeline.PIPELINE_PROPERTIES_FILE);
             
                 properties.load(inStream);
                 inStream.close();
             }
             catch (IOException e){
                   System.err.println("Error loading properties file.");
                   System.exit(-1);
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
