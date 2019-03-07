# fitnesse-parallel
This is for running the fitnesse tests in parallel and generating the report.


To execute fitnesse test in parallel, you can download the jars from target and run the following command.
```
java -jar fitnesse-parallel-1.1-SNAPSHOT.jar http://<fitnesseinstanceIpaddress>:8888/FitNesse.${Suite}.RestApis.${ClientType}.${Environment} min max
 ```
 
FitNesse.${Suite}.RestApis.${ClientType}.${Environment} :  is which suite to run

min and max value is to be decided based on the hardware it is going to run. if you have 16 core machine, you can setup high number.

```
ex: java -jar fitnesse-parallel-1.1-SNAPSHOT.jar http://${IP_ADDRESS}:8888/FitNesse.${Suite}.RestApis.${ClientType}.${Environment} 50 60
```
This is to generate the testresults xml:

mvn exec:java -Dexec.mainClass="XMLConcat" -Dexec.args="${FitnesseRoot}/files/testResults ${Environment}"

This will genearete a nice html report.

mvn -PreportMerge xml:transform -DFitNesseRoot=${FitnesseRoot} 
