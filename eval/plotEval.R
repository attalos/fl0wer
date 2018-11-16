#!/usr/bin/Rscript
require(data.table)
require(getopt)
X11()

mean_f <- function(data) {
	meanValue = mean(data)
	if (meanValue < 1) {
		meanValue = 1
	}
	return(meanValue)
}

error_f <- function(data) {
	error = sd(data)/sqrt(length(data))
    if (is.na(error)) {
        return(0)
    }
	return(error)
}

reasonerData_f <- function(data, reasonerName) {
	return(dt[reasoner==reasonerName, .(mean=mean_f(time), error=error_f(time)), .(ontology, classcount)][order(classcount)][,.(classcount,mean,error)])
}

errorbar_f <- function(data, col) {
	arrows(c(data[,classcount]), data[,mean] - data[,error], c(data[,classcount]), data[,mean] + data[,error], length=0.05, angle=90, code=3, col=col)
}

args = commandArgs(trailingOnly = TRUE)

#read parameters
spec = matrix(c(
    "input",           "i", 1, "character",
    "out",             "o", 1, "character",
    "outtype",         "" , 1, "character",
    "log",             "l", 0, "logical",
    "type",            "t", 1, "character",
    "symbol",          "s", 1, "character",
    "symbolFl0wer",    "0", 1, "character",
    "symbolHermit",    "1", 1, "character",
    "symbolOpenllet",  "2", 1, "character",
    "symbolJFact",     "3", 1, "character",
    "xupper",          "x", 1, "integer",
    "yupper",          "y", 1, "integer",
    "xlower",          "4", 1, "integer",
    "ylower",          "5", 1, "integer",
    "jfactignore",     "j", 0, "logical",
    "help",            "h", 0, "logical"
), byrow=TRUE, ncol=4);

opt = getopt(spec);

#print help
if ( !is.null(opt$help) ) {
  cat(getopt(spec, usage=TRUE));
  q(status=1);
}

if ( is.null(opt$input  ) ) { print("-i is required"); quit();}
if ( is.null(opt$out    ) ) { opt$out    = "" }
if ( is.null(opt$outtype) ) { opt$outtype= "display" }
if ( is.null(opt$log    ) ) { opt$log    = FALSE }
if ( is.null(opt$type   ) ) { opt$type   = "p" }
if ( is.null(opt$symbol ) ) { opt$symbol = "x" }
if ( is.null(opt$symbolFl0wer   ) ) { opt$symbolFl0wer   = opt$symbol }
if ( is.null(opt$symbolHermit   ) ) { opt$symbolHermit   = opt$symbol }
if ( is.null(opt$symbolOpenllet ) ) { opt$symbolOpenllet = opt$symbol }
if ( is.null(opt$symbolJFact    ) ) { opt$symbolJFact    = opt$symbol }
if ( is.null(opt$xupper   ) ) { opt$xupper   = -1 }
if ( is.null(opt$yupper   ) ) { opt$yupper   = -1 }
if ( is.null(opt$xlower   ) ) { opt$xlower   = 1 }
if ( is.null(opt$ylower   ) ) { opt$ylower   = 1 }
if ( is.null(opt$jfactignore ) ) { opt$jfactignore = FALSE }

dt = fread(opt$input, sep=",", header=TRUE)

log_graph = ""
if (opt$log) { log_graph = "xy" }

#ggplot(dt[reasoner=="Fl0wer", mean(time), .(ontology, classcount)][order(classcount)][,.(classcount,V1)], aes(x=classcount, y=V1)) + geom_point() + geom_line()
#ylim=range(t[2],tt[2])

flowerData = reasonerData_f(dt, "Fl0wer")
hermitData = reasonerData_f(dt, "Hermit")
openlletData = reasonerData_f(dt, "Openllet")

#write to file
if (opt$out != "") {
    pdf(opt$out)
}

if (opt$jfactignore) {
    # x limits
    if (opt$xupper != -1) {
        xlim = c(opt$xlower, opt$xupper)
    } else {
        xlim = range(flowerData[,classcount])
    }
    
    # y limits
    if (opt$yupper != -1) {
        ylim = c(opt$ylower, opt$yupper)
    } else {
        ylim = range(flowerData[,mean] + flowerData[,error], hermitData[,mean] + hermitData[,error], openlletData[,mean] + openlletData[,error])
    }

	plot(flowerData[,classcount], flowerData[,mean], type=opt$type, pch=opt$symbolFl0wer, col="blue", xlim=xlim, ylim=ylim, log=log_graph, ylab="time [ms]", xlab="classcount")
	
	errorbar_f(flowerData, "blue")
	errorbar_f(hermitData, "red")
	errorbar_f(openlletData, "green")
	
	lines(hermitData, type=opt$type, pch=opt$symbolHermit, col="red")
	lines(openlletData, type=opt$type, pch=opt$symbolOpenllet, col="green")
	
	legend("topleft", inset=.04, legend=c("Fl0wer", "Hermit", "Openllet"), col=c("blue", "red", "green"), lty=1)
} else {
	jfactData = reasonerData_f(dt, "JFact")

    # x limits
    if (opt$xupper != -1) {
        xlim = c(opt$xlower, opt$xupper)
    } else {
        xlim = range(flowerData[,classcount])
    }
    
    # y limits
    if (opt$yupper != -1) {
        ylim = c(opt$ylower, opt$yupper)
    } else {
	    ylim = range(flowerData[,mean] + flowerData[,error], hermitData[,mean] + hermitData[,error], openlletData[,mean] + openlletData[,error], jfactData[,mean] + jfactData[,error])
    }
    print(xlim)
    print(ylim)
	
	plot(flowerData[,classcount], flowerData[,mean], type=opt$type, pch=opt$symbolFl0wer, col="blue", xlim=xlim, ylim=ylim, log=log_graph, ylab="time [ms]", xlab="classcount")
	
	errorbar_f(flowerData, "blue")
	errorbar_f(hermitData, "red")
	errorbar_f(openlletData, "green")
	errorbar_f(jfactData, "black")
	
	lines(hermitData, type=opt$type, pch=opt$symbolHermit, col="red")
	lines(openlletData, type=opt$type, pch=opt$symbolOpenllet, col="green")
	lines(jfactData, type=opt$type, pch=opt$symbolJFact, col="black")
	
	legend("topleft", inset=.04, legend=c("Fl0wer", "Hermit", "Openllet", "JFact"), col=c("blue", "red", "green", "black"), lty=1)
}

#arrows(bla, , xx, up, col = repcols, angle = 90, length = 0.03, code = 3)

if (opt$out != "") {
    dev.off()
} else {
    message("Press return to continue")
    invisible(readLines("stdin", n=1))
}
