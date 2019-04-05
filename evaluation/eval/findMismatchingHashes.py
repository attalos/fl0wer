#!/usr/bin/python3

import pandas
import sys

# get input
if len(sys.argv) != 2:
    print("This script needs exactly one parameter: the input file")
    exit(1)
input_file = sys.argv[1]

# parase csv
df = pandas.read_csv(input_file)

# find keys where the hash has a mismatch
# i actually hate the two following lines, because it think they are not as
# simple as they could be, but they work...
filtered = (df[df.answerHash != "missingAnswer"].groupby(["ontology", "taskID"]).answerHash.nunique())
keys_of_errors = (filtered[filtered != 1]).index.values

# print those blocks
print("##############################")
print("### Mismatching answers at ###")
print("##############################")
for error_keys in keys_of_errors:
    print("\n")
    print(df[(df.ontology == error_keys[0]) & (df.taskID == error_keys[1])])
