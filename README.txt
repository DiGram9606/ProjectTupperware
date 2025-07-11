compile -
javac -d out -cp "lib/*:." src/ui/*.java src/util/*.java

run  -
java -cp "lib/*:out" src.ui.DesmosCloneApp

note- use ; instead of : for Windows

(optional)- Always use rm -rf out/ (or Windows equivalent) if you see unexpected behavior. This ensures you start with a clean build.
