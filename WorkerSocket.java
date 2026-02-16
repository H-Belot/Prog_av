import java.io.*;
import java.net.*;
import java.util.Random;
/**
 Worker est un serveur. Il attend les requetes du Master.
 *il calcule localement PI par la methode de Monte Carlo et envoie
 */
public class WorkerSocket {
    static int port = 25545; //default port
    private static boolean isRunning = true;
    
    /**
     * compute PI locally by MC and sends the number of points 
     * inside the disk to Master. 
     */
    public static void main(String[] args) throws Exception {

        if (args.length > 0) 
        port=Integer.parseInt(args[0]);
	    System.out.println(port);
        ServerSocket s = new ServerSocket(port);
        System.out.println("Server started on port " + port);
        Socket soc = s.accept();
	
        // BufferedReader bRead for reading message from Master
        BufferedReader bRead = new BufferedReader(new InputStreamReader(soc.getInputStream())); // interface du flux d'entree du socket côté serveur

        // PrintWriter pWrite for writing message to Master
        PrintWriter pWrite = new PrintWriter(new BufferedWriter(new OutputStreamWriter(soc.getOutputStream())), true); // interface du flux de sortie du socket coté serveur
	    String str;
        while (isRunning) {
	    str = bRead.readLine();          // read message from Master
	    if (!(str.equals("END"))){
		System.out.println("Server receives totalCount = " +  str);
		
            // compute
            System.out.println("TODO : compute Monte Carlo and send total");
            long iterations = Long.parseLong(str);
            long circleCount = calculPi(iterations);
            System.out.println("Number of points in the circle: " + circleCount);

            pWrite.println(circleCount);// send number of points in quarter of disk
	    }
        else{
		    isRunning=false;
	    }	    
        }
        bRead.close();
        pWrite.close();
        soc.close();
   }

    public static Long calculPi(long iterations) 
      {
	  long circleCount = 0;
	  Random prng = new Random ();
	  for (long j = 0; j < iterations ; j++) //boucle de Monte Carlo qui renvoit le nombre de points dans le cercle
	      {
		  double x = prng.nextDouble();
		  double y = prng.nextDouble();
		  if ((x * x + y * y) < 1){
            ++circleCount; 
          }  
	      }
	  return circleCount;
      }
}