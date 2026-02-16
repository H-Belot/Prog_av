import java.io.*;
import java.util.*;

public class ScalabiliteFaible {

    public static void main(String[] args) {

        String inputFile = "resultats_faible_distribue.csv";
        String outputFile = "resultats_faible_median.csv";

        Map<Integer, List<Long>> timesByWorkers = new TreeMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {

            String line = br.readLine(); // skip header

            while ((line = br.readLine()) != null) {

                String[] tokens = line.split(",");
                int workers = Integer.parseInt(tokens[3]);
                long duration = Long.parseLong(tokens[4]);

                timesByWorkers
                        .computeIfAbsent(workers, k -> new ArrayList<>())
                        .add(duration);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(outputFile))) {

            pw.println("WORKERS,MEDIAN_DURATION_NS");

            for (Map.Entry<Integer, List<Long>> entry : timesByWorkers.entrySet()) {

                List<Long> times = entry.getValue();
                Collections.sort(times);

                int n = times.size();
                long median = (n % 2 == 1)
                        ? times.get(n / 2)
                        : (times.get(n / 2 - 1) + times.get(n / 2)) / 2;

                pw.println(entry.getKey() + "," + median);

                System.out.println("Workers: " + entry.getKey()
                        + " Median: " + median);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Fichier généré : " + outputFile);
    }
}
