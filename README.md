"# sparkless-cassandraless" 
just a test task

1)build (from root directory, with tests):
mvn clean package assembly:single

2)run (from ./target folder):
java -jar sparkless-cassandraless-1.0-SNAPSHOT-jar-with-dependencies.jar -p 5432 -d ../

3)launch client connetcion on windows, multiple connections are allowed
(but perform step 2 first):

cmd->telnet localhost 5432


4) exit server process:
return to folder from step 2) and type 'quit' (with no braces)


