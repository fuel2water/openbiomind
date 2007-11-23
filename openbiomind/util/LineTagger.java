package util;

import java.io.*;
import java.util.*;

/**
 * Parses sequences of lines strcutured in "line XML".
 * @author emac
 *
 */
public class LineTagger{
	
	   private List<String> lines=new ArrayList<String>();

	   /**
	    * Stores all lines supplied by a given text file.
	    * @param fileName
	    * @throws FileNotFoundException
	    * @throws IOException
	    */
	   public LineTagger(String fileName) throws FileNotFoundException,IOException{
		   
		      BufferedReader reader=new BufferedReader(new FileReader(fileName));
		      
              for (String current=reader.readLine();current!=null;current=reader.readLine()){
            	  lines.add(current);
              }
              reader.close();
	   }
	
	   public LineTagger fromOn(int startPoint){
		      return new LineTagger(lines.subList(startPoint,size()));
	   }
	   
	   /**
	    * Constructs line tagger for a given sequence of lines.
	    * @param lines
	    */
	   public LineTagger(List<String> lines){
		      this.lines=lines;
	   }
	   
       /**
        * Returns the number of stored lines.
        * @return
        */
	   public int size(){
    	      return lines.size();
       }
	   
       /** 
        * Special toString used for debugging purposes.
        */
	   public String toString(){
		   
    	      StringBuffer buffer=new StringBuffer();
    	      
    	      for (int i=0;i<lines.size();i++){
    	    	  buffer.append(i+": "+get(i)+"\n");
    	      }
    	      return buffer.toString();
       }
	   
	   /**
        * Fuzzy line-based parse of the line sequence.
        * @param beginTag
        * @return
        */
	   public List<LineTagger> fuzzyLineParse(String beginTag){
		   
		      List<LineTagger> output=new ArrayList<LineTagger>();
		      List<String> current=null;
		   
    	      for (String line:this.lines){
    	    	  if (line.contains(beginTag)){
    	    		 if (current!=null){
    	    			output.add(new LineTagger(current));
    	    		 }
		             current=new ArrayList<String>();
    	    	  }
    	    	  if (current!=null){
    	    		 current.add(line);
    	    	  }
    	      }
    	      if (current!=null){
    	    	 output.add(new LineTagger(current));
    	      }
    	      return output;
       }
	   
       /**
        * Fuzzy line-based parse of the line sequence, allowing end tag.
        * @param beginTag
        * @return
        */
	   public List<LineTagger> fuzzyLineParse(String beginTag,String endTag){
		   
		      List<LineTagger> output=new ArrayList<LineTagger>();
		      List<String> current=null;
		   
    	      for (String line:this.lines){
    	    	  if (line.contains(beginTag)){
    	    		 if (current!=null){
    	    			output.add(new LineTagger(current));
    	    		 }
		             current=new ArrayList<String>();
    	    	  }
    	    	  if (line.contains(endTag)){
    	    	     if (current!=null){
    	    	    	output.add(new LineTagger(current));
    	    	     }
    	    	     return output;
    	    	  }
    	    	  if (current!=null){
    	    		 current.add(line);
    	    	  }
    	      }
    	      if (current!=null){
    	    	 output.add(new LineTagger(current));
    	      }
    	      return output;
       }
	   
	   public String get(int index){
		      return lines.get(index);  
	   }
}