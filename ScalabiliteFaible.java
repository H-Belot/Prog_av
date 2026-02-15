import java.io.*;
import java.util.*;

public class ScalabiliteFaible {

    public static void main(String[] args) {

        String inputFile = "resultats_faible.csv";
        String outputFile = "resultats_faible_efficiency.csv";

        Map<Integer, List<Long>> timesByWorkers = new TreeMap<>();

        /* ===== LECTURE CSV ===== */
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

        /* ===== MÉDIANES ===== */
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

        /* ===== CALCUL EFFICACITÉ FAIBLE ===== */
        Long t1 = medians.get(1);

        if (t1 == null) {
            System.err.println("ERREUR : workers=1 absent");
            return;
        }

        System.out.printf("%-10s %-20s %-10s%n",
                "WORKERS", "MEDIAN_TIME_NS", "E_WEAK");

        /* ===== ÉCRITURE CSV ===== */
        try (PrintWriter pw = new PrintWriter(new FileWriter(outputFile))) {

            pw.println("WORKERS,MEDIAN_DURATION_NS,E_WEAK");

            for (Map.Entry<Integer, Long> e : medians.entrySet()) {

                int workers = e.getKey();
                long medianTime = e.getValue();

                double efficiency = (double) t1 / medianTime;

                System.out.printf(Locale.US,
                        "%-10d %-20d %-10.4f%n",
                        workers, medianTime, efficiency);

                pw.printf(Locale.US,
                        "%d,%d,%.6f%n",
                        workers, medianTime, efficiency);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\nCSV généré : " + outputFile);
    }
}
