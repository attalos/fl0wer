#!/usr/bin/python3

import pandas
import sys
import statistics

# get input
if len(sys.argv) != 3:
    print("This script needs exactly two parameters. The two output files")
    exit(1)

input1 = sys.argv[1]
input2 = sys.argv[2]

# parse csv
df1 = pandas.read_csv(input1)
df2 = pandas.read_csv(input2)

# get key list (ontology + taskID)
keys = df1[["ontology", "taskID", "classcount"]].drop_duplicates(keep="first")

# print
speedups = [[], [], [], []]
for index, key in keys.iterrows():
    time1 = df1[(df1.ontology == key.ontology) & (df1.taskID == key.taskID)]["time"]
    time2 = df2[(df2.ontology == key.ontology) & (df2.taskID == key.taskID)]["time"]

    quotient = (time1/time2).tolist()
    time1 = time1.tolist()
    time2 = time2.tolist()

    if len(time2) == 0:
        continue

    print("\n#############################\n")
    print("ontology: " + str(key.ontology))
    print("taskID:   " + str(key.taskID))
    print("size:     " + str(key.classcount))
    print()
    if time1[0] != -1 and time2[0] != -1:
        print("fl0wer:   " + str(quotient[0]))
        speedups[0].append(quotient[0])
    if time1[1] != -1 and time2[1] != -1:
        print("hermit:   " + str(quotient[1]))
        speedups[1].append(quotient[1])
    if time1[2] != -1 and time2[2] != -1:
        print("openllet: " + str(quotient[2]))
        speedups[2].append(quotient[2])
    if time1[3] != -1 and time2[3] != -1:
        print("jfact:    " + str(quotient[3]))
        speedups[3].append(quotient[3])


print("\n\n#############################")
print(    "########## AVERAGE ##########")
print(    "#############################\n")
print("fl0wer:   mean = " + "{:.3f}".format(statistics.mean(speedups[0])) + "  median = " + "{:.3f}".format(statistics.median(speedups[0])))
print("hermit:   mean = " + "{:.3f}".format(statistics.mean(speedups[1])) + "  median = " + "{:.3f}".format(statistics.median(speedups[1])))
print("openllet: mean = " + "{:.3f}".format(statistics.mean(speedups[2])) + "  median = " + "{:.3f}".format(statistics.median(speedups[2])))
print("jfact:    mean = " + "{:.3f}".format(statistics.mean(speedups[3])) + "  median = " + "{:.3f}".format(statistics.median(speedups[3])))
