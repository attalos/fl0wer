Evaluation Method
=================

* wanted to messure runtime of
  * subsumption
  * subsumerset calculation (described before?)
  * classification

When simply messuring the runtime by envoking the reasoner from the command line, one also messures 
the time used for parsing the ontology (which is quite long for java-reasoners using the owl-api) and also
some other setup time, unrelated to the reasoning task.

-->
Decided for setup, where only the runtime of the reasoning task is measured (and not the time of loading the ontology).
To achive this, we used java reasoners implementing the `OWLReasoner` class. This provides us the possibility of measuring the 
runtime of:
    * classification: `reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY)`
    * subsumerset:    `reasoner.getSuperClasses(classOwl)`
    * subsumption:    `reasoner.isSatisfiable(subclass and not superclass)`
fl0wer does not implement the OWLReasoner interface (and can't since no such thing as FL0-only reasoner exist in this interface), but
it provides three comparable functions:
    * classification: `fl0wer.classify()`
    * subsumerset:    `fl0wer.calculate_subsumerset(classOwl)`
    * subsumption:    `fl0wer.decide_subsumption(subClassOwl, superClassOwl)`

Used ontologies
---------------

Since there are (as far as we know) no FL0 ontologies arround, we had to find some way to generate them.
...
