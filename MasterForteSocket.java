import java.io.*;
import java.net.*;
import java.util.Locale;

public class MasterForteSocket {

    static final int[] tab_port = {25545,25546,25547,25548,25549,25550,25551,25552};
    static final String ip = "127.0.0.1";

    static void writeCsv(String filename,double pi,double error,long nTot,int numWorkers,long duration) {

        File file = new File(filename);

        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {

            if (file.length() == 0)
                pw.println("PI,ERROR,NTOT,WORKERS,DURATION_NS");

            pw.printf(Locale.US,
                    "%f,%e,%d,%d,%d%n",
                    pi, error, nTot, numWorkers, duration);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

        int Ntot = 16000000; // PROBLÈME GLOBAL FIXE

        BufferedReader console =
                new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Number of workers: ");
        int numWorkers = Integer.parseInt(console.readLine());

        Socket[] sockets = new Socket[numWorkers];
        BufferedReader[] reader = new BufferedReader[numWorkers];
        PrintWriter[] writer = new PrintWriter[numWorkers];

        for (int i = 0; i < numWorkers; i++) {

            sockets[i] = new Socket(ip, tab_port[i]);

            reader[i] = new BufferedReader(
                    new InputStreamReader(sockets[i].getInputStream()));

            writer[i] = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(sockets[i].getOutputStream())),
                    true);
        }

        long start = System.nanoTime();

        int total = 0;
        int totalCount = Ntot / numWorkers;
        String message = String.valueOf(totalCount);

        for (int i = 0; i < numWorkers; i++)
            writer[i].println(message);

        for (int i = 0; i < numWorkers; i++)
            total += Integer.parseInt(reader[i].readLine());

        long duration = System.nanoTime() - start;

        double pi = 4.0 * total / Ntot;
        double error = Math.abs((pi - Math.PI)) / Math.PI;

        System.out.println("Pi = " + pi);

        writeCsv("resultats_forte_distribue.csv",
                pi, error, Ntot, numWorkers, duration);

        for (int i = 0; i < numWorkers; i++) {
            writer[i].println("END");
            reader[i].close();
            writer[i].close();
            sockets[i].close();
        }
    }
}
