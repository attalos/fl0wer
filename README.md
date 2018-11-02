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

For the evaluation use the `eval` directory. It contains three files. The `evaluator.jar` which is a symlink to the .jar you created by using `mvn package`, an `execute.sh` which helps running the actual evaluation and `plotEval.R` which plots the results. You can use this `eval` directory as you working directory and create subdirectorys to store the task files and evaluation files.

You can use the `evaluator.jar` to create FL<sub>0</sub> ontologies using EL ontologies.
Those are obviously not semantically equivalent, but have a similar structure. (Essentially &#8707; is replaced by &#8704; and not plane FL<sub>0</sub> stuff is droped)

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
./plotEval.R -i output.txt [OPTION]

OPTION:
    -o --out            output file
    -l --log            for logarithmic scale
    -t --type           graph type (possible are `p` (points), `l` (lines), `both` (default))
    -s --symbol         symbol used for the points (e.g. `x`, `o`, `-` (default))
    -j --jfactignore    to ignore jfact in the plotting
    -h --help           show help text
```

To see all possible options try `./plotEval.R .h`.

Problems & Fixes
----------------

If you run into an `java.lang.OutOfMemoryError` try using `java -Xmx2048m -jar ...` or something similar. 
If the exception occurs during the run of the execution script search for the line with `java -jar` and change it there.
