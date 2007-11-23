package classification.evolution;

public interface EvolvableFactory{

       public Evolvable makeEvolvable();

       public Evolvable makeEvolvable(Evolvable mom,Evolvable dad);
       
}