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
    "log",         "l", 0, "logical",
    "jfactignore", "j", 0, "logical",
    "out",         "o", 1, "character",
    "input",       "i", 1, "character",
    "help",        "h", 0, "logical"
), byrow=TRUE, ncol=4);

opt = getopt(spec);

#print help
if ( !is.null(opt$help) ) {
  cat(getopt(spec, usage=TRUE));
  q(status=1);
}

if ( is.null(opt$input  ) ) { print("-i is required"); quit();}
if ( is.null(opt$log    ) ) { opt$log    = FALSE }
if ( is.null(opt$jfactignore ) ) { opt$jfactignore = FALSE }
if ( is.null(opt$out    ) ) { opt$out    = "" }

dt = fread(opt$input, sep=",", header=TRUE)

log_graph = ""
if (opt$log) { log_graph = "xy" }

#ggplot(dt[reasoner=="Fl0wer", mean(time), .(ontology, classcount)][order(classcount)][,.(classcount,V1)], aes(x=classcount, y=V1)) + geom_point() + geom_line()
#ylim=range(t[2],tt[2])

flowerData = reasonerData_f(dt, "Fl0wer")
hermitData = reasonerData_f(dt, "Hermit")
openlletData = reasonerData_f(dt, "Openllet")

if (opt$jfactignore) {
	ylim = range(flowerData[,mean] + flowerData[,error], hermitData[,mean] + hermitData[,error], openlletData[,mean] + openlletData[,error])
	plot(flowerData[,classcount], flowerData[,mean], type="b", pch="-", col="blue", ylim=ylim, log=log_graph, ylab="time [ms]", xlab="classcount")
	
	errorbar_f(flowerData, "blue")
	errorbar_f(hermitData, "red")
	errorbar_f(openlletData, "green")
	
	lines(hermitData, type="b", pch="-", col="red")
	lines(openlletData, type="b", pch="-", col="green")
	
	legend("topleft", inset=.04, legend=c("Fl0wer", "Hermit", "Openllet"), col=c("blue", "red", "green"), lty=1)
} else {
	jfactData = reasonerData_f(dt, "JFact")
	
	ylim = range(flowerData[,mean] + flowerData[,error], hermitData[,mean] + hermitData[,error], openlletData[,mean] + openlletData[,error], jfactData[,mean] + jfactData[,error])
	plot(flowerData[,classcount], flowerData[,mean], type="b", pch="-", col="blue", ylim=ylim, log=log_graph, ylab="time [ms]", xlab="classcount")
	
	errorbar_f(flowerData, "blue")
	errorbar_f(hermitData, "red")
	errorbar_f(openlletData, "green")
	errorbar_f(jfactData, "black")
	
	lines(hermitData, type="b", pch="-", col="red")
	lines(openlletData, type="b", pch="-", col="green")
	lines(jfactData, type="b", pch="-", col="black")
	
	legend("topleft", inset=.04, legend=c("Fl0wer", "Hermit", "Openllet", "JFact"), col=c("blue", "red", "green", "black"), lty=1)
}

#arrows(bla, , xx, up, col = repcols, angle = 90, length = 0.03, code = 3)

message("Press return to continue")
invisible(readLines("stdin", n=1))
