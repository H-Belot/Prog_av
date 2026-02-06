import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv("resultats_faible_median.csv")

workers = df["WORKERS"]
time_ns = df["MEDIAN_DURATION_NS"]

plt.figure()
plt.plot(workers, time_ns, marker='o')

plt.xlabel("Nombre de workers")
plt.ylabel("Temps d'exécution médian (ns)")
plt.title("Scalabilité faible - Monte Carlo Pi")
plt.grid(True)

plt.show()
