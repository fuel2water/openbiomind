package classification.evolution.gp.operators;

import java.util.*;

import util.Randomizer;

public class OperatorFactory{
	
	   protected List<LeafOperator> leaves=new ArrayList<LeafOperator>();
	   protected List<InternalOperator> internals=new ArrayList<InternalOperator>();
	
	   public LeafOperator makeLeaf(){
		      return (LeafOperator)Randomizer.getInstance().fromList(leaves);
	   }
	   
	   public InternalOperator makeInternal(){
		      return (InternalOperator)Randomizer.getInstance().fromList(internals);
	   }
	   
	   public Operator makeOperator(){
		      if (Randomizer.getInstance().logic()){
		    	 return makeLeaf();
		      }
		      return makeInternal();
	   }
}