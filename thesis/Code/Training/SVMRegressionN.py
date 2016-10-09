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

		A.append(temp)
		B.append(np.float(elm[len(elm)-1]))



	# f= open('data','w')
	# f.write(str(A))
	# f.close()
	# f = open('target','w')
	# f.write(str(B))
	# f.close()

	# f = open('/home /dsl/Vamshi/LBEP/Data/SeqScan-R.txt','w')

	scalerA = StandardScaler().fit(A)
	scalerB = StandardScaler().fit(B)

	A = scalerA.transform(A)
	B = scalerB.transform(B)

	clf = svm.SVR()
	clf.fit(A,B)

	# 5 - fold testing.
	# scores =  cross_validation.cross_val_score(clf, A, B, cv=5, scoring='mean_squared_error')

	# print scores
	# print("SVM Model" + clf)
	# print("mean_absolute_error \n" + str(scores))

	# f.close()
	modelURI = "Models/"+operator+"/"
	if not os.path.exists(modelURI):
		os.makedirs(modelURI)

	scalerAop = open(modelURI+'sa.pkl', 'wb')
	pickle.dump(scalerA,scalerAop)
	scalerBop = open(modelURI+'sb.pkl', 'wb')
	pickle.dump(scalerB,scalerBop)
	

	scalerAop.close();
	scalerBop.close();
	
	modelURI += 'm.pkl'
	joblib.dump(clf, modelURI)

	print 'SUCCESS,' + modelURI + ' written to disk.'