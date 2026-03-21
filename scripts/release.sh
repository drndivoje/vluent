#!/bin/bash

set -e

# Check if version argument is provided
if [ -z "$1" ]; then
    echo "Usage: ./release.sh <version>"
    echo "Example: ./release.sh 0.0.3"
    exit 1
fi

VERSION=$1
NEXT_VERSION="${VERSION%.*}.$((${VERSION##*.} + 1))-SNAPSHOT"

echo "=== Releasing version $VERSION ==="

# Set release version
echo "Setting version to $VERSION..."
mvn versions:set -DnewVersion="$VERSION"
mvn versions:commit

# Format code
echo "Applying Spotless formatting..."
mvn spotless:apply

# Verify build
echo "Verifying build..."
mvn clean verify -Pdeployment

# Deploy to Sonatype Central
echo "Deploying to Sonatype Central..."
mvn deploy -Pdeployment

# Git operations
echo "Committing and tagging..."
git add -A
git commit -m "Release $VERSION"
git tag "v$VERSION"
git push origin master --tags

# Prepare next snapshot
echo "Preparing next development version $NEXT_VERSION..."
mvn versions:set -DnewVersion="$NEXT_VERSION"
mvn versions:commit
git add pom.xml
git commit -m "Prepare $NEXT_VERSION"
git push origin

echo "=== Release $VERSION completed successfully ==="
