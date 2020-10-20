#!/bin/bash

set -e
set -x

mvn -Dmaven.compiler.source=1.6 -Dmaven.compiler.target=1.6 clean test

