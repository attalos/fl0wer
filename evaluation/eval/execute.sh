#!/bin/bash

executeTask() {
	#prepare system
	sleep 2
	sudo nice --18 java -Xmx6g -jar $1 execute $2 $3
	#sudo chrt -f 99 java -Xmx6g -jar $1 execute $2 $3
	#echo "sudo chrt -f 99 java -jar $1 execute $2 $3"
}

helpMsg="usage: cmd taskFile jarFile [-r]"
continueExecution=true
currentLine=0
resultHeader=$(java -jar $2 resultHeader)

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
		(r) continueExecution=false; echo $resultHeader > output.txt;;
		(\?) echo "invalid option: -$OPTARG"; echo $helpMsg; exit;;
	esac
done

#get correct current line
if [ $continueExecution = true ]; then
	if [ ! -f .currentExecutionLine ] || [ ! -f output.txt ]; then
		echo "No file to continue found!\nStarting a new evaluation."
		echo $currentLine > .currentExecutionLine
        echo $resultHeader > output.txt
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
	echo -n "fl0wer   -   ";  result_fl0wer=$(executeTask $2 fl0wer $line);     echo $result_fl0wer
	echo -n "hermit   -   ";  result_hermit=$(executeTask $2 hermit $line);     echo $result_hermit
	echo -n "openllet -   ";  result_openllet=$(executeTask $2 openllet $line); echo $result_openllet
	echo -n "jfact    -   ";  result_jfact=$(executeTask $2 jfact $line);       echo $result_jfact

	#write
	echo $result_fl0wer >> output.txt
	echo $result_hermit >> output.txt
	echo $result_openllet >> output.txt
	echo $result_jfact >> output.txt
	echo done
	((i++))
	echo $i > .currentExecutionLine
done < "$1"
