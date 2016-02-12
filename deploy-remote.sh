#!/usr/bin/env bash
echo "build and deploy plugin artifacts to remote repo..."
./gradlew :frame-plugin:clean :frame-plugin:build :frame-plugin:uploadArchives --stacktrace $1
