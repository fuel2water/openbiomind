package classification.evolution.gp.operators;

/**
 * Aggressively non-Oject-ish static class for indentifying and supplying the operators in lines from saved models.
 * @author emac
 *
 */
public class OperatorInterpreter{

	   /**
        * Returns the first word in a line. 
        * @param value
        * @return
	    */
       private static String firstWord(String value){
               //System.out.println("Value: "+value);
		       for (String col:value.split(" ")){
		    	   //System.out.println("col: "+col);
		    	   if (!col.equals("")){
		    		  return col;
		    	   }
		       }
		       return "";
	   }
	
       /**
        * Exhaustively tests a line until finding the operator contained on it.
        * @param operatorLine
        * @return
        */
       public static Operator makeOperatorFor(String operatorLine){
              if (operatorLine.trim().equals(AndOperator.OPERATOR_NAME)){
            	 return AndOperator.getInstance();
              }
              if (operatorLine.trim().equals(OrOperator.OPERATOR_NAME)){
            	 return OrOperator.getInstance();
              }
              if (operatorLine.trim().equals(NotOperator.OPERATOR_NAME)){
            	 return NotOperator.getInstance();
              }
              if (operatorLine.trim().equals(SumOperator.OPERATOR_NAME)){
                 return SumOperator.getInstance();
              }
              if (operatorLine.trim().equals(SubOperator.OPERATOR_NAME)){
                 return SubOperator.getInstance();
              }
              if (operatorLine.trim().equals(MulOperator.OPERATOR_NAME)){
                 return MulOperator.getInstance();
              }
              if (operatorLine.trim().equals(DivOperator.OPERATOR_NAME)){
                 return DivOperator.getInstance();
              }
              if (firstWord(operatorLine).equals(DirectInputOperator.OPERATOR_NAME)){
                 return new DirectInputOperator(operatorLine);
              }
              if (firstWord(operatorLine).equals(ConstantOperator.OPERATOR_NAME)){
                 return new ConstantOperator(operatorLine);
              }
              if (operatorLine.contains(ThresholdInputOperator.OPERATOR_NAME)){
            	 return new ThresholdInputOperator(operatorLine);
              }
              System.err.println("No operator found for "+operatorLine);
              return null;
       }
}