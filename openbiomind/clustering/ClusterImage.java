package clustering;

import java.io.*;
import java.util.*;

//import java.awt.Font;
//import java.awt.Color;
import java.awt.image.*;
import javax.imageio.*;

/**
 * Generates, stores and operates a raster image representing a clustering.
 * @author Lucio
 *
 */
public class ClusterImage{
    
       private static final int CHAR_HEIGHT=6;
       private static final int CHAR_WIDTH=6;
    
       private int rows;
       private int cols;
       private int height;
       private int width;
       private int longest=0;
       private int vlongest=0;
       private BufferedImage image;
       private boolean traditionalColors;
    
       public ClusterImage(OmniClustering clustering,boolean traditionalColors){
              this.traditionalColors=traditionalColors;
              for (Cluster cluster:clustering.getResults()){
                  for (ClusterElement element:cluster.getElements()){
                      
                      String description=element.getID()+": "+clustering.getData().describe(element.getID());
                      
                      if (description.length()>longest){
                         longest=description.length();
                      }
                  }
              }
              for (String col:clustering.getData().getColumnNames()){
                  if (col.length()>vlongest){
                     vlongest=col.length();
                  }
              }
              rows=clustering.totalElements()+clustering.totalClusters()+vlongest;
              cols=clustering.vectorLength();
              //System.out.println(cols+" columns in clustering dataset");
              height=rows*CHAR_HEIGHT;
              width=(longest+cols)*CHAR_WIDTH;
              image=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
              for (int i=0;i<height;i++){
                  for (int j=0;j<width;j++){
                      setRGB(i,j,255,255,255);
                  }
              }
              represent(clustering);
       }
       
       private void setRGB(int i,int j,int r,int g,int b){
           
                    int rgb=((0x00) <<24) | ((r & 0xFF) <<16) | ((g & 0xFF) <<8) | ((b & 0xFF) <<0);
                    
                    image.setRGB(j,i,rgb);
       }
       
       /**
        * Harcoded character map. I preferred this unelegant solution because it gives me complete control of the
        * size in pixels of characters, while using predefined fonts may present issues of point/pixel size of 
        * characters exceeding limits...
        */
       private static final Map<Character,List<String>> c2pm=new HashMap<Character,List<String>>();
       
       static {
           
              List<String> lines;

              lines=new ArrayList<String>();
              lines.add("11100");
              lines.add("10010");
              lines.add("10001");
              lines.add("10010");
              lines.add("11100");
              c2pm.put('D',lines);
              lines=new ArrayList<String>();
              lines.add("11111");
              lines.add("10101");
              lines.add("10101");
              lines.add("10101");
              lines.add("10101");
              c2pm.put('M',lines);
              lines=new ArrayList<String>();
              lines.add("10001");
              lines.add("10010");
              lines.add("11100");
              lines.add("10010");
              lines.add("10001");
              c2pm.put('K',lines);
              lines=new ArrayList<String>();
              lines.add("10001");
              lines.add("11001");
              lines.add("10101");
              lines.add("10011");
              lines.add("10001");
              c2pm.put('N',lines);
              lines=new ArrayList<String>();
              lines.add("11110");
              lines.add("10001");
              lines.add("11110");
              lines.add("10010");
              lines.add("10001");
              c2pm.put('R',lines);
              lines=new ArrayList<String>();
              lines.add("11111");
              lines.add("00100");
              lines.add("00100");
              lines.add("00100");
              lines.add("00100");
              c2pm.put('T',lines);
              lines=new ArrayList<String>();
              lines.add("11111");
              lines.add("10000");
              lines.add("11111");
              lines.add("00001");
              lines.add("11111");
              c2pm.put('5',lines);
              lines=new ArrayList<String>();
              lines.add("00100");
              lines.add("01100");
              lines.add("10100");
              lines.add("00100");
              lines.add("01110");
              c2pm.put('1',lines);
              lines=new ArrayList<String>();
              lines.add("01111");
              lines.add("10000");
              lines.add("01110");
              lines.add("00001");
              lines.add("11110");
              c2pm.put('S',lines);
              lines=new ArrayList<String>();
              lines.add("11111");
              lines.add("10000");
              lines.add("11111");
              lines.add("10000");
              lines.add("11111");
              c2pm.put('E',lines);
              lines=new ArrayList<String>();
              lines.add("10000");
              lines.add("10000");
              lines.add("10000");
              lines.add("10000");
              lines.add("11111");
              c2pm.put('L',lines);
              lines=new ArrayList<String>();
              lines.add("11111");
              lines.add("10001");
              lines.add("11111");
              lines.add("10000");
              lines.add("10000");
              c2pm.put('P',lines);
              lines=new ArrayList<String>();
              lines.add("11111");
              lines.add("10011");
              lines.add("10101");
              lines.add("11001");
              lines.add("11111");
              c2pm.put('0',lines);
              lines=new ArrayList<String>();
              lines.add("11111");
              lines.add("10001");
              lines.add("11111");
              lines.add("10001");
              lines.add("10001");
              c2pm.put('A',lines);
              lines=new ArrayList<String>();
              lines.add("11111");
              lines.add("10001");
              lines.add("11111");
              lines.add("00001");
              lines.add("11111");
              c2pm.put('9',lines);
              lines=new ArrayList<String>();
              lines.add("11111");
              lines.add("00001");
              lines.add("00001");
              lines.add("00001");
              lines.add("00001");
              c2pm.put('7',lines);
              lines=new ArrayList<String>();
              lines.add("11111");
              lines.add("00001");
              lines.add("11111");
              lines.add("00001");
              lines.add("11111");
              c2pm.put('3',lines);
              lines=new ArrayList<String>();
              lines.add("00000");
              lines.add("00000");
              lines.add("11111");
              lines.add("00000");
              lines.add("00000");
              c2pm.put('-',lines);
              lines=new ArrayList<String>();
              lines.add("10001");
              lines.add("10001");
              lines.add("11111");
              lines.add("00001");
              lines.add("00001");
              c2pm.put('4',lines);
              lines=new ArrayList<String>();
              lines.add("11111");
              lines.add("10000");
              lines.add("10111");
              lines.add("10001");
              lines.add("11111");
              c2pm.put('G',lines);
              lines=new ArrayList<String>();
              lines.add("11110");
              lines.add("10001");
              lines.add("11110");
              lines.add("10001");
              lines.add("11110");
              c2pm.put('B',lines);
              lines=new ArrayList<String>();
              lines.add("11111");
              lines.add("00001");
              lines.add("11111");
              lines.add("10000");
              lines.add("11111");
              c2pm.put('2',lines);
              lines=new ArrayList<String>();
              lines.add("11111");
              lines.add("10000");
              lines.add("10000");
              lines.add("10000");
              lines.add("11111");
              c2pm.put('C',lines);
              lines=new ArrayList<String>();
              lines.add("10001");
              lines.add("10001");
              lines.add("11111");
              lines.add("10001");
              lines.add("10001");
              c2pm.put('H',lines);
              lines=new ArrayList<String>();
              lines.add("11111");
              lines.add("00010");
              lines.add("00100");
              lines.add("01000");
              lines.add("11111");
              c2pm.put('Z',lines);
              lines=new ArrayList<String>();
              lines.add("11111");
              lines.add("10000");
              lines.add("11111");
              lines.add("10000");
              lines.add("10000");
              c2pm.put('F',lines);
              lines=new ArrayList<String>();
              lines.add("11111");
              lines.add("10001");
              lines.add("11111");
              lines.add("10001");
              lines.add("11111");
              c2pm.put('8',lines);
              lines=new ArrayList<String>();
              lines.add("11111");
              lines.add("10001");
              lines.add("10001");
              lines.add("10001");
              lines.add("11111");
              c2pm.put('O',lines);
              lines=new ArrayList<String>();
              lines.add("10001");
              lines.add("01010");
              lines.add("00100");
              lines.add("01010");
              lines.add("10001");
              c2pm.put('X',lines);
              lines=new ArrayList<String>();
              lines.add("01110");
              lines.add("00100");
              lines.add("00100");
              lines.add("00100");
              lines.add("01110");
              c2pm.put('I',lines);
              lines=new ArrayList<String>();
              lines.add("10001");
              lines.add("01010");
              lines.add("00100");
              lines.add("00100");
              lines.add("00100");
              c2pm.put('Y',lines);
              lines=new ArrayList<String>();
              lines.add("00001");
              lines.add("00001");
              lines.add("10001");
              lines.add("10001");
              lines.add("11111");
              c2pm.put('J',lines);
              lines=new ArrayList<String>();
              lines.add("11111");
              lines.add("10000");
              lines.add("11111");
              lines.add("10001");
              lines.add("11111");
              c2pm.put('6',lines);
              lines=new ArrayList<String>();
              lines.add("10001");
              lines.add("10001");
              lines.add("10001");
              lines.add("01010");
              lines.add("00100");
              c2pm.put('V',lines);
              lines=new ArrayList<String>();
              lines.add("10001");
              lines.add("10001");
              lines.add("10001");
              lines.add("10001");
              lines.add("11111");
              c2pm.put('U',lines);
              lines=new ArrayList<String>();
              lines.add("11111");
              lines.add("10001");
              lines.add("10111");
              lines.add("10101");
              lines.add("11111");
              c2pm.put('Q',lines);
              lines=new ArrayList<String>();
              lines.add("10101");
              lines.add("10101");
              lines.add("10101");
              lines.add("10101");
              lines.add("11111");
              c2pm.put('W',lines);
              lines=new ArrayList<String>();
              lines.add("00000");
              lines.add("00000");
              lines.add("00000");
              lines.add("00011");
              lines.add("00011");
              c2pm.put('.',lines);
              lines=new ArrayList<String>();
              lines.add("00011");
              lines.add("00011");
              lines.add("00000");
              lines.add("00011");
              lines.add("00011");
              c2pm.put(':',lines);
              lines=new ArrayList<String>();
              lines.add("00000");
              lines.add("00000");
              lines.add("00000");
              lines.add("00000");
              lines.add("00000");
              c2pm.put(' ',lines);
              lines=new ArrayList<String>();
              lines.add("00000");
              lines.add("00000");
              lines.add("00000");
              lines.add("00011");
              lines.add("00001");
              c2pm.put(',',lines);
              lines=new ArrayList<String>();
              lines.add("01100");
              lines.add("10000");
              lines.add("10000");
              lines.add("10000");
              lines.add("01100");
              c2pm.put('(',lines);
              lines=new ArrayList<String>();
              lines.add("00110");
              lines.add("00001");
              lines.add("00001");
              lines.add("00001");
              lines.add("00110");
              c2pm.put(')',lines);
              lines=new ArrayList<String>();
              lines.add("10000");
              lines.add("01000");
              lines.add("00100");
              lines.add("00010");
              lines.add("00001");
              c2pm.put('\'',lines);
              lines=new ArrayList<String>();
              lines.add("00001");
              lines.add("00010");
              lines.add("00100");
              lines.add("01000");
              lines.add("10000");
              c2pm.put('/',lines);
              lines=new ArrayList<String>();
              lines.add("00011");
              lines.add("00011");
              lines.add("00000");
              lines.add("00011");
              lines.add("00001");
              c2pm.put(';',lines);
              lines=new ArrayList<String>();
              lines.add("00100");
              lines.add("00100");
              lines.add("11111");
              lines.add("00100");
              lines.add("00100");
              c2pm.put('+',lines);
              lines=new ArrayList<String>();
              lines.add("00100");
              lines.add("01000");
              lines.add("10000");
              lines.add("01000");
              lines.add("00100");
              c2pm.put('<',lines);
              lines=new ArrayList<String>();
              lines.add("00100");
              lines.add("00010");
              lines.add("00001");
              lines.add("00010");
              lines.add("00100");
              c2pm.put('>',lines);
           /*
              lines=new ArrayList<String>();
              lines.add("00000");
              lines.add("00000");
              lines.add("00000");
              lines.add("00000");
              lines.add("00000");
            */
              lines=new ArrayList<String>();
              lines.add("00000");
              lines.add("00000");
              lines.add("00000");
              lines.add("00000");
              lines.add("11111");
              c2pm.put('_',lines);
              lines=new ArrayList<String>();
              lines.add("11000");
              lines.add("10000");
              lines.add("10000");
              lines.add("10000");
              lines.add("11000");
              c2pm.put('[',lines);
              lines=new ArrayList<String>();
              lines.add("00011");
              lines.add("00001");
              lines.add("00001");
              lines.add("00001");
              lines.add("00011");
              c2pm.put(']',lines);
       }
       
       private void scribe(int row,int col,char letter){
               //System.out.println(letter);
               
               List<String> pixMap=c2pm.get(letter);
               
               for (int i=0;i<pixMap.size();i++){
                   for (int j=0;j<pixMap.get(i).length();j++){
                       if (pixMap.get(i).charAt(j)=='1'){
                          setRGB(row*CHAR_HEIGHT+i,col*CHAR_WIDTH+j,0,0,0);
                       }
                   }
               }
       }
       
       /*private void vscribe(int row,int col,char letter){
               //System.out.println(letter);
               
               List<String> pixMap=c2pm.get(letter);
               
               for (int i=0;i<pixMap.size();i++){
                   for (int j=0;j<pixMap.get(i).length();j++){
                       if (pixMap.get(i).charAt(j)=='1'){
                          setRGB(row*CHAR_HEIGHT+j,col*CHAR_WIDTH+CHAR_WIDTH-i-1,0,0,0);
                       }
                   }
               }
       }*/
       
       private void scribe(int row,int col,String word){
               word=word.toUpperCase();
               for (int i=0;i<word.length();i++){
                   scribe(row,col+i,word.charAt(i));
               }
       }
       
       private void vscribe(int row,int col,String word){
               word=word.toUpperCase();
               for (int i=0;i<word.length();i++){
                   scribe(row+i,col,word.charAt(i));
               }
       }
       
       private void square(int row,int col,float intensity,float avg){
           
               int iStart=row*CHAR_HEIGHT;
               int jStart=col*CHAR_WIDTH;
               int red=0;
               int green=0;
               
               if (this.traditionalColors){
                   if (intensity>avg){
                      red=(int)((intensity-avg)*2*255);
                   }
                   
                   if (intensity<avg){
                      green=(int)((avg-intensity)*2*255);
                   }
               }
               else {
                    red=(int)(intensity*255);
               }
               for (int i=0;i<CHAR_HEIGHT;i++){
                   for (int j=0;j<CHAR_WIDTH;j++){
                       setRGB(iStart+i,jStart+j,red,green,0);
                   }
               }
       }
       
       /**
        * Effectively draws the clustering image in an appropriately dimensioned image.
        * @param clustering
        */
       private void represent(OmniClustering clustering){
           
               int row=vlongest;
               int col=0;

               //image.getGraphics().setColor(Color.BLACK);
               //image.getGraphics().setFont(new Font("Courier",Font.PLAIN,8));
               //image.getGraphics().drawString("teste",8,8);
               for (int i=0;i<clustering.getData().getColumnNames().size();i++){
                   
                   String colName=clustering.getData().getColumnNames().get(i);
                   
                   vscribe(vlongest-colName.length(),i+longest,colName);
               }
               for (Cluster cluster:clustering.getResults()){
                   for (ClusterElement element:cluster.getElements()){
                       
                       String description=element.getID()+": "+clustering.getData().describe(element.getID());
                       
                       scribe(row,col+longest-description.length(),description);
                       
                       float upper=Float.MIN_VALUE;
                       float sum=0.0f;
                       
                       for (Float v:element.getValues()){
                           if (v>upper){
                              upper=v;
                           }
                           sum+=v;
                       }
                       
                       float avg=sum/element.getValues().size();
                       
                       avg/=upper;
                       for (int i=0;i<element.getValues().size();i++){
                           square(row,col+longest+i,element.getValues().get(i)/upper,avg);
                       }
                       row+=1;
                   }
                   row+=1;
               }
       }
       
       /**
        * Dumps this image in PNG format.
        * @param writer
        */
       public void writePNG(File file) throws IOException{
              ImageIO.write(image,"png",file);
       }

}