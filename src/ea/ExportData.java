package ea;

import java.io.*;
import java.util.ArrayList;
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

        // run EA 10 times
        int times = 10;
        for (int i = 0; i <= times; i++)
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
        for (String r : results)
        {
            System.out.println("- "+r);
            String[] all = r.split(",");
            String bestTime = all[0];
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

        // export to file
        try(FileWriter fw = new FileWriter("results.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            int averageScore = 0;
            for (String r : results){
                String[] all = r.split(",");
                averageScore += Float.valueOf(all[0]);
                out.println(r);
            }
            out.println("");
            out.println("best individual: "+ bestRun);
            out.println("averageScore: "+(averageScore / (times+1)));
            out.println("");
        }
        catch (IOException e) { e.printStackTrace(); }

    }
}
