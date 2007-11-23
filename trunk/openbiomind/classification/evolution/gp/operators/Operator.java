package classification.evolution.gp.operators;

import java.io.*;
import java.util.Map;

public interface Operator{
	
	   public boolean isLeaf();
	   
	   public boolean isUnary();

	   public void write(Writer write) throws IOException;
	   
	   public float compute(float a,float b);
	   
	   public float compute(Map<String,Float> feature2value);

}