Reasoner Evaluation
===================

This evaluation was writte for the Fl0wer reasoner and compares it with Hermit, JFact and Openllet.

Building
--------

Go into the `fl0ReasonerEvaluation` folder and build the jar:

```console
$ mvn package
```

Usage
-----

I do recommend to create a new evaluation folder outside the cloned git repo and copy the three essential files there:

```
$ cd ../..
$ mkdir eval
$ cp fl0ReasonerEvaluation.git/executionScript/execute.sh eval
$ cp fl0ReasonerEvaluation.git/executionScript/plotEval.R eval
$ cp fl0ReasonerEvaluation.git/fl0ReasonerEvaluation/target/fl0ReasonerEvaluation-1.0-SNAPSHOT-jar-with-dependencies.jar eval/evaluator.jar
$ cd eval
```

If you plan to change the code rather use symlinks so you don't have to create a copy each time.

```
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

With your FL<sub>0</sub> ontologie, you can create taskfiles, which you will later use to benchmark the individual reasoners.

```console
$ java -jar evaluator.jar createClassification INPUT_DIR TASK_FILENAME
$ java -jar evaluator.jar createSubsumption INPUT_DIR TASK_FILENAME
$ java -jar evaluator.jar createSubsumerset INPUT_DIR TASK_FILENAME
```

To execute a single task run:

```console
$ java -jar evaluator.jar execute REASONER_NAME TASK_LINE
```

If you want to evaluate the whole taskfile use `execute.sh`.
This script will ask you for superuser rights to set the priority 
of the task so it won't get interupted. This lowers the noise level.

```console
$ ./execute.sh TASK_FILE evaluator.sh
```