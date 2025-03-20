#!/bin/bash
cd "$(dirname "$0")/.." || { echo "Failed to navigate to project root."; exit 1; }

echo "============================="
echo " 🚀 Running the Server..."
echo "============================="

#!/bin/bash
mvn exec:java@run-server

if [ $? -eq 0 ]; then
    echo "✅ Server started successfully."
else
    echo "❌ Failed to start the server."
    exit 1
fi


