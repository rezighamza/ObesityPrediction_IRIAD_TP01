#!/bin/bash
cd "$(dirname "$0")/.." || exit 1

java -cp bin client.ClientEtablissement
