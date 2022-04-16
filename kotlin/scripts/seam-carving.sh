#!/usr/bin/env bash

# Run "./gradlew farJar" first.

java -cp build/libs/algorithms-kotlin-fat-jar-1.0-SNAPSHOT.jar com.ainzzorl.algorithms.images.SeamCarving "$@"
