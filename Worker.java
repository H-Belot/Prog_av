
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Task for running the Monte Carlo simulation.
 */
class Worker implements Callable<Long>  // comme un runable mais retourne une valeur
{   
    private int numIterations;
    public Worker(int num) 
	{ 
	    this.numIterations = num; 
	}

  @Override
      public Long call() 
      {
	  long circleCount = 0;
	  Random prng = new Random ();
	  for (int j = 0; j < numIterations; j++) //boucle de Monte Carlo qui renvoit le nombre de points dans le cercle
	      {
		  double x = prng.nextDouble();
		  double y = prng.nextDouble();
		  if ((x * x + y * y) < 1)  ++circleCount; 
	      }
	  return circleCount;
      }
}