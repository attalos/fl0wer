#!/usr/bin/python3

import pandas
import sys
import numpy as np
import rpy2.robjects.packages as rpackages
import rpy2.robjects as robj
from rpy2.robjects import pandas2ri
from rpy2.robjects.lib import grid
from rpy2.robjects.packages import importr

utils = rpackages.importr('utils')
utils.chooseCRANmirror(ind=1) # select the first mirror in the list

# ggplot2
def install_packages():
    # R package names
    packnames = ["ggplot2"]

    # R vector of strings
    from rpy2.robjects.vectors import StrVector

    # Selectively install what needs to be install.
    # We are fancy, just because we can.
    names_to_install = [x for x in packnames if not rpackages.isinstalled(x)]
    if len(names_to_install) > 0:
        utils.install_packages(StrVector(names_to_install))

install_packages()
import rpy2.robjects.lib.ggplot2 as ggplot2


def mean_of_valid(values):
    return np.mean([x for x in values if x != -1])

#params
if len(sys.argv) != 2:
    print("Output file needed")
    exit(1)

filename = sys.argv[1]

#device
grdevices = importr('grDevices')
grdevices.pdf(file="graph.pdf", width=20, height=10)

########
# MAIN #
########
df = pandas.read_csv(filename)
df["mean_time"] = df["time"].groupby(df["ontology"]).transform(mean_of_valid)

robj.pandas2ri.activate()
plotting_data_R = robj.conversion.py2ri(df)

# page
grid.newpage()
lt = grid.layout(1, 2)
vp = grid.viewport(layout = lt)
vp.push()

# diagram one - dependent on classcount
gp = ggplot2.ggplot(plotting_data_R)
pp = gp + \
     ggplot2.aes_string(x="classcount", y="time", colour="reasoner", shape="reasoner") + \
     ggplot2.geom_point() + \
     ggplot2.scale_x_log10() + \
     ggplot2.scale_y_log10()
vp = grid.viewport(**{'layout.pos.col':1, 'layout.pos.row': 1})
pp.plot(vp=vp)

# diagram two - dependent on mean time
pp = gp + \
     ggplot2.aes_string(x="mean_time", y="time", colour="reasoner", shape="reasoner") + \
     ggplot2.geom_point() + \
     ggplot2.scale_x_log10() + \
     ggplot2.scale_y_log10()
vp = grid.viewport(**{'layout.pos.col':2, 'layout.pos.row': 1})
pp.plot(vp=vp)

input("Press Enter to continue...")




##df.set_index(["taskID", "reasonr", "ontology"], inplace=True)
##
##print(df)
##
### transform the data in an easyer to print table
##plotting_data = pandas.DataFrame(columns=["classcount", "ontology"])
##for reasoner in ["Fl0wer", "Hermit", "Openllet", "JFact"]:
##    tmp_df = df.loc[0].loc[reasoner, ["classcount", "time"]]
##    tmp_df.rename(columns={"time":reasoner}, inplace=True)
##    plotting_data = pandas.merge(plotting_data, tmp_df, on=["ontology", "classcount"], how="outer")
##
##print(plotting_data)
##
##plotting_data_R = robj.conversion.py2ri(plotting_data)
