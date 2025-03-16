#!/bin/bash
cd "$(dirname "$0")/.." || exit 1

mvn exec:java@run-client
