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
from sklearn.metrics import make_scorer
from sklearn import datasets
from sklearn import cross_validation
from sklearn.pipeline import make_pipeline
from sklearn import preprocessing
from sklearn.preprocessing import StandardScaler
import numpy as np
from sklearn.externals import joblib
import os 
import sys


def mean_relative_error(ground_truth, predictions):
    error = np.average(np.divide( np.abs(ground_truth - predictions) , ground_truth))
    return error

def median_relative_error(ground_truth, predictions):
    error = np.median(np.divide( np.abs(ground_truth - predictions) , ground_truth))
    return error

def compute(filename):
	fileArray = filename.split("/")
	operator = fileArray[-1].split(".")[0]
	#print 'filename : ' + filename
	#print 'received' + operator

	ip = open(filename)
	i = 0
	A = []
	B = []
	for line in ip:
		temp = []
		elm = line.rstrip("\n").split(" ");
		#print line
		temp = [np.exp(float(row)) for row in range(len(elm)-1) ]

		# A.append([temp[0] ,temp[1]])
		A.append(temp)
		B.append(np.float(elm[len(elm)-1]))

	# f = open('/home /dsl/Vamshi/LBEP/Data/SeqScan-R.txt','w')
	# clf = make_pipeline(preprocessing.StandardScaler(), svm.SVR())
	# scalerA = StandardScaler().fit(A)
	# scalerB = StandardScaler().fit(B)

	# A = scalerA.transform(A)
	# B = scalerB.transform(B)

	clf = svm.SVR()
	clf.fit(A,B)
	meanre_scoring  = make_scorer(mean_relative_error, greater_is_better=True)
	# 5 - fold testing.
	# print scores
	value = cross_validation.cross_val_score(clf, A, B, cv=10, scoring=meanre_scoring)
	print operator + ","+ str(np.average(value))
	#print str(value)

	return value;
