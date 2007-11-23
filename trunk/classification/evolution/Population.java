package classification.evolution;

import java.io.*;
import java.util.*;

import dataset.Dataset;
import util.Randomizer;

public class Population{

      private ArrayList<Evolvable> data=new ArrayList<Evolvable>();
      private int tournamentSize;

      public Population(){
      }

      public Population(EvolvableFactory factory,int populationSize,int tournamentSize){
      
             //OutputStreamWriter writer=new OutputStreamWriter(System.out);
      
             this.tournamentSize=0;
             while (data.size()<populationSize){
             
                   Evolvable spontaneous=factory.makeEvolvable();
                   
                   /*try {
                       writer.write("Evolvable #"+data.size()+"\n");
                       spontaneous.write(writer);
                   }
                   catch (IOException e){
                         System.err.println("Trying to print evolvable.");
                   }*/
                   data.add(spontaneous);
             }
      }

      public Evolvable get(int index){
             return data.get(index);
      }

      public float averageFitness(){
      
             float sum=0.0f;
             
             for (Evolvable e:data){
                 sum+=e.getFitness();
             }
             return sum/data.size();
      }

      public void write(Writer writer) throws IOException{
             for (int i=0;i<data.size();i++){
                 writer.write("Model #"+i+"\n");
                 data.get(i).write(writer);
             }
      }

      public void display(){
             try {
                 write(new OutputStreamWriter(System.out));
             }
             catch (IOException e){
                   e.printStackTrace();
             }
      }

      public void add(Evolvable offspring){
             data.add(offspring);
      }

      public int size(){
             return data.size();
      }

      public Evolvable select(){
      
             Evolvable output=(Evolvable)Randomizer.getInstance().fromList(data);
             
             for (int i=0;i<tournamentSize-1;i++){
             
                 Evolvable current=(Evolvable)Randomizer.getInstance().fromList(data);
                   
                 if (output==null){
                    output=current;
                    continue;
                 }
                 if (current.getFitness()>output.getFitness()){
                    output=current;
                    continue;
                 }
                 if ((current.size()<output.size())&&(current.getFitness()==output.getFitness())){
                    output=current;
                 }
             }
             return output;
      }

      public void computeFitness(Dataset train,FitnessEvaluator fitnessEvaluator){
             for (Evolvable e:data){
                 e.computeFitness(train,fitnessEvaluator);
             }
      }

      public boolean contains(Evolvable doppelganger){
             for (Evolvable e:data){
                 if (e.equals(doppelganger)){
                    return true;
                 }
             }
             return false;
      }

      public Population getBest(){
      
             Population output=new Population();
             float bestFitness=data.get(0).getFitness();
             
             for (Evolvable e:data){
                 if (e.getFitness()>bestFitness){
                    bestFitness=e.getFitness();
                 }
             }
             
             int minSize=-1;
             
             for (Evolvable e:data){
                 if (e.getFitness()==bestFitness){
                    if (minSize<0){
                       minSize=e.size();
                       continue;
                    }
                    if (e.size()<minSize){
                       minSize=e.size();
                    }
                 }
             }
             for (Evolvable e:data){
                 if ((e.getFitness()==bestFitness)&&(e.size()==minSize)){
                    if (output.contains(e)){
                       continue;
                    }
                    output.add(e);
                 }
             }
             return output;
      }
      
}