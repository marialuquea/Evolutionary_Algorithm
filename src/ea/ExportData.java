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
        
        // DOING AVERAGE OF ALL RESULTS
        try {
            File file = new File("C:\\Users\\Maria\\Desktop\\ecooooo\\results\\testing\\hillclimber.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            double total = 0.0;
            double count = 0.0;
            String st;
            while ((st = br.readLine()) != null){
                double d = Double.parseDouble(st);
                total += d;
                count++;
            }
            System.out.println("total: "+total);
            System.out.println("average: "+(total / count));
        } catch (Exception e) {e.printStackTrace();}


        ArrayList<String> results = new ArrayList<>();
        // ArrayList<Integer> pacings = new ArrayList<>();

        int start = 500;
        int bound = 200;
        String [] selection = Parameters.selection;     // 3
        String [] crossover = Parameters.crossover;     // 3
        String [] mutation = Parameters.mutation;       // 2
        String [] diversity = Parameters.diversity;     // 2

        int idx = 0;
        int times = 4;
        for (int i = 0; i <= times; i++) // run EA 30 times to get average
        {
            /*
            if ((i % 10 == 0) && (i != 0)) {
                idx++;
            }
            */

            System.out.println("--i: "+i+"\n");
            EA ea = new EA();
            Individual individual = new Individual();
            ea.runAlgorithm(start, bound, selection[0], crossover[1], mutation[1], diversity[1]);
            System.out.println("------SELECTION: "+selection[0]);
            System.out.println("------CROSSOVER: "+crossover[1]);
            System.out.println("------MUTATION: "+mutation[0]);
            System.out.println("------DIVERSITY: "+diversity[0]);
            individual = ea.getBestIndividual();
            String result = individual.getResult();
            results.add(result);


            /*
            ----EVALUATING INITIALISING STRATEGY----
            System.out.println("\ni: "+i+" start: "+start+"\tbound: "+bound);
            String hola = ""+start+bound;
            System.out.println(hola);
            pacings.add(Integer.parseInt(hola));
            // next number
            bound += 50;
            if ((start - bound) <200) {
                start += 50;
                bound = 50;
            }     */

            //System.out.println("End of loop "+i+"/"+times+1);
        }

        // after the n times, print the smallest time
        System.out.println("\n------RESULTS-------");
        float best = 250;
        String bestRun = "";
        for (int i = 0; i < results.size(); i++)
        {
            String[] all = results.get(i).split(",");
            float raceTime = Float.valueOf(all[0]);
            System.out.println("- "+raceTime);
            /*
            System.out.println(pacings.get(i));

            try(FileWriter fw = new FileWriter("results/pacings3.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                out.println(pacings.get(i) + "," + raceTime);
            }
            catch (IOException e) { e.printStackTrace(); }
             */

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
        System.out.println("\n\nBest Individual: "+bestRun);

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
            // out.println("averageScore: "+(averageScore / (times+1)));
            out.println("");
        }
        catch (IOException e) { e.printStackTrace(); }



    }
}
