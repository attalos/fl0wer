FL<sub>0</sub>wer Reasoner
==========================

i hope, that i will find some day to write a usefull readme :)

API usage
---------

```java
ConstantValues.initialise(-1, false, false, false);
FL_0_subsumption fl0wer = new FL_0_subsumption(<OWLOntology>);
Map<OWLClass, Collection<OWLClass>> class_hierchie = fl0wer.classify();
List<OWLClass> subsumers = fl0wer.calculate_subsumerset(<OWLClass>);
boolean subsumed = fl0wer.decide_subsumption(<OWLClass>, <OWLClass>);
```

current Problems with:
* classification for /home/attalos/Documents/private/projects/fl0wer/ontologies/fl0_ontologies/ore2015/ore_ont_13482.owl
