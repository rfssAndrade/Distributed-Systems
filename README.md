# A01-Bicloin

Distributed Systems 2020-2021, 2nd semester project

**Grade:** 18,63 / 20

## Authors

**Group A01**

91029 [André Dias](mailto:ist191029@tecnico.ulisboa.pt)

93696 [Daniel Lopes](mailto:daniel.quintas.lopes@tecnico.ulisboa.pt)

93750 [Ricardo Andrade](mailto:ricardo.s.andrade@tecnico.ulisboa.pt)

## Getting Started

The overall system is composed of multiple modules.

See the project statement for a full description of the domain and the system.

### Prerequisites

Java Developer Kit 11 is required running on Linux, Windows or Mac.
Maven 3 is also required.

To confirm that you have them installed, open a terminal and type:

```
javac -version

mvn -version
```

### Installing

To compile and install all modules:

```
mvn clean install -DskipTests
```

The integration tests are skipped because they require theservers to be running.


## Built With

* [Maven](https://maven.apache.org/) - Build Tool and Dependency Management
* [gRPC](https://grpc.io/) - RPC framework


## Versioning

We use [SemVer](http://semver.org/) for versioning. 
