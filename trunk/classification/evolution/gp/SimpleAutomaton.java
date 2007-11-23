package classification.evolution.gp;

import java.io.*;
import java.util.*;

import classification.Classifier;
import classification.evolution.Evolvable;
import classification.evolution.gp.operators.*;
import util.*;

/**
 * A very simple (and often unelegant) automaton implementation.
 * @author emac
 *
 */
public class SimpleAutomaton extends Evolvable{

	   /**
	    * Internal automaton node class.
	    * @author emac
	    *
	    */
	   class Node{
		   
		     private Operator operator;
		     private int left;
		     private int right;
		   
             public Node(LeafOperator operator){
            	    this.operator=operator;
             }
		     
             public Node(Operator operator,int left,int right){
            	    this.operator=operator;
            	    this.left=left;
            	    this.right=right;
             }
		     
             public int getLeft(){
            	    return left;
             }

             public int getRight(){
            	    return right;
             }
             
		     /**
              * Performs the corresponding computation for this node.
              * @param feature2value
              * @param nodes
              * @return
              */
		     public float evaluate(Map<String,Float> feature2value,List<Node> nodes){
            	    if (operator.isLeaf()){
            	       return operator.compute(feature2value);
            	    }
            	    if (operator.isUnary()){
            	       return operator.compute(nodes.get(left).evaluate(feature2value,nodes),0.0f);
            	    }
            	    return operator.compute(nodes.get(left).evaluate(feature2value,nodes),nodes.get(right).evaluate(feature2value,nodes));
             }
		     
		     /**
		      * Writes a correctly idented textual represenation of this node annd then goes recursively to the next ones.
		      * @param writer
		      * @param nodes
		      * @param index
		      * @param level
		      */
		     void write(Writer writer,List<Node> nodes,int level) throws IOException{
		    	  for (int i=0;i<level;i++){
		    		  writer.write(" ");
		    	  }
		    	  operator.write(writer);
		    	  writer.write("\n");
		    	  if (operator.isLeaf()){
		    		 return;
		    	  }
		    	  level+=1;
		    	  nodes.get(left).write(writer,nodes,level);
		    	  if (operator.isUnary()){
		    		 return;
		    	  }
		    	  nodes.get(right).write(writer,nodes,level);
		     }
		     
		     public Operator getOperator(){
		    	    return operator;
		     }
		     
	   }
	
	   private List<Node> nodes=new ArrayList<Node>();

	   /**
	    * Computes the level of the operator in this textual representation.
	    * @param operatorline
	    * @return
	    */
	   private static int levelOf(String operatorLine){
		   
		       int output=0;
		       
		       for (Character c:operatorLine.toCharArray()){
		    	   if (c!=' '){
		    		  return output;
		    	   }
		    	   output+=1;
		       }
		       return output;
	   }
	   
	   /**
	    * (Re)contructs an automaton from a textual description.
	    * @param lines
	    */
	   public SimpleAutomaton(LineTagger lines){
		      //System.out.println(lines.toString());
		      for (int i=1;i<lines.size();i++){
		    	  //System.out.println(lines.get(i));
		    	  
		    	  Operator operator=OperatorInterpreter.makeOperatorFor(lines.get(i));
		    	  
		    	  if (operator.isLeaf()){
		    		 //System.out.println("leaf");
		    		 nodes.add(new Node((LeafOperator)operator));
		    		 continue;
		    	  }
		    	  
		    	  int parentLevel=levelOf(lines.get(i));
		    	  List<Integer> children=new ArrayList<Integer>();
		    	  
		    	  for (int j=i+1;(j<lines.size())&&(levelOf(lines.get(j))>parentLevel);j++){
		    		  if (levelOf(lines.get(j))==parentLevel+1){
		    		     children.add(j-1);
		    		  }
		    	  }
		    	  if (children.size()==1){
		    		 nodes.add(new Node(operator,children.get(0),0));
		    	  }
		    	  else {
		    		   nodes.add(new Node(operator,children.get(0),children.get(1)));
		    	  }
		      }
	   }
	   
	   /**
	    * Constructs automaton by assexual reproduction.
	    * @param operatorFactory
	    * @param length
	    */
	   public SimpleAutomaton(OperatorFactory operatorFactory,int length){
		      while (nodes.size()<length){
		    	  
		    	    int left=nodes.size()*2+1;
		    	    int right=nodes.size()*2+2;
		    	    
		    	    if ((left>=length)||(right>=length)){
		    	       nodes.add(new Node(operatorFactory.makeLeaf()));
		    	       continue;
		    	    }
		    	    nodes.add(new Node(operatorFactory.makeOperator(),left,right));
		      }
	   }
	   
       /**
        * Constructs automaton by sexually reproduction.
        * @param dad
        * @param mom
        * @param operatorFactory
        * @param mutationRate
        */
	   public SimpleAutomaton(Evolvable dad,Evolvable mom,OperatorFactory operatorFactory,float mutationRate){
    	   
    	      List<Node> momNodes=((SimpleAutomaton)mom).getNodes();
    	      List<Node> dadNodes=((SimpleAutomaton)mom).getNodes();
    	      int length=Math.min(momNodes.size(),dadNodes.size());
    	      int crossPoint=Randomizer.getInstance().natural(length);

              for (int i=0;i<length;i++){
            	  if (i<crossPoint){
            		 nodes.add(momNodes.get(i));
            	  }
            	  else {
            		   nodes.add(dadNodes.get(i));
            	  }
            	  if (Randomizer.getInstance().real()<mutationRate){
            		 if (nodes.get(i).getOperator().isLeaf()){
            			nodes.set(i,new Node(operatorFactory.makeLeaf()));
            		 }
            		 else {
            			  nodes.set(i,new Node(operatorFactory.makeOperator(),nodes.get(i).getLeft(),nodes.get(i).getRight()));
            		 }
            	  }
              }
       }
	   
       public List<Node> getNodes(){
    	      return nodes;
       }
       
       /**
	    * Evaluates a given feature vector.
	    */
	   public boolean evaluate(Map<String,Float> feature2value){
		      return nodes.get(0).evaluate(feature2value,nodes)>0.0f;
	   }
	
       /**
        * Writes a textual representation of this automaton in the given writer.
        */
	   public void write(Writer target) throws IOException{
              nodes.get(0).write(target,nodes,0);
       }

       /**
        * Check equality of this classifier with other by using a horrifying (but simple) comparison of textual 
        * descriptions.
        */
	   public boolean equals(Classifier other){
               try {
                   StringWriter otherWriter=new StringWriter();
                   StringWriter thisWriter=new StringWriter();
              
                   other.write(otherWriter);
                   this.write(thisWriter);
                   return otherWriter.toString().equals(thisWriter.toString());
               }
               catch (IOException e){
               }
               return false;
       }
       
       /**
        * Returns the feature set of this automaton by scanning its textual representation. Again, a horrible but simple 
        * implementation.
        */
	   public Set<String> featureSet(){
    	   
    	      Set<String> output=new HashSet<String>();
              
              try {
                  StringWriter thisWriter=new StringWriter();
              
                  this.write(thisWriter);
                  for (String line:thisWriter.toString().split("\n")){
                	  if (line.contains("input")){
                         //System.out.println(line);
                		 output.add(line.trim().split(" ")[1]);
                	  }
                  }
              }
              catch (IOException e){
              }
    	      return output;
       }

       /**
        * Measures the size of this automaton from its textual representation. Again, a horrible but simple 
        * implementation.
        */
	   public int size(){
              try {
                  StringWriter thisWriter=new StringWriter();
              
                  this.write(thisWriter);
                  return thisWriter.toString().split("\n").length;
              }
              catch (IOException e){
              }
    	      return 0;
       }

}