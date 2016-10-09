import os 
import SVMRegressionN
import SVMRegression5fold
import glob

list  = glob.glob("data/*.train")

for filepath in list:
	SVMRegressionN.compute(filepath)
