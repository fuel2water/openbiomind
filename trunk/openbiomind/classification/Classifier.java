package classification;

import java.util.*;

import util.Writeable;

public interface Classifier extends Writeable{

       public abstract boolean evaluate(Map<String,Float> f2v);

       public abstract int size();

       public boolean equals(Classifier other);
       
       public Set<String> featureSet();
}