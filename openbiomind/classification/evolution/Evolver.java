package classification.evolution;

import classification.*;
import dataset.Dataset;

public class Evolver implements Trainer{

      private EvolutionParameters evolutionParameters;

      public Evolver(EvolutionParameters evolutionParameters){
             this.evolutionParameters=evolutionParameters;
      }

      public Ensemble train(Dataset train){
      
             Population population=new Population(evolutionParameters.getEvolvableFactory(),evolutionParameters.getPopulationSize(),evolutionParameters.getTournamentSize());
             float bestFitness=0.0f;
             
             for (int i=0;i<evolutionParameters.getMaxGenerations();i++){
                 population.computeFitness(train,evolutionParameters.getFitnessEvaluator());
                 
                 Population bestSet=population.getBest();
                 
                 bestFitness=bestSet.averageFitness();
                 System.out.printf("Generation %d, best fitness: %f\n",i,bestFitness);
                 bestSet.display();

                 Population newpop=new Population();
                 
                 newpop.add(bestSet.select());
                 while (newpop.size()<population.size()){
                       newpop.add(evolutionParameters.getEvolvableFactory().makeEvolvable(population.select(),population.select()));
                 }
                 population=newpop;
             }
             population.computeFitness(train,evolutionParameters.getFitnessEvaluator());

             Ensemble output=new Ensemble();
             Population bestset=population.getBest();

             for (int i=0;i<bestset.size();i++){
                 output.add(bestset.get(i));
             }
             return output;
      }

}