package classification.evolution.gp.conventional;

import java.util.List;

import classification.evolution.gp.operators.*;

class ConventionalOperatorFactory extends OperatorFactory{

      public ConventionalOperatorFactory(List<String> features){
             System.out.println(features.size()+" features");
             for (String f:features){
                 leaves.add(new DirectInputOperator(f));
                 leaves.add(new ConstantOperator());
             }
             internals.add(SumOperator.getInstance());
             internals.add(SubOperator.getInstance());
             internals.add(DivOperator.getInstance());
             internals.add(MulOperator.getInstance());
      }
      
}