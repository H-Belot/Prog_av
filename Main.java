import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;

/*
 * Permet de lancer la simulation de Monte Carlo pour estimer pi,
 * mesurer le temps d'exécution et enregistrer les résultats dans un fichier CSV.
 */
public class Main {

    private static void writeCsv(String filename,
                                 double pi,
                                 double error,
                                 long nTot,
                                 int numWorkers,
                                 long duration) {

        boolean append = true;
        File file = new File(filename);

        try (PrintWriter pw = new PrintWriter(new FileWriter(file, append))) {

            if (file.length() == 0) {
                pw.println("PI,ERROR,NTOT,WORKERS,DURATION_NS");
            }

            pw.printf(java.util.Locale.US,
                    "%f,%e,%d,%d,%d%n",
                    pi, error, nTot, numWorkers, duration);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        int totalCount = 833_333; // total de points
        int numWorkers = 12;
        int nb_ex = 5;              // nombre d'expériences

        Master master = new Master(); // ✔ une seule instance suffit

        for (int i = 0; i < nb_ex; ++i) {

            long total;
            long startTime = System.nanoTime();

            try {
                total = master.doRun(totalCount / numWorkers, numWorkers);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return;
            }

            long stopTime = System.nanoTime();
            long duration = stopTime - startTime;

            long nTot = totalCount;
            double pi = 4.0 * total / nTot;
            double error = Math.abs(pi - Math.PI) / Math.PI;

            writeCsv("resultats.csv", pi, error, nTot, numWorkers, duration);
        }
    }
}
