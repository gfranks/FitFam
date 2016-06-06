#!/bin/bash
echo "****************************"
echo "*         Cleaning         *"
echo "****************************"
./gradlew clean

echo "****************************"
echo "*    Running Unit Tests    *"
echo "****************************"
./gradlew test --continue

echo "****************************"
echo "*         Cleaning         *"
echo "****************************"
./gradlew clean

echo "*******************************"
echo "* Assembling WorkoutCompanion *"
echo "*******************************"
if [[ "$TRAVIS_BRANCH" =~ ^(wc-)([0-9]+)\.([0-9]+)\.([0-9]+)$ ]] ; then
   echo "Building on a release branch. Using prod keystore."
   ./gradlew assembleRelease crashlyticsUploadDistributionRelease -PdisablePreDex
   exit $?
else
   echo "Building on a test branch. Using debug keystore."
   ./gradlew assembleDebug crashlyticsUploadDistributionDebug -PdisablePreDex
   exit $?
fi
