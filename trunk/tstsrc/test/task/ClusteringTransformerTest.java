package test.task;

import junit.framework.*;

import java.io.*;

import task.ClusteringTransformer;
import util.LineTagger;

public class ClusteringTransformerTest extends TestCase{

       private static final String INPUT_DATASET="tstfiles/varm126.tab";
       private static final String STANDARD_HORIZONTAL="tstfiles/horizontal.tab";
       private static final String OUTPUT_HORIZONTAL="tstfiles/outhorizontal.tab";

       private void callAsRuntime(String[] args) throws IOException{

               StringBuffer cmdString=new StringBuffer();

               cmdString.append("java -cp classes task.ClusteringTransformer");
               for (String arg:args){
                   cmdString.append(" ");
                   cmdString.append(arg);
               }

               String strFinal=cmdString.toString();

               System.out.println(strFinal);

               BufferedReader errorReader=new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(strFinal).getErrorStream()));

               System.out.println("Errors in system call: ");
               for (String line=errorReader.readLine();line!=null;line=errorReader.readLine())
                   System.out.println(line);             
       }

       public void testHorizontal() throws Exception{
              
              String[] args=new String[6];

              System.out.println("Setting parameters");
              args[0]="-d";
              args[1]=INPUT_DATASET;
              args[2]="-t";
              args[3]=ClusteringTransformer.DATASET_HORIZONTAL;
              args[4]="-o";
              args[5]=OUTPUT_HORIZONTAL;
              System.out.println("Checking output file");
              checkTransient(OUTPUT_HORIZONTAL);
              System.out.println("Calling transformation");
              //ClusteringTransformer.main(args);
              callAsRuntime(args);
              System.out.println("Testing assertions");
              assertTrue("Output file not generated in horizontal transformation",(new File(OUTPUT_HORIZONTAL)).exists());
              assertTrue("Resulting transformation does not match expected result",compareFiles(STANDARD_HORIZONTAL,OUTPUT_HORIZONTAL));
              System.out.println("End of test");
       }

       private boolean compareFiles(String expected,String computed) throws IOException{

               LineTagger file1=new LineTagger(expected);
               LineTagger file2=new LineTagger(computed);

               if (file1.size()!=file2.size())
                  return false;
               for (int i=0;i<file1.size();i++)
                   if (!file1.getLines().get(i).equals(file2.getLines().get(i)))
                      return false;
               return true;
       }

       private void checkTransient(String fileName){

               File file=new File(fileName);

               if (file.exists()){
                  file.delete();
               }
       }

}

