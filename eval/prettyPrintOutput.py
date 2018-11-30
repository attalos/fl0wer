#!/usr/bin/python3

import pandas
import sys

# get input
if len(sys.argv) != 3:
    print("This script needs exactly two parameter: the output.txt (1) file and the corresponding taskfile (2)")
    exit(1)
input_file = sys.argv[1]
task_file = sys.argv[2]
task_file_lines = open(task_file).readlines()

# parase csv
df = pandas.read_csv(input_file)

# get key list (ontology + taskID)
keys = df[["ontology", "taskID"]].drop_duplicates(keep="first")

# print blockwise
for index, key in keys.iterrows():
    print("\n#############################\n")
    print(task_file_lines[int(index/4)])
    print(df[(df.ontology == key.ontology) & (df.taskID == key.taskID)])
