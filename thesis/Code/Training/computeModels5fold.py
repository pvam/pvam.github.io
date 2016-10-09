import os 
import SVMRegression
import SVMRegression5fold
import glob

list  = glob.glob("data/*.train")

for filepath in list:
	res = SVMRegression5fold.compute(filepath)
	#write a program to print the status of operators. 
	#operator accuracy. 
	#Final list of operators , the ones that need focus more.
