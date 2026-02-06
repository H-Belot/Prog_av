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

        int Ntot = 12_000_000;   // PROBLÈME FIXE
        int numWorkers = 1;      // p = 1,2,4,6,8,...
        int nb_ex = 5;           // répétitions

        Master master = new Master();

        for (int i = 0; i < nb_ex; i++) {

            int pointsParWorker = Ntot / numWorkers;

            long start = System.nanoTime();
            long total;

            try {
                total = master.doRun(pointsParWorker, numWorkers);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            long stop = System.nanoTime();
            long duration = stop - start;

            double pi = 4.0 * total / Ntot;
            double error = Math.abs(pi - Math.PI) / Math.PI;

            writeCsv("resultats.csv", pi, error, Ntot, numWorkers, duration);
        }
    }
}
