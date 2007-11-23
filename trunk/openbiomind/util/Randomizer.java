package util;

import java.util.*;

public class Randomizer{

       private static Randomizer instance=new Randomizer();
      
       private Random random;

       private Randomizer(){
               random=new Random();
       }

       public int natural(int limit){
              return random.nextInt(limit);
       }

       public float real(){
           return random.nextFloat();
       }

       public float gaussianReal(){
              return (float)random.nextGaussian();
       }

       public boolean logic(){
              return random.nextBoolean();
       }

       public static Randomizer getInstance(){
              return instance;
       }

       public Object fromList(List source){
              return source.get(random.nextInt(source.size()));
       }
      
}