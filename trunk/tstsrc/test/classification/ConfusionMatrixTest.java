package test.classification;

import java.util.*;
import junit.framework.*;

import classification.ConfusionMatrix;
import util.LineTagger;

public class ConfusionMatrixTest extends TestCase{

       public void testConstruction(){

              ConfusionMatrix matrix=makeBasic();
              boolean asserter=true;

              for (int i=0;i<2;i++)
                  for (int j=0;j<2;j++)
                      if (matrix.get(i,j)!=(i*2+j)){
                         asserter=false;
                         break;
                      }
              assertTrue("Incorrect construction of matrix from lines",asserter);
       }

       private ConfusionMatrix makeBasic(){
              List<String> lines=new ArrayList<String>();

              lines.add("Matrix:");
              lines.add("0\t1");
              lines.add("2\t3");
              return new ConfusionMatrix(new LineTagger(lines));
       }

       public void testIsDegenerate(){

              ConfusionMatrix matrix=makeBasic();

              assertTrue("Incorrect degeneracy testing",!matrix.isDegenerate());
       }

       public void testBalancedAccuracy(){

              ConfusionMatrix matrix=makeBasic();

              assertTrue("Incorrect balanced accuracy computation",matrix.balancedAccuracy()==0.3f);                            
       }


       public void testAccount(){

              ConfusionMatrix matrix=makeBasic();

              matrix.account(false,false);
              assertTrue("Incorrect accounting of expected-computed pair",matrix.get(0,0)==1);              
       }

       public void testAccuracy(){

              ConfusionMatrix matrix=makeBasic();

              assertTrue("Incorrect accuracy computation",matrix.accuracy()==0.5);
       }

}

