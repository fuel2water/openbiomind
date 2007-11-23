package classification.evolution.gp.moses;

import java.util.*;

import classification.evolution.gp.operators.*;

class MosesDirectOperatorFactory extends OperatorFactory{

      public MosesDirectOperatorFactory(List<String> features){
    	     for (String f:features){
                 leaves.add(new DirectInputOperator(f));
    	     }
    	     internals.add(AndOperator.getInstance());
             internals.add(OrOperator.getInstance());
             internals.add(NotOperator.getInstance());
      }
      
}