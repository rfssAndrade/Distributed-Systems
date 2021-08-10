# Application


## Authors

Group A01


### Lead developer 

93750 [Ricardo Andrade](mailto:ricardo.s.andrade@tecnico.ulisboa.pt)

### Contributors

91029 [André Dias](mailto:ist191029@tecnico.ulisboa.pt)

93696 [Daniel Lopes](mailto:daniel.quintas.lopes@tecnico.ulisboa.pt)

## About

This is a CLI (Command-Line Interface) application.


## Instructions for using Maven

To compile and run using _exec_ plugin:

```
mvn compile exec:java
```

To generate launch scripts for Windows and Linux
(the POM is configured to attach appassembler:assemble to the _install_ phase):

```
mvn install
```

To run using appassembler plugin on Linux:

```
./target/appassembler/bin/spotter arg0 arg1 arg2
```

To run using appassembler plugin on Windows:

```
target\appassembler\bin\spotter arg0 arg1 arg2
```


## To configure the Maven project in Eclipse

'File', 'Import...', 'Maven'-'Existing Maven Projects'

'Select root directory' and 'Browse' to the project base folder.

Check that the desired POM is selected and 'Finish'.


----

