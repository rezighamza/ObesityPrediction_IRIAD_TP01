#!/bin/bash
cd "$(dirname "$0")/.." || exit 1

mkdir -p bin

mvn clean compile

echo "Compilation terminée."
