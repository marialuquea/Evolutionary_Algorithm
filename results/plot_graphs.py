import matplotlib.pyplot as plt
import csv

x=[]
y=[]

with open('pacing3.csv') as csvfile:
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

