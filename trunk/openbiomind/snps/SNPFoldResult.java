package snps;

import java.io.*;

import classification.ConfusionMatrix;

class SNPFoldResult{
    
      private SNPClassifier classifier;
      private ConfusionMatrix matrix;
    
      public SNPFoldResult(SNPClassifier classifier,ConfusionMatrix matrix){
             this.classifier=classifier;
             this.matrix=matrix;
      }
 
      public void write(Writer writer) throws IOException{
             writer.write("Model: \n");
             this.classifier.write(writer);
             writer.write("Confusion matrix: \n");
             this.matrix.write(writer);
      }
}