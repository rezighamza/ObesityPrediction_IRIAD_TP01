# Obesity Prediction - TP 1 IRIAD

## Overview

This project implements a simple client-server architecture using Java RMI (Remote Method Invocation) to perform obesity prediction based on patient data.

## Project Structure
    src/ 
    └── main/
        └── src/
            └── java/ 
                ├── client/ 
                ├── common/ 
                ├── model/ 
                └── server/ 
            └── resources/ 
                └── gui/
                    └── view/
                └── data/
    scripts/ 
    ├── compile.sh 
    ├── run_client.sh 
    ├── run_server.sh
    └── run_gui.sh

- **client/**: Contains client-side classes (Health Center).
- **server/**: Contains server-side classes (RMI Server and Services).
- **common/**: Contains shared classes and interfaces (Patient data, exceptions, etc.).
- **model/**: Contains the AI model logic (Apriori/association rules, training, predictions).

## Getting Started

### Requirements:
* Java JDK (version 8+ recommended) ==> we used OpenJDK 17
* Maven ===> for dependency management and build automation
  * JavaFX ===> for GUI mode

### Compile:

```bash
./scripts/compile.sh

Run Server:

./scripts/run_server.sh

Run Client: ====> if you want to run in ternimal

./scripts/run_client.sh

Run GUI: ====> if you want to run in GUI mode (using JavaFX)

./scripts/run_gui.sh
```

## Notes for Collaborators

The project is configured to compile with Java 8 compatibility (via pom.xml).
It will work with higher JDK versions (11, 17, etc.), as long as Maven respects the specified compiler settings.
**Avoid changing the Java version in pom.xml unless the whole team agrees.
If you encounter issues running the server or client, make sure no other RMI registry is conflicting (use jps or check ports).**