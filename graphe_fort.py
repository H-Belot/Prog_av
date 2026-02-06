import pandas as pd
import matplotlib.pyplot as plt

# Lire le CSV
df = pd.read_csv("resultats_forte_speedup.csv")

workers = df["WORKERS"]
speedup = df["SPEEDUP"]

# Courbe idéale
ideal = workers

plt.figure()
plt.plot(workers, speedup, marker='o', label="Speedup mesuré")
plt.plot(workers, ideal, linestyle='--', label="Speedup idéal")

plt.xlabel("Nombre de workers")
plt.ylabel("Speedup")
plt.title("Scalabilité forte - Monte Carlo Pi")
plt.legend()
plt.grid(True)

plt.show()
