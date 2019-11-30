# -*- coding: utf-8 -*-
"""
Created on Sat Nov 30 10:32:47 2019

@author: Maria
"""

import matplotlib.pyplot as plt
import csv
import pandas
import scipy.stats as stats

tournament=[]
roulette=[]

with open('tournament.csv') as csvfile:
   readCSV = csv.reader(csvfile, delimiter=',')
   for row in readCSV:
      tournament.append(row[0])
      
with open('roulette.csv') as csvfile:
   readCSV = csv.reader(csvfile, delimiter=',')
   for row in readCSV:
      roulette.append(row[0])
      


def t_test(one, two):
   
   df1 = pandas.read_csv(one)
   df2 = pandas.read_csv(two)
   
   a = df1[['tournament']]
   b = df2[['roulette']]
   
   t, p = stats.ttest_ind(a,b)
   
   print(float(t))
   print(float(p))
   
t_test('tournament.csv', 'roulette.csv')




def shapiroWilkTest(dataset):
   
   df = pandas.read_csv(dataset)
   
   df1 = df[['tournament']]
   
   print(df1)
   
   W, P = stats.shapiro(df1)
   
   print(W)
   print(P)

shapiroWilkTest('tournament.csv')



def mannWhitneyTest(dataset1, dataset2):
   
   df1 = pandas.read_csv(dataset1)
   df2 = pandas.read_csv(dataset2)
   
   a = df1[['mean_squared_error']]
   b = df2[['mean_squared_error']]
   
   u, p = stats.mannwhitneyu(a, b)
   
   print(u)
   print(p)
   