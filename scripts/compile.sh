#!/bin/bash
cd "$(dirname "$0")/.." || exit 1

mkdir -p bin

javac -d bin -cp src $(find src -name "*.java")

echo "Compilation terminée."
