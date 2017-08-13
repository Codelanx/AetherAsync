# Usage: ./import.bat <version> -d [file]
file="Runemate.jar"
version="2.54.2.1"
mvn install:install-file -Dfile=$file -DgroupId=com.runemate -DartifactId=runemate -Dversion=$version -Dpackaging=jar -DgeneratePom=true