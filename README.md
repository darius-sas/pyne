# pyne
Temporal dependency graph extraction from Java Git repositories

## Building
Requirements:
 - Java jdk 11+
 - Maven
 
building the project:
 - Navigate to pyne directory in a command line prompt.
 - Run the following command:
```
mvn install
```

## Executing
Requirements:
 - Java jdk 11+
 - Have a build version of `pyne-cli-1.0-SNAPSHOT-jar-with-dependencies.jar`. This can be found after the build step in `pyne-cli/target`.
 
Running the CLI
  - Open a command line prompt.
  - Run the following command:
```
java -jar <path-to-jar>/pyne-cli-1.0-SNAPSHOT-jar-with-dependencies.jar --help
```

When you run that last command you will get a list of all available options. 
You can then remove `--help` and replace it with the URI of the project you want to open together with the options you need.
