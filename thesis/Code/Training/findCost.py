from sklearn import svm
from sklearn import metrics
from sklearn import datasets
from sklearn import cross_validation
from sklearn import preprocessing
import numpy as np
from sklearn.externals import joblib
import sys
import pickle
import warnings

list = sys.argv
warnings.simplefilter("ignore")
operator = list[1]
modelURI = "/home/vamshi/LBEP/Training/Models/"+operator+"/m.pkl"
#print 'trying to open file' + modelURI
clf = joblib.load(modelURI)
print clf.predict(list[2:])[0]
