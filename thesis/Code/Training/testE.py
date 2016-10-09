from sklearn import svm
from sklearn import metrics
from sklearn import datasets
from sklearn import cross_validation
from sklearn.pipeline import make_pipeline
from sklearn import preprocessing
from sklearn.preprocessing import StandardScaler
import numpy as np
from sklearn.externals import joblib
import os 
import sys
import pickle
import glob

ip = open('data/Sort-quicksort.train')
for line in ip:
		temp = []
		elm = line.rstrip("\n").split(" ");
		temp = [np.exp(float(row)) for row in range(len(elm)-1) ]
		print len(temp)

