# import numpy as np
# import matplotlib.pyplot as plt


# x = np.array([420149.57,337177.77,109652.14,186281.04,149297.07,173617.35,177071.02,89126.76,261911.65,12823.95,314256.99,175966.52,119869.16,41799.47 ,645858.49,1205993.8,70664.12 ,288257.26,284147.89])
# # linearly generated sequence

# y = np.array([35591.813,46025.334,82264.048,48798.893,13457.043,609587.411,333165.874,914761.532,25648.826,24030.624,20325.206,79698.316,18173.993,7966.992,492072.713,71797.456,88892.933,52106.978,29538.685])


# A = np.vstack([x, np.ones(len(x))]).T

# m, c = np.linalg.lstsq(A, y)[0]
# print m, c

# plt.plot(x, y, '*', label='Original data', markersize=10)
# plt.plot(x, m*x + c, 'r', label='Fitted cost model')
# plt.xlabel('Optimizers cost estimate')
# plt.ylabel('Actual execution time(ms)')
# plt.legend()
# plt.show()

import numpy as np
from sklearn import linear_model
import matplotlib.pyplot as plt

###############################################################################
A = np.array([[420149.57], [337177.77], [109652.14], [186281.04], [149297.07], [173617.35], [177071.02], [89126.760], [261911.65], [12823.950], [314256.99], [175966.52], [119869.16], [41799.470], [645858.49], [1205993.8], [70664.120], [288257.26], [284147.89]])

B = np.array([35591.813,46025.334,82264.048,48798.893,13457.043,609587.411,333165.874,914761.532,25648.826,24030.624,20325.206,79698.316,18173.993,7966.992,492072.713,71797.456,88892.933,52106.978,29538.685])


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
plt.plot(A, regr_predict , c='b', label='Least Squares Fit Line')
# plt.plot(A, B , c='g', label='True Model')
# plt.plot(A, y_lin, c='r', label='Linear model')
plt.xlabel("Optimizer's cost")
plt.ylabel('Actual Execution time(ms)')
plt.title('Optimizer estimates can incur significant errors.')
plt.legend()
plt.show()