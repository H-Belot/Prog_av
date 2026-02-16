import java.io.*;
import java.net.*;
import java.util.Locale;

public class MasterSocket {

    static int maxServer = 8;
    static final int[] tab_port = {25545,25546,25547,25548,25549,25550,25551,25552};
    static String[] tab_total_workers = new String[maxServer];
    static final String ip = "127.0.0.1";
    static BufferedReader[] reader = new BufferedReader[maxServer];
    static PrintWriter[] writer = new PrintWriter[maxServer];
    static Socket[] sockets = new Socket[maxServer];

    public static void main(String[] args) throws Exception {

        int totalCount = 16000000;  // nombre d'itérations par worker
        double pi;

        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        String s;

        System.out.println("#########################################");
        System.out.println("# Computation of PI by MC method        #");
        System.out.println("#########################################");

        System.out.print("\nHow many workers (< " + maxServer + ") ? ");
        int numWorkers = Integer.parseInt(bufferRead.readLine());

        // Création des connexions
        for (int i = 0; i < numWorkers; i++) {

            System.out.println("Connecting to worker on port " + tab_port[i]);

            sockets[i] = new Socket(ip, tab_port[i]);
            reader[i] = new BufferedReader(
                    new InputStreamReader(sockets[i].getInputStream()));

            writer[i] = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(sockets[i].getOutputStream())),
                    true);
        }

        String message_repeat = "y";

        while (message_repeat.equalsIgnoreCase("y")) {

            long startTime = System.nanoTime();
            int total = 0;

            String message = String.valueOf(totalCount);

            // Envoi du travail aux workers
            for (int i = 0; i < numWorkers; i++) {
                writer[i].println(message);
            }

            // Lecture des résultats
            for (int i = 0; i < numWorkers; i++) {
                tab_total_workers[i] = reader[i].readLine();
                total += Integer.parseInt(tab_total_workers[i]);
            }

            pi = 4.0 * total / (totalCount * numWorkers);

            long stopTime = System.nanoTime();
            long duration = stopTime - startTime;

            double error = Math.abs((pi - Math.PI)) / Math.PI;
            long nTot = (long) totalCount * numWorkers;

            System.out.println("\nPi : " + pi);
            System.out.println("Error : " + error);
            System.out.println("Ntot : " + nTot);
            System.out.println("Workers : " + numWorkers);
            System.out.println("Time (ns) : " + duration + "\n");

            // Écriture CSV
            writeCsv("resultats_distribue.csv",
                    pi, error, nTot, numWorkers, duration);

            System.out.print("Repeat computation (y/N) ? ");
            message_repeat = bufferRead.readLine();
        }

        // Fermeture propre
        for (int i = 0; i < numWorkers; i++) {
            writer[i].println("END");
            reader[i].close();
            writer[i].close();
            sockets[i].close();
        }

        System.out.println("Master finished.");
    }

    private static void writeCsv(String filename,double pi,double error,long nTot,int numWorkers,long duration) {

        File file = new File(filename);
        boolean append = true;

        try (PrintWriter pw = new PrintWriter(new FileWriter(file, append))) {

            if (file.length() == 0) {
                pw.println("PI,ERROR,NTOT,WORKERS,DURATION_NS");
            }

            pw.printf(Locale.US,
                    "%f,%e,%d,%d,%d%n",
                    pi, error, nTot, numWorkers, duration);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
