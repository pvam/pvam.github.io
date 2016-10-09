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
from sklearn import linear_model

filename  = "../dataop/IndexNestedLoop.train"
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
	nr = np.float(elm[1])
	# A.append([nl*nr])
	# A.append([nl+nr])
	A.append([nl * math.log(nl,2)])
	B.append(np.float(elm[len(elm)-1]))


regr = linear_model.LinearRegression()
regr.fit(A, B)

print(regr.coef_)

# The mean square error
regr_predict = regr.predict(A)
error = str(round(np.mean((regr_predict -B)**2),1))
print 'error '+ error
exp_variance = str(round(regr.score(A, B), 4)) 
print 'explained variance ' + exp_variance

# look at the results
plt.scatter(A, B, c='k', label='data')
plt.hold('on')
plt.plot(A, regr_predict , c='b', label='LinearRegression')
# plt.plot(A, B , c='g', label='True Model')
# plt.plot(A, y_lin, c='r', label='Linear model')
plt.xlabel('Nl * log(Nr,2)')
plt.ylabel('Execution time(ms) ')
# plt.title(operator)
plt.legend()
plt.show()