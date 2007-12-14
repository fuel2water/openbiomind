package task;

import java.io.*;
import java.util.*;

import classification.Classifier;
import dataset.Dataset;

/**
 * Computes and retrieves frequency-based feature utility.
 * @author emac
 *
 */
public class SNPUtilityComputer{
    
      private static final Set<String> mandatoryOptions=new HashSet<String>();
      private static final Set<String> optionalOptions=new HashSet<String>();
      private static final String RESULTS_DIRECTORY_OPTION="-r";
      private static final String OUTPUT_DIRECTORY_OPTION="-o";
      private static final String BASE_DATASET_OPTION="-d";
      private static final String SNP2GENE_OPTION="-m";
    
      static {
             mandatoryOptions.add(RESULTS_DIRECTORY_OPTION);
             mandatoryOptions.add(OUTPUT_DIRECTORY_OPTION);
             mandatoryOptions.add(BASE_DATASET_OPTION);
             mandatoryOptions.add(SNP2GENE_OPTION);
             optionalOptions.add("-"+PipelineParameters.TARGET_CATEGORY_PROPERTY);
      }
      
      private Map<String,String> snp2gene;
      private Map<String,Set<String>> gene2snps=new HashMap<String,Set<String>>();
      private Map<String,String> gene2description;
      private Map<String,Integer> gene2count=new HashMap<String,Integer>();
      private int models;
      private UtilityComputer uc;
      
      /**
       * Computes feature utility from a metatask result.
       * @param metaResult
       */
      public SNPUtilityComputer(MetaTaskResult metaResult,Dataset dataset,Map<String,String> snp2gene,Map<String,String> gene2description){
             uc=new UtilityComputer(metaResult,dataset);
             this.snp2gene=snp2gene;
             this.gene2description=gene2description;
             for (ClassificationTaskResult task:metaResult.getTaskResults()){
                 for (FoldResult fold:task.getFolds()){
                     for (Classifier c:fold.getEnsemble().getComponents()){
                         this.models+=1;
                         
                         Set<String> genes=new HashSet<String>();
                         
                         for (String f:c.featureSet()){
                             if (!snp2gene.containsKey(f)){
                                continue;
                             }
                             
                             String gene=snp2gene.get(f);
                             
                             genes.add(gene);
                             if (!gene2snps.containsKey(gene)){
                                gene2snps.put(gene,new HashSet<String>());
                             }
                             gene2snps.get(gene).add(f);
                         }
                         for (String gene:genes){
                             if (!gene2count.containsKey(gene)){
                                gene2count.put(gene,0);
                             }
                             gene2count.put(gene,gene2count.get(gene)+1);
                         }
                     }
                 }
             }
      }
      
      private static void loadMap(Reader reader,Map<String,String> snp2gene,Map<String,String> gene2description) throws IOException{
          
              BufferedReader buffer=new BufferedReader(reader);
              
              for (String line=buffer.readLine();line!=null;line=buffer.readLine()){
                  
                  String[] cols=line.split("\t");
                  
                  if (cols.length!=3){
                     continue;
                  }
                  snp2gene.put(cols[0],cols[1]);
                  gene2description.put(cols[1],cols[2]);
              }
      }
      
      private float averageDiff(String gene){
          
              float sum=0.0f;
              
              for (String snp:gene2snps.get(gene)){
                  sum+=uc.getDifferentiationOf(snp);
              }
              return sum/gene2snps.get(gene).size();
      }
      
      private float averageSAM(String gene){
          
              float sum=0.0f;
              
              for (String snp:gene2snps.get(gene)){
                  sum+=uc.getSAMOf(snp);
              }
              return sum/gene2snps.get(gene).size();
      }
      
      /**
       * Writes a list of important *genes*, according to the frequency of their SNPs.
       * @param writer
       */
      public void writeGeneOriented(Writer writer) throws IOException{
          
             Map<Integer,List<String>> count2genes=new HashMap<Integer,List<String>>();
             Map<String,Float> gene2diff=new HashMap<String,Float>();
             Map<String,Float> gene2sam=new HashMap<String,Float>();
             Set<Float> diffSet=new HashSet<Float>();
             Set<Float> samSet=new HashSet<Float>();
             
             for (String gene:this.gene2count.keySet()){
                 
                 int count=this.gene2count.get(gene);
                 
                 if (!count2genes.containsKey(count)){
                    count2genes.put(count,new ArrayList<String>());
                 }
                 count2genes.get(count).add(gene);
                 gene2diff.put(gene,averageDiff(gene));
                 diffSet.add(gene2diff.get(gene));
                 gene2sam.put(gene,averageSAM(gene));
                 samSet.add(gene2sam.get(gene));
             }
             
             List<Integer> counts=new ArrayList<Integer>(count2genes.keySet());
             
             Collections.sort(counts);
             Collections.reverse(counts);
             
             List<Float> diffs=new ArrayList<Float>(diffSet);
             
             Collections.sort(diffs);
             Collections.reverse(diffs);
             
             List<Float> sams=new ArrayList<Float>(samSet);
             int rank=1;
             
             Collections.sort(sams);
             Collections.reverse(sams);
             for (Integer c:counts){
                 for (String gene:count2genes.get(c)){
                     writer.write(gene+"\t"+c*1.0f/this.models*100+"\t"+rank+"\t"+(diffs.indexOf(gene2diff.get(gene))+1)+"\t"+(sams.indexOf(gene2sam.get(gene))+1)+"\t"+gene2description.get(gene)+"\n");
                 }
                 rank+=1;
             }
      }
      
      /**
       * Writes a list of utilities and associated data for SNPs.
       * @param writer
       */
      public void writeSNPOriented(Writer writer) throws IOException{
          
             Map<Float,ArrayList<String>> v2f=new HashMap<Float,ArrayList<String>>();
          
             for (String feature:this.uc.getFeatures()){
                 
                 float utility=uc.getUtilityOf(feature);
                 
                 if (!v2f.containsKey(utility)){
                    v2f.put(utility,new ArrayList<String>());
                 }
                 v2f.get(utility).add(feature);
             }
             
             ArrayList<Float> sorter=new ArrayList<Float>(v2f.keySet());
             int rank=1;
                     
             Collections.sort(sorter);
             Collections.reverse(sorter);
             for (float utility:sorter){
                 for (String feature:v2f.get(utility)){
                     writer.write(feature+"\t"+utility+"\t"+rank+"\t"+uc.getDiffRankOf(feature)+"\t"+uc.getSAMRank(feature)+"\t"+mkGeneDescriptionFor(feature)+"\n");
                 }
                 rank+=1;
             }
      }

      private String mkGeneDescriptionFor(String feature){
              if (!this.snp2gene.containsKey(feature)){
                 return "";
              }
              if (this.snp2gene.get(feature).equals("")){
                 return "";
              }
              return this.snp2gene.get(feature)+": "+this.gene2description.get(this.snp2gene.get(feature));
      }
      
      /**
       * Execution flow.
       * @param parameters
       */
      public static void execute(PipelineParameters parameters){
             String resultDir=parameters.getMetataskOutputPath();
             String outputDir=parameters.getSNPUtilityDir();
             String datasetName=parameters.getDatasetPath();
             String category=parameters.getTargetCategory();
             String snp2geneFile=parameters.getSNP2Gene();
             MetaTaskResult result=null;
             Dataset dataset=null;
             
             try {
                 result=new MetaTaskResult(resultDir);
                 
                 FileReader reader=new FileReader(datasetName);
                 
                 dataset=new Dataset(reader,category);
                 reader.close();
             }
             catch (IOException e){
                   System.err.println("Error while loading metatask result from "+resultDir);
             }
             
             Map<String,String> snp2gene=new HashMap<String,String>();
             Map<String,String> gene2description=new HashMap<String,String>();

             try {

                 FileReader reader=new FileReader(snp2geneFile);
                 
                 loadMap(reader,snp2gene,gene2description);
                 reader.close();
             }
             catch (IOException e){
                   System.err.println("Error while loading SNP to gene mapping from "+snp2geneFile);
             }
             
             SNPUtilityComputer suc=new SNPUtilityComputer(result,dataset,snp2gene,gene2description);
             String outsnps=outputDir+"/util_snps.txt";
             File checker=new File(outputDir);
                 
             if (!checker.exists()){
                checker.mkdir();
             }
             try {
                 
                 FileWriter writer=new FileWriter(outsnps);
                 
                 suc.writeSNPOriented(writer);
                 writer.close();
             }
             catch (IOException e){
                   System.err.println("Error while saving SNP-based utilities at "+outsnps);
             }

             String outgenes=outputDir+"/util_genes.txt";
             
             try {
                 
                 FileWriter writer=new FileWriter(outgenes);
                 
                 suc.writeGeneOriented(writer);
                 writer.close();
             }
             catch (IOException e){
                   System.err.println("Error while saving gene-based utilities at "+outgenes);
             }
      }
      
      /**
       * Computes feature utilities for a given set of task outputs and dumps result in a file.
       * @param args
       */
      public static void main(String[] args){
             OptionManager options=new OptionManager(mandatoryOptions,optionalOptions,args);
             boolean approved=true;
             String errors=options.makeErrorMessages();
             
             if (!errors.equals("")){
                System.out.println(errors);
                approved=false;
             }
             if (!approved){
                System.out.println("Usage: java task.SNPUtilityComputer <-r result dir> <-o output dir> <-d base dataset> <-m snp 2 gene file> [-"+
                        PipelineParameters.TARGET_CATEGORY_PROPERTY+" target category]");
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
             
             parameters.setMetataskOutputPath(options.getOption(RESULTS_DIRECTORY_OPTION));
             parameters.setSNPUtilityDir(options.getOption(OUTPUT_DIRECTORY_OPTION));
             parameters.setDatasetPath(options.getOption(BASE_DATASET_OPTION));
             parameters.setSNP2Gene(options.getOption(SNP2GENE_OPTION));
             execute(parameters);
      }
}