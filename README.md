# MTSA #

*Modal Transition System Analyser*

[MTSA webpage](http://mtsa.dc.uba.ar/)

## What is MTSA? ##
The Modal Transition Analyser (MTSA) supports behaviour modelling, analysis and synthesis of concurrent software systems. 
Behaviour models are described using the Finite State Processes (FSP) language and behaviour constraints described in Fluent Linear Temporal Logic (FLTL). Descriptions are then be compiled into Labelled Transition Systems (LTS). 
Analysis is supported by model animation (LTS walkthroughs and graphical animation), FLTL model checking, simulation and bisimulation. 
Synthesis is supported by implementation of various discrete event controller synthesis algorithms.
MTSA also supports modelling, analysis and synthesis partial behaviour models in the form of Modal Transition Systems (MTS). 

MTSA is an evolution of the LTSA tool developed at Imperial College and is a currently a joint research effort of the Distributed Software Engineering (DSE) group at Imperial College London and the Laboratory on Foundations and Tools for Software Engineering (LaFHIS) at University of Buenos Aires.

## How to install ##
You can run MTSA using JAVA. Just download the [JAR](http://mtsa.dc.uba.ar/download/MTSA_latest.zip) and run it.

## How to build ##
```
$ cd ./maven-root/mtsa/
$ mvn clean
$ mvn install -DskipTests=true
```

## Getting started ##
To learn how to use MTSA or get involved in contributing to its code, refer to our [wiki](https://bitbucket.org/lnahabedian/mtsa/wiki/).

### LTS Examples ###
Once you clone the repository, refer to **examples** folder in: mtsa/maven-root/mtsa/src/test/java/ltsa/dist/

## Projects ##

MTSA is an experimental platform for research in Software Engineering. 
Some research projects, with associated papers, case studies, and experimental data can be found at our [main webpage](http://mtsa.dc.uba.ar/#projects).

## Publications ##

Refer to our [main webpage](http://mtsa.dc.uba.ar/#publications) for more information on publications with MTSA.