# -*- coding: utf-8 -*-
"""
Created on Sat Nov 30 09:25:59 2019

@author: Maria
"""

import csv

with open('roulette.txt', 'r') as in_file:
    stripped = (line.strip() for line in in_file)
    lines = (line.split(",") for line in stripped if line)
    with open('roulette.csv', 'w') as out_file:
        writer = csv.writer(out_file)
        writer.writerow(("roulette"))
        writer.writerows(lines)