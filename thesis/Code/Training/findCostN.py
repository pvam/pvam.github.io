from sklearn import svm
from sklearn import metrics
from sklearn import datasets
from sklearn import cross_validation
from sklearn.pipeline import make_pipeline
from sklearn import preprocessing
import numpy as np
from sklearn.externals import joblib
import sys
import pickle

list = sys.argv
operator = list[1]
modelURI = "/home/dsl/Vamshi/LBEP/Training/Models/"+operator+"/"
sa = pickle.load(open(modelURI+"sa.pkl", 'rb'))
sb = pickle.load(open(modelURI+"sb.pkl", 'rb'))

parameters = list[2:]
parameters = sa.transform(parameters)

modelURI += "m.pkl"
clf = joblib.load(modelURI)
result = clf.predict(parameters)[0]
print sb.inverse_transform(result)