#!/bin/bash
  buildNumber=`echo $TRAVIS_JOB_NUMBER | cut -d"." -f1` 
  buildDate=`date +%Y%m%d`
if [ "$TRAVIS_BRANCH" = "ui_integrator" ] ; then
  mvn clean install -DforceContextQualifier=$buildDate-$buildNumber -f hydrograph.ui/pom.xml
elif [ "$TRAVIS_BRANCH" = "engine_integrator" ] ; then
  ./gradlew build --refresh-dependencies --build-file hydrograph.engine/build.gradle --settings-file hydrograph.engine/settings.gradle
elif [ "$TRAVIS_BRANCH" = "service_integrator" ] ; then
  ./gradlew build --refresh-dependencies --build-file hydrograph.server/hydrograph.server.debug/build.gradle
elif [ "$TRAVIS_BRANCH" = "integrator" ] ; then
  mvn clean install -DforceContextQualifier=$buildDate-$buildNumber -f hydrograph.ui/pom.xml
  ./gradlew build --refresh-dependencies --build-file hydrograph.engine/build.gradle --settings-file hydrograph.engine/settings.gradle 
  ./gradlew build --refresh-dependencies --build-file hydrograph.server/hydrograph.server.debug/build.gradle
elif [ "$TRAVIS_BRANCH" = "master" ] ; then
  mvn clean install -DforceContextQualifier=$buildDate-$buildNumber -f hydrograph.ui/pom.xml
  ./gradlew build --refresh-dependencies --build-file hydrograph.engine/build.gradle --settings-file hydrograph.engine/settings.gradle 
  ./gradlew build --refresh-dependencies --build-file hydrograph.server/hydrograph.server.debug/build.gradle
fi
