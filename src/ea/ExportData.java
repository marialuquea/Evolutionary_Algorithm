package ea;

import java.util.ArrayList;

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

        for (int i = 0; i <= 10; i++)
        {
            EA ea = new EA();
            Individual individual = new Individual();

            System.out.println("");
            ea.runAlgorithm();
            individual = ea.bestI();
            String result = individual.getResult();
            results.add(result);
        }

        System.out.println("------RESULTS-------");
        for (String r : results)
        {
            System.out.println(results);
        }


    }
}
