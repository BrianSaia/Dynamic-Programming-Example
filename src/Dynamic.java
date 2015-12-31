/**
 * @author - Brian Saia
 * @date - 12/31/15
 * @time - 2:23 PM
 * @license -
 * @discription -
 */

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Dynamic{
   private static long previousValues[];
   private static ArrayList history;
   private static int k;
   private static String fileName;
   private static final short MAX_N = 92;


   public static void main(String[] args) {
      // debug mode is to determine the largest 'n' so that fib(n) does not overflow the data-type used
      final boolean DEBUG = false;

      if(DEBUG){
         long oldValue = 0;
         for (int i = 0; true; i++) {
            long newValue = fibunacciIterative((short) i);
            System.out.println(i + " " + newValue);
            if(oldValue <= newValue)
               oldValue = newValue;
            else{
               System.out.println("n cannot exceed: " + (i-1));
               break;
            }
         }
      }else {
         if(args.length < 2){
            System.out.println("usage: java FibonacciAlgorithmPerformanceEvaluation k fileName.csv\n" +
                  "the time performance of each algorithm is based on the average of k samples\n" +
                  "and 'fileName.csv' is where the results are stored");
            return;
         }
         k = Integer.valueOf(args[0]);
         fileName = args[1];
         if(k < 1){
            System.out.println("k can NOT be less than 1");
            return;
         }
         System.out.println("n\tIterative (ns)\tDynamic (ns)\tRecursive (ns)");
         history = new ArrayList<Float[]>(MAX_N);

         long startTime;
         long totalTime[];
         float averageTime[];
         /*
         saving the results to a file in the shutdown hook will allow the user to exit out of the program at anytime
         using ^C and still maintain functionality
         */
         Runtime.getRuntime().addShutdownHook(new SaveFileThread());
         for (short n = 0; n <= MAX_N; n++) {
            //perform the calculation k times to average out abnormalities
            totalTime = new long[]{0, 0, 0};
            for (int i = 0; i < k; i++) {
               startTime = System.nanoTime();
               fibunacciIterative(n);
               totalTime[0] += System.nanoTime() - startTime;

               previousValues = new long[n];
               startTime = System.nanoTime();
               fibunacciDynamic(n, previousValues);
               totalTime[1] += System.nanoTime() - startTime;

               startTime = System.nanoTime();
               fibunacciRecursive(n);
               totalTime[2] += System.nanoTime() - startTime;
            }
            averageTime = new float[]{totalTime[0]/(float) k,totalTime[1]/(float) k,totalTime[2]/(float) k};
            history.add(averageTime);
            System.out.printf("%d\t%-10.2f\t%-10.2f\t%-10.2f\n",n,averageTime[0],averageTime[1],averageTime[2]);
         }
      }
   }
   private static class SaveFileThread extends Thread {
      @Override
      public void run() {
         try {
            //append the csv file extension if the user did not add it
            if(fileName.length() < 4 || !fileName.substring(fileName.length()-4,fileName.length()-1).equalsIgnoreCase(".csv"))
               fileName += ".csv";
            FileWriter file = new FileWriter(fileName, true);
            file.write("k = " + k + "\n");
            file.write("n,Iterative (ns),Dynamic (ns),Recursive (ns)\n");
            for(short n = 0; n < history.size(); n++){
               file.write( n + "," +
                     ((float[]) history.get(n))[0] + "," +
                     ((float[]) history.get(n))[1] + "," +
                     ((float[]) history.get(n))[2] + "\n");
            }
            file.close();
         } catch (IOException e) {
            System.out.println("IO-Exception!!! file name: \"" + fileName + "\" cannot be created, open or is a directory/");
         }
      }
   }

   private static long fibunacciIterative(short n){
      if (n == 0) {
         return 0;
      }
      if (n < 3) {
         return 1;
      }
      long nMinusOne = 1;
      long nMinusTwo = 1;
      long temp = 0;
      for (int i = 3; i <= n; i++) {
         temp = nMinusOne + nMinusTwo;
         nMinusTwo = nMinusOne;
         nMinusOne = temp;
      }
      return temp;
   }

   private static long fibunacciRecursive(short n) {
      if (n == 0) {
         return 0;
      }
      if (n < 3) {
         return 1;
      }
      return fibunacciRecursive((short) (n - 1)) + fibunacciRecursive((short) (n - 2));
   }

   private static long fibunacciDynamic(short n, long previousValues[]) {
      if (n == 0) {
         return 0;
      }
      if (n < 3) {
         return 1;
      }
      if(previousValues[n-1] > 0)
         return previousValues[n-1];
      previousValues[n-1] = fibunacciDynamic((short) (n - 1),previousValues) + fibunacciDynamic((short) (n - 2),previousValues);
      return previousValues[n-1];
   }
}
