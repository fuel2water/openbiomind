package snps;

import java.io.*;
import java.util.*;

import dataset.*;
import util.Randomizer;

/**
 * Stores and operates SNP data.
 * @author Lucio
 *
 */
public class SNPDataset{

      private List<SNPFeature> features=new ArrayList<SNPFeature>();
      private float dominantFrequency;
      private List<SNPSample> samples=new ArrayList<SNPSample>();
      private Map<String,Integer> id2Index=new HashMap<String,Integer>();
      private Map<String,SNPFeature> snpMap=new HashMap<String,SNPFeature>();
      private int maxFeatures;
      private int bottom;
    
      /**
       * Processing of file header common to the two constructors.
       * @param reader
       */
      private void crunchHeader(BufferedReader reader,String targetCategory) throws IOException{

             String[] idCols=reader.readLine().split("\t");
             String[] labelCols=reader.readLine().split("\t");
             int pos=0,neg=0;
             
             for (int i=1;i<labelCols.length;i++){
                 id2Index.put(idCols[i],i-1);
                 
                 SNPSample current=new SNPSample(this,idCols[i],labelCols[i],targetCategory);
                 
                 samples.add(current);
                 if (current.isCase()){
                    pos++;
                 }
                 else {
                      neg++;
                 }
             }
             this.dominantFrequency=Math.max(pos,neg)*1.0f/samples.size();
      }
      
      public boolean isHetero(String id,String snp){
             return snpMap.get(snp).isHetero(id2Index.get(id));
      }
      
      public List<String> listSNPs(){
             return new ArrayList<String>(snpMap.keySet());
      }
      
      public boolean isHomo(String id,String snp){
             return snpMap.get(snp).isHomo(id2Index.get(id));
      }
      
      public float getNumeric(String id,String snp){
             return snpMap.get(snp).getNumeric(id2Index.get(id));
      }
      
      public List<SNPSample> getSamples(){
             return samples;
      }
      
      /**
       * Constructs the "shell" of the dataset. Features are assumed to be added later by another class.
       * @param ids
       * @param labels
       * @param targetCategory
       * @param nFeatures
       */
      public SNPDataset(List<String> ids,List<String> labels,String targetCategory,int nFeatures){
             for (int i=0;i<ids.size();i++){
                 samples.add(new SNPSample(this,ids.get(i),labels.get(i),targetCategory));
                 id2Index.put(ids.get(i),i);
             }
             this.maxFeatures=nFeatures;
      }
      
      /**
       * Loads dataset and at the same time feature-selects it. The dataset actually stored is expected to have less
       * features (typically orders of magnitude less) than the original dataset.
       * @param fileName
       * @param targetCategory
       * @param numberOfFeatures
       * @throws FileNotFoundException
       * @throws IOException
       */
      public SNPDataset(Reader sourceReader,String targetCategory,int numberOfFeatures) throws IOException{
             this.maxFeatures=numberOfFeatures;
          
             BufferedReader reader=new BufferedReader(sourceReader);

             crunchHeader(reader,targetCategory);
             
             for (String line=reader.readLine();line!=null;line=reader.readLine()){

                 SNPFeature feature=new SNPFeature(line,samples);
                 
                 addFeature(feature);
             }
      }
    
      /**
       * Loads dataset without feature selection. Recommended for small/already-selected datasets, up to tens of 
       * thousands of features.
       * @param fileName
       * @param targetCategory
       * @throws FileNotFoundException
       * @throws IOException
       */
      public SNPDataset(Reader sourceReader,String targetCategory) throws IOException{
          
             BufferedReader reader=new BufferedReader(sourceReader);
             
             crunchHeader(reader,targetCategory);
             for (String line=reader.readLine();line!=null;line=reader.readLine()){

                 SNPFeature feature=new SNPFeature(line,samples);
                 
                 features.add(feature);
                 snpMap.put(feature.getID(),feature);
             }
      }

      public void shuffle(){
             for (int i=0;i<samples.size();i++){
                 
                 int j=Randomizer.getInstance().natural(samples.size());
                 int k=Randomizer.getInstance().natural(samples.size());
                 boolean saver=samples.get(j).getExpected();
                 
                 samples.get(j).setExpected(samples.get(k).getExpected());
                 samples.get(k).setExpected(saver);
             }
      }
      
      /**
       * Returns a train-test split of this dataset, in numeric form.
       * @param isTrain
       * @return
       */
      public List<Dataset> numericSplit(boolean[] isTrain){
          
             List<Entity> train=new ArrayList<Entity>();
             List<Entity> test=new ArrayList<Entity>();
             
             for (int i=0;i<isTrain.length;i++){
                 if (isTrain[i]){
                    train.add(samples.get(i).toEntity());
                 }
                 else {
                      test.add(samples.get(i).toEntity());
                 }
             }
             
             List<Dataset> output=new ArrayList<Dataset>();
             
             output.add(new Dataset(train));
             output.add(new Dataset(test));
             for (SNPFeature feature:features){
                 output.get(0).setDescription(feature.getID(),feature.describeMapping());
                 output.get(1).setDescription(feature.getID(),feature.describeMapping());
             }
             return output;
      }
      
      /**
       * Fills the most differentiated features list, one feature at a time.
       * @param feature
       */
      public void addFeature(SNPFeature feature){
             if (features.size()<this.maxFeatures){
                System.out.println("Adding "+feature.getID());
                features.add(feature);
                this.snpMap.put(feature.getID(),feature);
                if (feature.getDifferentiation()<features.get(bottom).getDifferentiation()){
                   bottom=features.size()-1;
                }
                return;
             }
             if (feature.getDifferentiation()<=features.get(bottom).getDifferentiation()){
                return;
             }
             System.out.println("Replacing "+features.get(bottom).getDifferentiation()+" by "+feature.getDifferentiation());
             this.snpMap.remove(features.get(bottom).getID());
             this.snpMap.put(feature.getID(),feature);
             this.features.set(bottom,feature);
             for (int i=0;i<features.size();i++){
                 if (features.get(i).getDifferentiation()<features.get(bottom).getDifferentiation()){
                    bottom=i;
                 }
             }
      }
      
      public float getDominantFrequency(){
             return dominantFrequency;
      }
      
      /**
       * Writes a text representation of this dataset at the given writer.
       * @param writer
       */
      public void write(Writer writer) throws IOException{
             for (SNPSample sample:samples){
                 writer.write("\t"+sample.getID());
             }
             writer.write('\n');
             for (SNPSample sample:samples){
                 writer.write("\t"+sample.getCategory());
             }
             writer.write('\n');
             for (SNPFeature feature:features){
                 feature.write(writer);
             }
      }
      
      /**
       * Writes this dataset as a conventional numeric dataset used by methods that are not SNP-specific.
       * Descriptions specify the numeric mapping used for each feature.
       * @param writer
       * @throws IOException
       */
      public void writeAsNumeric(Writer writer) throws IOException{
             writer.write("\t");
             for (SNPSample sample:samples){
                 writer.write("\t"+sample.getID());
             }
             writer.write('\n');
             for (SNPSample sample:samples){
                 writer.write("\t"+sample.getCategory());
             }
             writer.write('\n');
             for (SNPFeature feature:features){
                 feature.writeAsNumeric(writer);
             }
      }
      
      /**
       * Temporary main for testing feature selection.
       * @param args
       */
      public static void main(String args[]){
          
             if (args.length!=4){
                System.out.println("Usage: java snps.SNPDataset <dataset> <target category> <number of features> <output file>");
                return;
             }
          
             SNPDataset dataset=null;
             String fileName=args[0];
             String targetCategory=args[1];
             int numberOfFeatures=Integer.valueOf(args[2]);
             String outfile=args[3];
          
             try {
                 
                 FileReader reader=new FileReader(fileName);
                 
                 dataset=new SNPDataset(reader,targetCategory,numberOfFeatures);
                 reader.close();
             }
             catch (IOException e){
                   System.err.println("Error while loading SNP dataset "+fileName);
             }
             
             try {
                 
                 FileWriter writer=new FileWriter(outfile);
                 
                 dataset.write(writer);
                 writer.close();
             }
             catch (IOException e){
                   System.err.println("Error while writing selected dataset");
             }
      }
      
}