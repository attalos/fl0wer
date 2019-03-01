#!/usr/bin/python3

import pandas
import sys
import numpy as np
import argparse
import warnings
import subprocess
import rpy2.robjects.packages as rpackages
import rpy2.robjects as robj
from rpy2.robjects import pandas2ri
from rpy2.robjects.lib import grid
from rpy2.robjects.packages import importr

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
        utils = rpackages.importr('utils')
        utils.chooseCRANmirror(ind=1) # select the first mirror
        utils.install_packages(StrVector(names_to_install))

# import ggplot2
install_packages()
warnings.filterwarnings("ignore", category=UserWarning)
import rpy2.robjects.lib.ggplot2 as ggplot2


def mean_of_valid(values):
    return np.mean([x for x in values if x != -1])

# r functions
r_TRUE = robj.r("TRUE")
r_FALSE = robj.r("FALSE")

########
# MAIN #
########
# argument parsing
parser = argparse.ArgumentParser(description="Plot the given files")
parser.add_argument("filenames", metavar="<filename>", type=str, nargs="+", help="csv input file (output of execute.sh)")
parser.add_argument("-o", "--output", metavar="<filename>", type=str, default="graph.pdf", help="the graph will be written to this file")
parser.add_argument("--open", action="store_true", help="open the graph with the default application (xdg-open <filename>)")
args = parser.parse_args()

# page
grdevices = importr('grDevices')
grdevices.pdf(file=args.output, width=20, height=10*len(args.filenames))
grid.newpage()
lt = grid.layout(len(args.filenames), 2)
vp = grid.viewport(layout = lt)
vp.push()

row = 0
for filename in args.filenames:
    row += 1

    df = pandas.read_csv(filename)
    df["mean_time"] = df["time"].groupby(df["ontology"]).transform(mean_of_valid)

    robj.pandas2ri.activate()
    plotting_data_R = robj.conversion.py2ri(df)


    # diagram one - dependent on classcount
    # ggplot2.geom_smooth(method="lm", formula="y ~ splines::bs(x, 3)", se=r_FALSE) + \
    # https://en.wikipedia.org/wiki/Local_regression
    gp = ggplot2.ggplot(plotting_data_R)
    pp = gp + \
        ggplot2.aes_string(x="classcount", y="time", colour="reasoner", shape="reasoner") + \
        ggplot2.geom_point() + \
        ggplot2.geom_smooth(method="loess", se=r_FALSE) + \
        ggplot2.scale_x_log10() + \
        ggplot2.scale_y_log10()
    vp = grid.viewport(**{'layout.pos.col':1, 'layout.pos.row': row})
    pp.plot(vp=vp)

    # diagram two - dependent on mean time
    pp = gp + \
        ggplot2.aes_string(x="mean_time", y="time", colour="reasoner", shape="reasoner") + \
        ggplot2.geom_point() + \
        ggplot2.geom_smooth(method="loess", se=r_FALSE) + \
        ggplot2.scale_x_log10() + \
        ggplot2.scale_y_log10()
    vp = grid.viewport(**{'layout.pos.col':2, 'layout.pos.row': row})
    pp.plot(vp=vp)

#grdevices.dev.off()
robj.r("dev.off()")

if args.open:
    subprocess.call(("xdg-open", args.output))
