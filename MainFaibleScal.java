import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * Scalabilité faible :
 * - charge de travail PAR worker constante
 * - taille totale du problème augmente avec le nombre de workers
 */
public class MainFaibleScal {

    // Écrit les résultats de l'estimation de pi dans un fichier CSV.
    private static void writeCsv(String filename,
                                 double pi,
                                 double error,
                                 long nTot,
                                 int numWorkers,
                                 long duration) {

        
        boolean append = true;
        File file = new File(filename);
        // Si le fichier n'existe pas ou est vide, écrire l'en-tête                            
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, append))) {
        // Le mode append permet d'ajouter des lignes à la fin du fichier sans écraser les données existantes
            if (file.length() == 0) {
                pw.println("PI,ERROR,NTOT,WORKERS,DURATION_NS");
            }
            // Utiliser Locale.US pour garantir que le séparateur décimal est un point, pas une virgule
            pw.printf(java.util.Locale.US,
                    "%f,%e,%d,%d,%d%n",
                    pi, error, nTot, numWorkers, duration);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Lance la simulation de Monte Carlo pour estimer pi, mesurer le temps d'exécution et enregistrer les résultats dans un fichier CSV.
    public static void main(String[] args) {

        int pointsParWorker = 3_000_000;      // CONSTANT
        int[] workersList = {1, 2, 4, 6, 8, 12};  // p variable
        int nb_ex = 5;                        // répétitions
        // Le master est responsable de la coordination des workers et de l'agrégation des résultats
        Master master = new Master();
       // Pour chaque nombre de workers, exécuter la simulation plusieurs fois pour obtenir des mesures fiables
        for (int numWorkers : workersList) {
            // Le nombre total de points simulés est égal au nombre de points par worker multiplié par le nombre de workers
            for (int i = 0; i < nb_ex; i++) {

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

                long nTot = (long) pointsParWorker * numWorkers;
                double pi = 4.0 * total / nTot;
                double error = Math.abs(pi - Math.PI) / Math.PI;

                writeCsv("resultats_faible.csv",
                        pi, error, nTot, numWorkers, duration);
            }
        }
    }
}
