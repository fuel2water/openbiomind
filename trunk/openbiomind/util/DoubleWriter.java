package util;

import java.io.*;

public class DoubleWriter extends BufferedWriter{
      
       public DoubleWriter(String fileName) throws IOException{
              super(new FileWriter(fileName));
       }

       public void write(String block) throws IOException{
              System.out.print(block);
              super.write(block);
       }
            
}