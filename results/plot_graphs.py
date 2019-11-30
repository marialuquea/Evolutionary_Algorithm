import matplotlib.pyplot as plt
import csv

x=[]
y=[]

with open('testing/tournament.txt') as csvfile:
   readCSV = csv.reader(csvfile, delimiter=',')
   for row in readCSV:
      x.append(row[0])
      y.append(row[1])
         
y = list(map(float, y))
# Plot
plt.scatter(x, y)
plt.title('How parameters affect initial population')
plt.xlabel('Parameter choice')
plt.ylabel('Race time')
plt.show()


import matplotlib.pyplot as plt
import numpy as np
import scipy.stats as stats
import math

mu = 0
variance = 1
sigma = math.sqrt(variance)
y = np.linspace(mu - 3*sigma, mu + 3*sigma, 100)
plt.plot(y, x)
plt.show()
