package ea;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
        ArrayList<Integer> pacings = new ArrayList<>();

        int start = 300;
        int bound = 50;

        int times = 27;
        for (int i = 0; i <= times; i++) // run EA 10 times
        {
            EA ea = new EA();
            Individual individual = new Individual();
            System.out.println("");
            ea.runAlgorithm(start, bound);
            individual = ea.bestI();
            String result = individual.getResult();
            results.add(result);
            System.out.println("\ni: "+i+" start: "+start+"\tbound: "+bound);
            String hola = ""+start+bound;
            System.out.println(hola);
            pacings.add(Integer.parseInt(hola));

            // next number
            bound += 50;
            if ((start - bound) <200) {
                start += 50;
                bound = 50;
            }

            System.out.println("End of loop "+i+"/"+times);
        }

        // after the 10 times, print the smallest time
        System.out.println("------RESULTS-------");
        float best = 250;
        String bestRun = "";

        System.out.println("results.size(): "+results.size());
        //for (String r : results)
        int a = 0;
        for (int i = 0; i < results.size(); i++)
        {

            // export pacing strategies to file
            String[] all = results.get(i).split(",");
            float raceTime = Float.valueOf(all[0]);
            System.out.println("- "+raceTime);
            System.out.println(pacings.get(i));
            try(FileWriter fw = new FileWriter("results/pacings3.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                out.println(pacings.get(i) + "," + raceTime);
            }
            catch (IOException e) { e.printStackTrace(); }

            // set best race time
            String bestTime = all[0];
            try{
                Float f = Float.valueOf(bestTime);
                if (f < best){
                    best = f;
                    bestRun = results.get(i);
                }
            }
            catch (Exception e) { e.printStackTrace(); }
        }
        System.out.println("\n\nBest Individual of 10 runs: "+bestRun);

        // export all results to file
        try(FileWriter fw = new FileWriter("results/results.txt", true);
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
