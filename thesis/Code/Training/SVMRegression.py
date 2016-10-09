# USAGE
# >>> from sklearn import svm
# >>> X = [[0, 0], [2, 2]]
# >>> y = [0.5, 2.5]
# >>> clf = svm.SVR()
# >>> clf.fit(X, y) 
# SVR(C=1.0, cache_size=200, coef0=0.0, degree=3, epsilon=0.1, gamma=0.0,
#     kernel='rbf', max_iter=-1, shrinking=True, tol=0.001, verbose=False)
# >>> clf.predict([[1, 1]])
# array([ 1.5])

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

def compute(filename):
	fileArray = filename.split("/")
	operator = fileArray[-1].split(".")[0]

	print 'SVM received operator = ' + operator
	ip = open(filename)
	i = 0
	A = []
	B = []
	for line in ip:
		temp = []
		elm = line.rstrip("\n").split(" ");
		temp = [np.exp(float(row)) for row in range(len(elm)-1) ]

		# A.append([temp[0] ,temp[1]])
		A.append(temp)
		B.append(np.float(elm[len(elm)-1]))

	clf = svm.SVR()
	clf.fit(A,B)


	# f.close()
	modelURI = "Models/"+operator+"/"
	if not os.path.exists(modelURI):
		os.makedirs(modelURI)
	
	modelURI += 'm.pkl'
	joblib.dump(clf, modelURI)

	print 'SUCCESS,' + modelURI + ' written to disk.'
