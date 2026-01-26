import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
// relire et essayer de changer la taille des taches et le nombre de workers, on le fait dans le master


/**
 * Approximates PI using the Monte Carlo method.  Demonstrates
 * use of Callables, Futures, and thread pools.
 */
public class Pi 
{
    public static void main(String[] args) throws Exception 
    {
	long total=0;
	// 10 workers, 50000 iterations each
	total = new Master().doRun(50000, 10);
	System.out.println("total from Master = " + total);
    }
}

/**
 * Créer des workers pour exécuter la simulation de Monte Carlo
 * et d'agréger les résultats.
 * Le master n'attends pas que chaque worker termine individuellement,
 * mais lance tous les workers et attend qu'ils soient tous terminés
 * avant de rassembler les résultats. C'est plus efficace que d'attendre, c'est assynchrone.
 */
class Master {
    public long doRun(int totalCount, int numWorkers) throws InterruptedException, ExecutionException // lance les workers et agrège les résultats
    {

	long startTime = System.currentTimeMillis(); // démarrage du chronomètre

	// Créer une liste de tâches Callable qui renvoies des longs
	List<Callable<Long>> tasks = new ArrayList<Callable<Long>>(); // création d'un arraylist de callable
	for (int i = 0; i < numWorkers; ++i) // pour chaque worker
	    {
		tasks.add(new Worker(totalCount)); // instanciation d'un worker pour chaque tâche
	    }
    
	// lancer les tâches et récupérer les Futures
	ExecutorService exec = Executors.newFixedThreadPool(numWorkers); // création d'un pool (ensemble de thread) de thread fixe en fonction du nombre de worker
	List<Future<Long>> results = exec.invokeAll(tasks); // créer une liste de future pour récupérer les résultats des tâches callable exécutées. 
	// invokeAll lance toutes les tâches et bloque jusqu'à ce qu'elles soient terminées
	long total = 0; // variable pour stocker le total des points dans le cercle

	// Assemble les résultats.
	for (Future<Long> f : results) //Parcours results et pour chaque future je regarde f 
	    {
		// Appelle get() pour obtenir le résultat de chaque tâche Callable
		// get() bloque jusqu'à ce que le résultat soit disponible
		//Les futures sont du chainage de dépendance, le master attend que les workers aient finis pour récupérer les résultats
		total += f.get(); // on ajoute le résultat de chaque future au total pour master qui a besoin de ce total pour calculer pi
	    }
	double pi = 4.0 * total / totalCount / numWorkers; // calcul de pi

	long stopTime = System.currentTimeMillis();

	System.out.println("\nPi : " + pi );
	System.out.println("Error: " + (Math.abs((pi - Math.PI)) / Math.PI) +"\n");

	System.out.println("Ntot: " + totalCount*numWorkers);
	System.out.println("Available processors: " + numWorkers);
	System.out.println("Time Duration (ms): " + (stopTime - startTime) + "\n");

	System.out.println( (Math.abs((pi - Math.PI)) / Math.PI) +" "+ totalCount*numWorkers +" "+ numWorkers +" "+ (stopTime - startTime));

	exec.shutdown();
	return total;
    }
}

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
