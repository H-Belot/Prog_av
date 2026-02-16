import java.io.*;
import java.util.*;

public class ScalabiliteForte {

    public static void main(String[] args) {

        String inputFile = "resultats_forte_distribue.csv";
        String outputFile = "resultats_forte_speedup.csv";

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

        Map<Integer, Long> medians = new TreeMap<>();

        for (Map.Entry<Integer, List<Long>> entry : timesByWorkers.entrySet()) {

            List<Long> times = entry.getValue();
            Collections.sort(times);

            int n = times.size();
            long median = (n % 2 == 1)
                    ? times.get(n / 2)
                    : (times.get(n / 2 - 1) + times.get(n / 2)) / 2;

            medians.put(entry.getKey(), median);
        }

        Long t1 = medians.get(1);

        if (t1 == null) {
            System.err.println("Erreur : pas de workers=1");
            return;
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(outputFile))) {

            pw.println("WORKERS,MEDIAN_DURATION_NS,SPEEDUP");

            for (Map.Entry<Integer, Long> entry : medians.entrySet()) {

                int workers = entry.getKey();
                long median = entry.getValue();

                double speedup = (double) t1 / median;

                pw.printf(Locale.US,
                        "%d,%d,%.6f%n",
                        workers, median, speedup);

                System.out.println("Workers: " + workers
                        + " Median: " + median
                        + " Speedup: " + speedup);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Fichier généré : " + outputFile);
    }
}
