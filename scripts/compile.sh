#!/bin/bash
cd "$(dirname "$0")/.." || { echo "Failed to navigate to project root."; exit 1; }

echo "============================="
echo "🛠️  Cleaning and Compiling..."
echo "============================="

mkdir -p bin
mvn clean compile -Dfile.encoding=UTF-8

if [ $? -eq 0 ]; then
    echo "✅ Compilation terminée avec succès."
else
    echo "❌ Échec de la compilation."
    exit 1
fi
