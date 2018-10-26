Reasoner Evaluation
===================

This evaluation was written for the Fl0wer reasoner and compares it with Hermit, JFact and Openllet.

Building
--------

Go into the `fl0ReasonerEvaluation` folder and build the jar:

```console
$ mvn package
```

Usage
-----

I do recommend to create a new evaluation folder outside the cloned git repo and copy the three essential files there:

```console
$ cd ../..
$ mkdir eval
$ cp fl0ReasonerEvaluation.git/executionScript/execute.sh eval
$ cp fl0ReasonerEvaluation.git/executionScript/plotEval.R eval
$ cp fl0ReasonerEvaluation.git/fl0ReasonerEvaluation/target/fl0ReasonerEvaluation-1.0-SNAPSHOT-jar-with-dependencies.jar eval/evaluator.jar
$ cd eval
```

If you plan to change the code rather use symlinks so you don't have to create a copy each time.

```console
$ cd ../..
$ mkdir eval
$ cd eval
$ ln -s ../fl0ReasonerEvaluation.git/executionScript/execute.sh execute.sh
$ ln -s ../fl0ReasonerEvaluation.git/executionScript/plotEval.R plotEval.R
$ ln -s ../fl0ReasonerEvaluation.git/fl0ReasonerEvaluation/target/fl0ReasonerEvaluation-1.0-SNAPSHOT-jar-with-dependencies.jar evaluator.jar
```

You can use the .jar to create FL<sub>0</sub> ontologies using EL ontologies.

```console
$ java -jar evaluator.jar translate INPUT_DIR OUTPUT_DIR
```

With your FL<sub>0</sub> ontologies, you can create taskfiles, which you will later use to benchmark the individual reasoners.

```console
$ java -jar evaluator.jar createClassification INPUT_DIR TASK_FILENAME TASK_COUNT TIMEOUT
$ java -jar evaluator.jar createSubsumption INPUT_DIR TASK_FILENAME TASK_COUNT TIMEOUT
$ java -jar evaluator.jar createSubsumerset INPUT_DIR TASK_FILENAME TASK_COUNT TIMEOUT
```

To execute a single task run:

```console
$ java -jar evaluator.jar execute REASONER_NAME TASK_LINE
```

If you want to evaluate the whole taskfile use `execute.sh`.
This script will ask you for superuser rights to set the priority 
of the task so it won't get interupted. This lowers the noise level.

```console
$ ./execute.sh TASK_FILE evaluator.jar [-r]
```
The output will always be written to output.txt.
`-r` restarts the evaluation. The current content of `output.txt` will be lost. 

Plotting
--------

For plotting you need to have R install. On ubuntu use:

```console
sudo apt install r-base r-cran-data.table r-cran-getopt
```

The `execute.sh` script should have created an output.txt file.
To plot this, you can use the `plotEval.R` script.

```
./plotEval.R output.txt [OPTION]

OPTION (only one of them possible):
    -l --log        for logarithmic scale
    -i --ignore     to ignore jfact in the plotting
```

Problems & Fixes
----------------

If you run into an `java.lang.OutOfMemoryError` try using `java -Xmx2048m -jar ...` or something similar. 
If the exception occurs during the run of the execution script search for the line with `java -jar` and change it there.
