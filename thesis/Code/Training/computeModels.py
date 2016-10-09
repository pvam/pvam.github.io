import os 
import SVMRegression
import SVMRegression5fold
import glob

list  = glob.glob("data/*.train")

for filepath in list:
	SVMRegression.compute(filepath)
