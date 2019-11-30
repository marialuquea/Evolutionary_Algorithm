# -*- coding: utf-8 -*-
"""
Created on Sat Nov 30 09:46:16 2019

@author: Maria
"""
import matplotlib.pyplot as plt
import csv

x=[]

with open('tournament.txt') as csvfile:
   readCSV = csv.reader(csvfile, delimiter=',')
   for row in readCSV:
      x.append(row[0])
      
x = list(map(float, x))

x = sorted(x)

zscores = []

total = 0
count = 0

for score in x:
   total = total + score
   count = count + 1
   
mean = total/count

sd = 1.212633326

for n in x:
   first = n - mean 
   out = first / sd
   zscores.append(out)

plt.plot(zscores, x)
plt.xlim([-2.5,3])
plt.show()
