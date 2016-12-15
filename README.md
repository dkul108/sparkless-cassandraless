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

======================================================
Test  task description.

Just a plain java used with no frameworks or libraries 
(except few features used from google Guava in order not to produce simple utilities classes) 
Implementation intentianally did in a redundand eterprise like way in order to easily 
add new features  and also to  provide clear responsibilities of main classes (covered with tests). 
So we have a 
-LocalFileConnection that have access to LocalFiles 
(by analogy you can easily add Samba, Ftp, etc files procession)
-TextParser that intentionally receives InputStreamProvider in order to process files in any case 
(eg we just can receive files from JMS and start to process with no persistence on disk etc).  
TextReader is lazy iterator that hides all low level parsing and text procession. 
All data parsed per each file are stored in ParseResults. Instance of this class is also used 
in order to collect parse results from all files parsed and it able to return data at the 
any moment of time because I've used concurrent collection with volatile statement 
that give it last state visibility to all threads.
-TagCounterExecutor, that contains thread pool and produces CompeltableFutures 
in order to parse files in a parallel. It contains CountDownLatch that indicates when 
the all results collected and main thread may print all the results. 
-TcServerSocket that creates server sockets and multiple threads with client sockets 
requests accepted where parse results are added in response. It is also aware of user input 
throught Console in order to exit from loop infinite and terminate program execution.
-TagCounterConfig that is a just a geberal config for a modules specified.
-TagCounter is just a main class that launches modules provided in a logical order. 
Additionally it parses command lines arguments related to socket port and directory 
to parse files and contains default key values if nothing been provided.

All the main moddules is covered with unit tests (with 94% percents coverage) . 
I've used fest-assert testing framework that is a liitle bit more verbose and flexible 
than plain junit.
For a project build and dependencies management I've took Maven 
(but same may be achieved with gradle, SBT, etc).






