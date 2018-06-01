#!/usr/bin/Rscript
require(data.table)
X11()

args = commandArgs(trailingOnly = TRUE)
dt = fread(args[1], sep=",", header=TRUE)

log_graph = ""
ignore_jfact = FALSE
if (length(args) == 2) {
	if (args[2] %in% c("--log", "-l") ) {
		log_graph = "yx"
	}

	if (args[2] %in% c("--ignore", "-i")) {
		    ignore_jfact = TRUE
	}
}

#ggplot(dt[reasoner=="Fl0wer", mean(time), .(ontology, classcount)][order(classcount)][,.(classcount,V1)], aes(x=classcount, y=V1)) + geom_point() + geom_line()
#ylim=range(t[2],tt[2])

flowerData = dt[reasoner=="Fl0wer", if (mean(time) == 0) 1 else mean(time), .(ontology, classcount)][order(classcount)][,.(classcount,V1)]
hermitData = dt[reasoner=="Hermit", if (mean(time) == 0) 1 else mean(time), .(ontology, classcount)][order(classcount)][,.(classcount,V1)]
openlletData = dt[reasoner=="Openllet", if (mean(time) == 0) 1 else mean(time), .(ontology, classcount)][order(classcount)][,.(classcount,V1)]


if (ignore_jfact) {
	plot(flowerData, type="b", col="blue", ylim=range(flowerData[,2],hermitData[,2],openlletData[,2]), log=log_graph, ylab="time [ms]")
	
	lines(hermitData, type="b", col="red")
	lines(openlletData, type="b", col="green")
	
	legend("topleft", inset=.04, legend=c("Fl0wer", "Hermit", "Openllet"), col=c("blue", "red", "green"), lty=1)
} else {
	jfactData = dt[reasoner=="JFact", if (mean(time) == 0) 1 else mean(time), .(ontology, classcount)][order(classcount)][,.(classcount,V1)]
	
	plot(flowerData, type="b", col="blue", ylim=range(flowerData[,2],hermitData[,2],openlletData[,2],jfactData[,2]), log=log_graph, ylab="time [ms]")
	
	lines(hermitData, type="b", col="red")
	lines(openlletData, type="b", col="green")
	lines(jfactData, type="b", col="black")
	
	legend("topleft", inset=.04, legend=c("Fl0wer", "Hermit", "Openllet", "JFact"), col=c("blue", "red", "green", "black"), lty=1)
}

message("Press return to continue")
invisible(readLines("stdin", n=1))
