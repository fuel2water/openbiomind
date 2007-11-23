package classification.evolution.snps;

import dataset.*;
import classification.evolution.*;
import classification.evolution.gp.conventional.*;

public class SNPGAEvolutionParametersFactory implements EvolutionParametersFactory{

       private static SNPGAEvolutionParametersFactory instance=new SNPGAEvolutionParametersFactory();
       
       private Dataset dataset;        
      
       private SNPGAEvolutionParametersFactory(){
       }

       public void setDataset(Dataset dataset){
              this.dataset=dataset;
       }

       public static SNPGAEvolutionParametersFactory getInstance(){
              return instance;
       }

       public EvolutionParameters makeParameters(){
              return new EvolutionParameters(new PatternStrengthClassifierFactory(dataset.getFeatures()),AccuracyFitnessEvaluator.getInstance());
              //return new EvolutionParameters(new LinearSNPClassifierFactory(dataset),AccuracyFitnessEvaluator.getInstance());
       }

}