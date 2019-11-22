package ea;

import java.io.IOException;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExportData implements Runnable
{
    public ExportData() {  }

    public static void main(String[] args) {
        ExportData ed = new ExportData();
        ed.run();
    }

    public void run()
    {
        ArrayList<String> results = new ArrayList<>();

        // read file with best iteration
        /*
        String filename = "results.csv";
        try{
            File file = new File(filename);
            Scanner inputStream = new Scanner(file);
            while (inputStream.hasNext()) {
                String data = inputStream.next();
                System.out.println(data);
            }
            inputStream.close();
        }
        catch (Exception e){ e.printStackTrace(); }
        */

        // run EA 10 times
        for (int i = 0; i <= 0; i++)
        {
            EA ea = new EA();
            Individual individual = new Individual();
            System.out.println("");
            ea.runAlgorithm();
            individual = ea.bestI();
            String result = individual.getResult();
            results.add(result);

            System.out.println("End of loop "+i+"/10");
        }

        // after the 10 times, print the smallest time
        System.out.println("------RESULTS-------");
        float best = 250;
        String bestRun = "";
        for (String r : results){
            System.out.println(r);
            String[] all = r.split(",");
            String bestTime = all[45];
            try{
                Float f = Float.valueOf(bestTime);
                if (f < best){
                    best = f;
                    bestRun = r;
                }
            }
            catch (Exception e) { e.printStackTrace(); }
        }
        System.out.println("\n\nBest Individual of 10 runs: "+bestRun);

        // write bestRun to csv file
        FileWriter csvWriter = null;
        try {
            csvWriter = new FileWriter("results.csv");
            csvWriter.append(bestRun);
        }
        catch (IOException e) {  e.printStackTrace(); }

    }
}
