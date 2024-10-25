
To run:

```
./mvnw package
export "APPLICATIONINSIGHTS_CONNECTION_STRING=<Copy connection string from Application Insights Resource Overview>"
java -javaagent:target/agent/applicationinsights-agent.jar -jar target/app.jar
```
