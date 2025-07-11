compile-
javac -cp "lib/*" -d build src/Main.java src/ui/*.java src/util/*.java

run-
java -cp "build;lib/*" src.Main
