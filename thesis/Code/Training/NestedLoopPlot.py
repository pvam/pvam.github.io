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
import math
from sklearn.svm import SVR
from sklearn import metrics
from sklearn import datasets
from sklearn import cross_validation
from sklearn.pipeline import make_pipeline
from sklearn import preprocessing
from sklearn.preprocessing import StandardScaler
import numpy as np
from sklearn.externals import joblib
import matplotlib.pyplot as plt
import os 
import sys
import pickle
filename  = str(sys.argv[1])
fileArray = filename.split("/")
operator = fileArray[-1].split(".")[0]

print 'SVM received operator = ' + operator
ip = open(filename)
i = 0
A = []
B = []
for line in ip:
	elm = line.rstrip("\n").split(" ")
	nl = np.float(elm[0])
	
	nr = nrt * math.log(nrt,2)
	res = nl * nr;
	temp = [res]

	A.append(temp)
	B.append(np.float(elm[len(elm)-1]))


print len(A)
print len(B)
print "-----"
# Fit regression model
svr_rbf = SVR(kernel='rbf')
svr_lin = SVR(kernel='linear')
y_rbf = svr_rbf.fit(A, B).predict(A)
y_lin = svr_lin.fit(A, B).predict(A)

###############################################################################
# look at the results
plt.scatter(A, B, c='k', label='data')
plt.hold('on')
plt.plot(A, y_rbf, c='g', label='RBF model')
plt.plot(A, y_lin, c='r', label='Linear model')
plt.xlabel('data')
plt.ylabel('target')
plt.title(operator + '- Support Vector Regression')
plt.legend()
plt.show()