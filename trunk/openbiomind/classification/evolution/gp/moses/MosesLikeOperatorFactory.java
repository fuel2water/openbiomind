package classification.evolution.gp.moses;

import java.util.*;

import classification.evolution.gp.operators.*;

class MosesLikeOperatorFactory extends OperatorFactory{

      public MosesLikeOperatorFactory(Map<String,Float> f2m){
             System.out.println(f2m.size()+" features");
             for (String f:f2m.keySet()){
                 leaves.add(new ThresholdInputOperator(f,f2m.get(f)));
             }
             internals.add(AndOperator.getInstance());
             internals.add(OrOperator.getInstance());
             internals.add(NotOperator.getInstance());
      }
      
}