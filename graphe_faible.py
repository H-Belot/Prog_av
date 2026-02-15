import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv("resultats_faible_efficiency.csv")

workers = df["WORKERS"]
eff = df["E_WEAK"]

plt.figure()
plt.plot(workers, eff, marker='o')

# Ligne idéale
plt.axhline(1, linestyle='--')

plt.xlabel("Nombre de workers (p)")
plt.ylabel("Efficacité faible E(p)")
plt.title("Scalabilité faible - Monte Carlo Pi")

plt.ylim(0, 1.1)
plt.grid(True)

plt.show()
