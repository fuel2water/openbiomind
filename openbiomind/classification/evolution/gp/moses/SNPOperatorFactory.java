package classification.evolution.gp.moses;

import java.util.*;

import classification.evolution.gp.operators.*;

class SNPOperatorFactory extends OperatorFactory{

      public SNPOperatorFactory(List<String> features){
             for (String f:features){
                 leaves.add(new SNPInputOperator(f));
             }
             internals.add(AndOperator.getInstance());
             internals.add(OrOperator.getInstance());
             internals.add(NotOperator.getInstance());
      }
      
}