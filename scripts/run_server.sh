#!/bin/bash
cd "$(dirname "$0")/.." || exit 1

#!/bin/bash
mvn exec:java@run-server


