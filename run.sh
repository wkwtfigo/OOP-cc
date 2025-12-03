#!/bin/bash
jasmin_path=Compilator/jasmin-2.4/jasmin.jar
gen_class_path=target/generated-classes
java -jar Compilator/jasmin-2.4/jasmin.jar $1
java -jar Compilator/jasmin-2.4/jasmin.jar $2
java Main
