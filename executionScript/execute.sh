#!/bin/bash

executeTask() {
	#prepare system
	sleep 2

	sudo chrt -f 99 java -jar $1 execute $2 $3 /home/attalos/Documents/private/projects/fl0wer/fl0ReasonerEvaluation.git/executionScript/output.txt	
	#echo "sudo chrt -f 99 java -jar $1 execute $2 $3 /home/attalos/Documents/private/projects/fl0wer/fl0ReasonerEvaluation.git/executionScript/output.txt"
}

helpMsg="usage: cmd taskFile jarFile [-r]"
continueExecution=true
currentLine=0

#correct num of param
if [ "$#" -lt 2 ]; then
	echo "Illegal number of parameters"
	echo $helpMsg
	exit;
fi

echo $1

#check options
((OPTIND+=2))
while getopts ":r" opt
do
	case $opt in
		(r) continueExecution=false; echo "reasoner,ontology,classcount,time" > output.txt;;
		(\?) echo "invalid option: -$OPTARG"; echo $helpMsg; exit;;
	esac
done

#get correct current line
if [ $continueExecution = true ]; then
	if [ ! -f .currentExecutionLine ]; then
		echo "File not found!"
		echo $currentLine > .currentExecutionLine
	else
		currentLine=$(cat .currentExecutionLine)
	fi
else
	echo $currentLine > .currentExecutionLine
fi

echo $currentLine

#execute tasks
i=0
while read -r line || [[ -n "$line" ]];
do
	#until currect line
	if [ $i -lt $currentLine ]; then
		((i++))
		continue
	fi

	#execute
	echo $line
	echo fl0wer; result_fl0wer=$(executeTask $2 fl0wer $line)	
	echo hermit; result_hermit=$(executeTask $2 hermit $line)	
	echo openllet; result_openllet=$(executeTask $2 openllet $line)	
	echo jfact; result_jfact=$(executeTask $2 jfact $line)	

	#write
	echo $result_fl0wer >> output.txt
	echo $result_hermit >> output.txt
	echo $result_openllet >> output.txt
	echo $result_jfact >> output.txt
	echo done
	((i++))
	echo $i > .currentExecutionLine
done < "$1"

