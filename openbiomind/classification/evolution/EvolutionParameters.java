package classification.evolution;

public class EvolutionParameters{

       private EvolvableFactory evolvableFactory;
       private int maxGenerations=50;
       private int tournamentSize=2;
       private int populationSize=100;
       private FitnessEvaluator fitnessEvaluator;

       public EvolutionParameters(EvolvableFactory evolvableFactory,FitnessEvaluator fitnessEvaluator){
              this.evolvableFactory=evolvableFactory;
              this.fitnessEvaluator=fitnessEvaluator;
       }

       public FitnessEvaluator getFitnessEvaluator(){
              return fitnessEvaluator;      
       }

       public int getPopulationSize(){
              return populationSize; 
       }

       public EvolvableFactory getEvolvableFactory(){
              return this.evolvableFactory;
       }

       public int getTournamentSize(){
              return tournamentSize;
       }

       public int getMaxGenerations(){
              return maxGenerations;
       }
      
}
