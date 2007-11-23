package classification.evolution.gp;

import classification.evolution.*;
import classification.evolution.gp.operators.*;

public class SimpleAutomataFactory implements EvolvableFactory{

	   private OperatorFactory operatorFactory;
	   private float mutationRate;
	   private int automatonLength;
	
	   public SimpleAutomataFactory(OperatorFactory operatorFactory,float mutationRate,int automatonLength){
		      this.operatorFactory=operatorFactory;
		      this.mutationRate=mutationRate;
		      this.automatonLength=automatonLength;
	   }
	   
	   public Evolvable makeEvolvable(){
		      return new SimpleAutomaton(operatorFactory,automatonLength);
	   }
	   
	   public Evolvable makeEvolvable(Evolvable dad,Evolvable mom){
		      return new SimpleAutomaton((SimpleAutomaton)dad,(SimpleAutomaton)mom,operatorFactory,mutationRate);
	   }
}