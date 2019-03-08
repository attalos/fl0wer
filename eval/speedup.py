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
keys = df1[["ontology", "taskID"]].drop_duplicates(keep="first")

# print
speedups = [[], [], [], []]
totalTime = [[0,0], [0,0], [0,0], [0,0]]
for index, key in keys.iterrows():
    time1 = df1[(df1.ontology == key.ontology) & (df1.taskID == key.taskID)]["time"]
    time2 = df2[(df2.ontology == key.ontology) & (df2.taskID == key.taskID)]["time"]
    size  = df2[(df2.ontology == key.ontology) & (df2.taskID == key.taskID)]["classcount"].iloc[0]

    quotient = (time1/time2).tolist()
    time1 = time1.tolist()
    time2 = time2.tolist()

    if len(time2) == 0:
        continue

    print("\n################################\n")
    print("ontology: " + str(key.ontology))
    print("taskID:   " + str(key.taskID))
    print("size:     " + str(size) + " classes")
    print()
    if time1[0] != -1 and time2[0] != -1:
        print("fl0wer:   " + str(quotient[0]))
        speedups[0].append(quotient[0])
        totalTime[0][0] += time1[0]
        totalTime[0][1] += time2[0]
    if time1[1] != -1 and time2[1] != -1:
        print("hermit:   " + str(quotient[1]))
        speedups[1].append(quotient[1])
        totalTime[1][0] += time1[1]
        totalTime[1][1] += time2[1]
    if time1[2] != -1 and time2[2] != -1:
        print("openllet: " + str(quotient[2]))
        speedups[2].append(quotient[2])
        totalTime[2][0] += time1[2]
        totalTime[2][1] += time2[2]
    if time1[3] != -1 and time2[3] != -1:
        print("jfact:    " + str(quotient[3]))
        speedups[3].append(quotient[3])
        totalTime[3][0] += time1[3]
        totalTime[3][1] += time2[3]


print("\n\n#############################")
print(    "########## AVERAGE ##########")
print(    "#############################\n")
print("fl0wer:   mean = " + "{:.3f}".format(statistics.mean(speedups[0])) + "  median = " + "{:.3f}".format(statistics.median(speedups[0])) + "  total = " + "{:.3f}".format(totalTime[0][0]/totalTime[0][1]))
print("hermit:   mean = " + "{:.3f}".format(statistics.mean(speedups[1])) + "  median = " + "{:.3f}".format(statistics.median(speedups[1])) + "  total = " + "{:.3f}".format(totalTime[1][0]/totalTime[1][1]))
print("openllet: mean = " + "{:.3f}".format(statistics.mean(speedups[2])) + "  median = " + "{:.3f}".format(statistics.median(speedups[2])) + "  total = " + "{:.3f}".format(totalTime[2][0]/totalTime[2][1]))
print("jfact:    mean = " + "{:.3f}".format(statistics.mean(speedups[3])) + "  median = " + "{:.3f}".format(statistics.median(speedups[3])) + "  total = " + "{:.3f}".format(totalTime[3][0]/totalTime[3][1]))
