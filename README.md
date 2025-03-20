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
                ├── model/
                └── data/
    scripts/ 
    ├── compile.sh 
    ├── run_client.sh 
    └── run_server.sh

    sequence diagrams/
        └── svg  (sequence diagrams in svg format)
        (sequence diagrams in sduml format)

- **client/**: Contains client-side classes (Health Center).
- **server/**: Contains server-side classes (RMI Server and Services).
- **common/**: Contains shared classes and interfaces (Patient data, exceptions, etc.).
- **model/**: Contains the AI model logic (Apriori/association rules, training, predictions).
- **resources/model/**: Contains the Xgboost model files (model.bin) after training.
- **resources/data/**: Contains the patient data files for training and testing.
- **scripts/**: Contains bash scripts to compile and run the server and client.
- **sequence diagrams/**: Contains sequence diagrams in svg and sduml (editable) formats.
## Getting Started

### Requirements:
* Java JDK (version 8+ recommended) ==> we used OpenJDK 17
* Maven ===> for dependency management and build automation

### Compile:

```bash
./scripts/compile.sh
```
### Run Server:
```bash
./scripts/run_server.sh
```
### Run Client: 
```bash
./scripts/run_client.sh
```

## Data Preparation Phase

### Preprocessing:

#### Client-Side Validation:
Each patient record is validated to ensure all attributes are provided and correctly formatted:
* **Gender**: Must be either `"Male"` or `"Female"`.
* **Age**: Positive integer between **10 and 100**.
* **Height & Weight**: Must be positive numbers (no zero or negative values).
* **Lifestyle Factors**: Fields such as smoking habits, physical activity, and eating patterns are required, with valid categorical entries (e.g., `"Yes"`/`"No"`, numerical scales for exercise frequency).

#### Server-Side Data Accumulation:
* Patient records from clients are accumulated in memory.
* Each patient is uniquely identified using the `clientID_count` format to avoid conflicts between multiple clients.

#### Outlier Detection (Post-Collection):
First thing 
Once **≥ 2111 patient records** are gathered, the server performs a final cleaning step:
* **Height Check**: Discard entries where height < **1.2 meters** or height > **2.2 meters**.
* **Weight Check**: Discard entries where weight < **30 kg** or weight > **200 kg**.
* **Age Check**: Discard entries where age < **10** or age > **100**.
* **BMI Check**: Remove entries where BMI < **10** or BMI > **60** to eliminate anomalies.
##### Model Check:
* Upon client request, the server first checks if a previously saved model (model.bin) exists.
* If a model exists: Prediction requests can be processed immediately.
* If no model exists: Prediction requests are denied until the model is trained.
## Training Phase:
After preprocessing and cleaning:
* The server triggers model training using the **XGBoost** algorithm.
* The trained model is saved at `resources/model/model.bin` and used for subsequent prediction requests.

## Model Status & Performance

| Metric | Value |
|--------|-------|
| Accuracy | 89.5% |
| Precision | 88.2% |
| Recall | 87.7% |
| F1-Score | 87.9% |

## Notes for Collaborators

The project is configured to compile with Java 8 compatibility (via pom.xml).
It will work with higher JDK versions (11, 17, etc.), as long as Maven respects the specified compiler settings.
**Avoid changing the Java version in pom.xml unless the whole team agrees.
If you encounter issues running the server or client, make sure no other RMI registry is conflicting (use jps or check ports).**
