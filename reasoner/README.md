FL<sub>0</sub>wer Reasoner
==========================

i hope, that i will find some day to write a usefull readme :)

API usage
---------

First you need to install the maven project. 
```console
$ cd fl0wer
$ mvn install
```

Then you need to include the following into the pom.xml of your maven project
and hope that you don't need to use a conflicting version of the owl-api...
```xml
<dependency>
    <groupId>org.attalos.fl0wer</groupId>
    <artifactId>fl0wer</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

Afterwards you are able to use it.

```java
ConstantValues.initialise(-1, false, false, false);
FL_0_subsumption fl0wer = new FL_0_subsumption(<OWLOntology>);
Map<OWLClass, Collection<OWLClass>> class_hierchie = fl0wer.classify();
List<OWLClass> subsumers = fl0wer.calculate_subsumerset(<OWLClass>);
boolean subsumed = fl0wer.decide_subsumption(<OWLClass>, <OWLClass>);
```

Console usage
-------------

First you need to create an jar-with-dependencies
```console
$ mvn package
```

and maybe rename it for convenience.

```console
$ cd target
$ mv fl0wer-1.0-SNAPSHOT-jar-with-dependencies.jar fl0wer.jar
```

If you just run `java -jar fl0wer.jar` you will see the help message.
To classify an ontology use.
```console
$ java -jar fl0wer.jar -i <path_to_ontoloy> -C
```

To calculate the subsumerset use.
```console
$ java -jar fl0wer.jar -i <path_to_ontoloy> -S <classname>
```

To decide a subsumption relation use
```console
$ java -jar fl0wer.jar -i <path_to_ontoloy> -c1 <classname> -c2 <classname>
```
