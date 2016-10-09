from scipy.optimize import nnls
ip = open('/home/dsl/Vamshi/LBEP/Data/SeqScan.txt')
i = 0
A = []
B = []
for line in ip:
	temp = []
	elm = line.rstrip("\n").split(" ");
	for index in range(len(elm)-1):
		temp.append(elm[index])

	A.append(temp)
	B.append(elm[len(elm)-1])

f = open('/home/dsl/Vamshi/LBEP/Data/SeqScan-R.txt','w')
r = nnls(A,B)
for val in r[0]:
	f.write(str(val)+"\n")
f.close()
