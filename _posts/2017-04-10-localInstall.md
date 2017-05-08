---
layout: default
title: "Local Install"
category: strt
section: "getting-started"
date: 2016-06-23 11:15:00
order: 1
---

<div class="page-header">
  <h1>Local Install</h1>
</div>

This section will guide you through the setup of the Hydrograph UI on your local machine. Once you 
have Hydrograph installed you'll be able to use all of Hydrograph's features to execute jobs locally.
This set of installation instructions is recommended for people who are just getting started with 
Hydrograph.

[1. Pre-requisites](#pre-requisites)  
[2. Build Hydrograph UI Executable](#build)  
[3. Launching Hydrograph UI](#launch-UI)  
[4. Setup Hydrograph UI](#setup-UI)  
[5. Hydrograph Configuration](#hgConfig)  

----------

<a name="pre-requisites"/>

## 1. Pre-requisites

Before setting up the Hydrograph UI on your local machine you'll need to make sure you have the following 
installed: 

- JDK 1.8.xx 64 bit ([download](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html))
    - Set the environment variable ***JAVA_HOME*** to the JDK 1.8 installation
    - If you have multiple Java installations, update the ***PATH*** variable and add ***$JAVA_HOME/bin***
- Gradle 3.0+ ([download](https://gradle.org/gradle-download/))
    - Add the Gradle installation path to the ***PATH*** variable.
- Maven 3.x ([download](http://maven.apache.org/download.cgi))
    - Add the Maven installation path to the ***PATH*** variable.

***NOTE***: If you're behind a proxy, you might need to set up some additional configuration files. 
See the [configuration](#hgConfig) section for more details. 

----------

<a name="build"/>

## 2. Build Hydrograph UI Executable

The Hydrograph code base consists of three components - the engine, the server, and the UI. Since 
the Hydrograph UI relies on both the server and engine components, you will need to build each of 
these projects before you can build the UI.  

### a. Download the source code from GitHub
To start, clone the latest source code from the master branch of 
[GitHub](https://github.com/capitalone/Hydrograph).


### b. Build the Engine project
Once you've cloned the repository, change the directory to ***hydrograph.engine*** and execute the 
following command to build the engine component: 

~~~~
gradle build
~~~~

This will build the the project and create the required JARs. You can verify that the JARs are 
are present at following relative paths:

~~~~
hydrograph.engine/hydrograph.engine.command-line/build/libs
hydrograph.engine/hydrograph.engine.core/build/libs
hydrograph.engine/hydrograph.engine.expression/build/libs
hydrograph.engine/hydrograph.engine.spark/build/libs
hydrograph.engine/hydrograph.engine.transformation/build/libs
~~~~

After verifying that the JAR files are present, navigate back to the ***hydrograph.engine*** 
directory and issue the following command to install the project in your local Maven repository: 

~~~~
gradle install
~~~~

Once you complete this step, you can check to see that the Hydrograph engine JAR files have been 
installed in the .m2 directory at the following path: 

~~~~~
{User Home Directory}/.m2/repository/hydrograph
~~~~~ 

### c. Build the Server project

The hydrograph.server project consists of two different services - the execution tracking service 
which is used to report the status of each component, and the debug service which allows developers 
to view data at each watch point. In order to build the server component of Hydrograph, you will 
need to build each of these services. 

#### i. Build Execution Tracking Service
Navigate to the directory ***hydrograph.server/hydrograph.server.execution.tracking*** and perform 
the following commands to build the execution tracking service: 

~~~~
mvn clean install
~~~~

Just as with the engine, this creates the required JARs. You can verify that JAR files have been 
created in the following directory: 

~~~~
hydrograph.server/hydrograph.server.execution.tracking/target/
~~~~

The *mvn install* command also takes care of moving the JAR files to your local Maven repository. 
You can verify that the JARs are in your local Maven repo by navigating to: 

~~~~
{User Home Directory}/.m2/repository/hydrograph
~~~~~ 

#### ii. Build Hydrograph Debug Service

To install the debug service navigate to the directory 
***hydrograph.server/hydrograph.server.debug*** and build the project using the following command: 

~~~~
gradle build
~~~~

Just as with the previous build steps, you can verify that the artifacts have been created by 
navigating to the following directory:

~~~~
hydrograph.server/hydrograph.server.debug/build/
~~~~

Once you've verified that the artifacts are present navigate back to 
***hydrograph.server/hydrograph.server.debug*** and issue the following command to install the
artifacts in  your local Maven repository: 

~~~~
gradle install
~~~~

After running *gradle install* you can check that the artifacts are in your local Maven repository: 

~~~~~
{User Home Directory}/.m2/repository/hydrograph
~~~~~ 

### d. Build the UI project
In order to successfully build the UI, you'll need to make sure that the artifacts created in the 
previous steps are in the proper location for use by the UI component of Hydrograph. Navigate to the
top level project directory - ***Hydrograph/*** - and issue the following command: 

~~~~~
gradle copyLibs
~~~~~

Navigate to ***hydrograph.ui*** and issue the following command to build and install the UI: 

~~~~
mvn clean install
~~~~

After a successful build, an operating system specific (Windows, Mac OS, etc.) Hydrograph UI will be
available at the following relative path:
***/hydrograph.ui/hydrograph.ui.product/target/products/hydrograph.ui.perspective.ID***

---

<a name="launch-UI"/>

## 3. Launching Hydrograph UI

### a. Windows users

1. Navigate to ***Hydrograph/hydrograph.ui/hydrograph.ui.product/target/products/hydrograph.ui.perspective.ID/win32/win32/x86_64***
2. Launch through hydrograph.exe

### b. Mac users

1. Navigate to ***Hydrograph/hydrograph.ui/hydrograph.ui.product/target/products/hydrograph.ui.perspective.ID/macosx/cocoa/x86_64/Hydrograph.app/***
2. Right click ***Hydrograph.app***
3. Select "Show Package Contents" 
4. Launch Hydrograph from ***Contents/MacOS/Config/hydrograph***

----------

<a name="setup-UI"/>

## 4. Setup Hydrograph UI

Once Hydrograph is launched, you will need to make sure that the Java JRE used by the UI matches the $JAVA_HOME environment 
variable that was set during the [pre-requisites](#pre-requisites) section.   

To check the current JRE in the Hydrograph UI locate the top menu bar and select Window-> Preferences -> Java -> Installed JREs. 
If the selected JRE does not match your $JAVA_HOME variable, click 'Add...'. Select 'Standard VM' and click 'Next'. Browse to 
the directory containing your JRE and click 'Open'. Click 'Finish', 'Apply', then 'Ok'. 

----------

<a name="hgConfig"/>

## 5. Configuring Hydrograph

### a. Artifact Repository Configuration
If you wish to use a different repository to download Hydrograph dependencies, you will have to update the repository URLs in the default pom.xml or build.gradle & common.gradle.
A change in these files will ensure that any new projects that you create within the UI will have the correct configuration files.

#### i. Updating repository configurations in Windows builds
- The Gradle configuration files on Windows builds can be found at ***\<Hydrograph_UI_path\>\\config\\gradle\\build***
- The Maven configuration file can be found at ***\<Hydrograph_UI_path\>\config\maven***

#### ii. Updating repository configurations in Mac builds
- The Gradle configuration files in Mac builds can be found at ***\<Hydrograph_UI_path\>/Contents/Eclipse/config/gradle/build***
- The Maven configuration files can be found at ***\<Hydrograph_UI_path\>/Contents/Eclipse/config/maven***

### b. Proxy Configuration
If you're behind a proxy, it's likely that you'll need to configure some additional files for Gradle and Maven to properly download dependencies. 

#### i. Gradle Configuration

For Gradle you'll need to create a ***gradle.properties*** file that contains information on the proxy server. Gradle will look for 
this file in the directory defined by the ***$GRADLE_USER_HOME*** environment variable. You can designate any directory as the ***GRADLE_USER_HOME***
directory, but we recommend choosing your user home directory.

Once you've set the environment variable create the file with the following properties:

```java
systemProp.http.proxyHost=www.somehost.org
systemProp.http.proxyPort=8080
systemProp.http.proxyUser=userid
systemProp.http.proxyPassword=password

systemProp.https.proxyHost=www.somehost.org
systemProp.https.proxyPort=8080
systemProp.https.proxyUser=userid
systemProp.https.proxyPassword=password
```

#### ii. Maven Configuration

For Maven you will need to update the ***settings.xml*** file with your proxy configuration.  

This file can be found in one of two locations:
- the Maven install location: ${maven.home}/conf/settings.xml
- the user install location: ${user.home}/.m2/settings.xml

Once you've located your ***settings.xml*** file you will need to update the commented-out \<proxies\> block with
information on your proxy. For example: 

```xml
<proxies>
	<proxy>
	  <id>myproxy</id>
	  <active>true</active>
	  <protocol>http</protocol>
	  <host>proxy.somewhere.com</host>
	  <port>8080</port>
	  <username>proxyuser</username>
	  <password>proxypass</password>
	</proxy>
</proxies>
```


