package classification.evolution.gp.operators;

import java.io.*;
import java.util.Map;

import snps.SNPFeature;
import util.Randomizer;

public class SNPInputOperator extends LeafOperator{
    
       public static final String OPERATOR_NAME="input";
       //public static final float[] VALUE_VECTOR={SNPFeature.A_VALUE,SNPFeature.B_VALUE,SNPFeature.H_VALUE};
       
       private String feature;
       private float value;
    
       /**
        * Bizarre operator that threats a string either as a model file line or as a feature name. Occasionally I will
        * think of a better solution for this. 
        * @param line
        */
       public SNPInputOperator(String line){
              String[] cols=line.trim().split(" ");
              
              if (cols.length>=3){
                 this.feature=cols[1];
                 this.value=Float.valueOf(cols[2]);
                 return;
              }
              this.feature=line;
              
              boolean a1=Randomizer.getInstance().logic();
              boolean a2=Randomizer.getInstance().logic();
              
              if (a1&&a2){
                 value=SNPFeature.AA_VALUE;
                 return;
              }
              if (a1!=a2){
                 value=SNPFeature.AB_VALUE;
                 return;
              }
              value=SNPFeature.BB_VALUE;
       }
       
       public float compute(Map<String,Float> feature2value){
              //if ((feature2value.get(feature)==SNPFeature.AA_VALUE)||(feature2value.get(feature)==SNPFeature.BB_VALUE)){
              if (feature2value.get(feature)==value){
                 return LogicOperator.TRUE;
              }
              return LogicOperator.FALSE;
       }
       
       public void write(Writer writer) throws IOException{
              writer.write(OPERATOR_NAME+" "+feature+" "+value);
       }
}