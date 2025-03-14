#Calculator tester

How to run:
Boot up command prompt
Head to the directory of the project, will be different for everyone, and there is a pom.xml file in there, then you are in the right spot

Running using maven:
cd to the correct directory as stated above, then run the command:
mvn exec:java -Dexec.mainClass="com.example.testcasegenerator.HelloApplication"
which will allow the project to run.

Run without maven downloaded on pc:
cd to the correct directory as stated above, then run the command:
mvnw.cmd exec:java -Dexec.mainClass="com.example.testcasegenerator.HelloApplication"

If you have not changed the names of any of the files everything should work as necessary.
