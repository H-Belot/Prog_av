import java.io.*;
import java.util.*;

public class scalabilité {

    public static void main(String[] args) {

        String inputFile = "resultats.csv";
        String outputFile = "resultats_speedup.csv";

        // workers -> liste des temps
        Map<Integer, List<Long>> timesByWorkers = new TreeMap<>();

        /* ================= LECTURE CSV ================= */
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {

            String line = br.readLine(); // header

            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty()) continue;

                String[] tokens = line.split(",");
                if (tokens.length < 5) continue;

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

        /* ================= MÉDIANES ================= */
        Map<Integer, Long> medians = new TreeMap<>();

        for (Map.Entry<Integer, List<Long>> entry : timesByWorkers.entrySet()) {

            List<Long> times = entry.getValue();
            Collections.sort(times);

            int n = times.size();
            long median;

            if (n % 2 == 1) {
                median = times.get(n / 2);
            } else {
                median = (times.get(n / 2 - 1) + times.get(n / 2)) / 2;
            }

            medians.put(entry.getKey(), median);
        }

        /* ================= SPEEDUP ================= */
        Long t1 = medians.get(1);

        if (t1 == null) {
            System.err.println("ERREUR : workers=1 absent, speedup impossible");
            return;
        }

        System.out.printf("%-10s %-20s %-10s%n",
                "WORKERS", "MEDIAN_TIME_NS", "SPEEDUP");

        /* ================= ÉCRITURE CSV ================= */
        try (PrintWriter pw = new PrintWriter(new FileWriter(outputFile))) {

            pw.println("WORKERS,MEDIAN_DURATION_NS,SPEEDUP");

            for (Map.Entry<Integer, Long> e : medians.entrySet()) {

                int workers = e.getKey();
                long medianTime = e.getValue();
                double speedup = (double) t1 / medianTime;

                System.out.printf(Locale.US,
                        "%-10d %-20d %-10.3f%n",
                        workers, medianTime, speedup);

                pw.printf(Locale.US,
                        "%d,%d,%.6f%n",
                        workers, medianTime, speedup);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\nCSV généré : " + outputFile);
    }
}
