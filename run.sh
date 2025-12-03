#!/bin/bash
jasmin_path=Compilator/jasmin-2.4/jasmin.jar
gen_class_path=Compilator/target/generated-classes
java -jar $jasmin_path $gen_class_path/$1
java -jar $jasmin_path $gen_class_path/Main.j
java Main

