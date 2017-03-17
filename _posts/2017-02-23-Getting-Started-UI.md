---
layout: default
title: "Local Development Install"
category: strt
section: "getting-started"
date: 2016-06-23 11:15:00
order: 1
---

<div class="page-header">
  <h1>Install Hydrograph UI for Local Development</h1>
</div>

This section will guide you through the setup of the Hydrograph UI on your local machine. Once you have Hydrograph installed on your local machine you'll
be able to use all of Hydrograph's features to execute jobs locally. This set of installation instructions is recommended for people who are just getting
started with Hydrograph.

[1. Pre-requisites](#pre-requisites)<br/>
[2. Build Hydrograph UI Executable](#build)<br/>
[3. Launching Hydrograph UI](#launch-UI)<br/>
[4. Hydrograph Configuration](#hgConfig)<br/>

----------

<a name="pre-requisites"/>
## 1. Pre-requisites

Before setting up the Hydrograph UI on your local machine you'll need to make sure you have the following items installed: 

- JDK 1.8.xx 64 bit ([download](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html))
    - Set the environment variable **JAVA_HOME** to the JDK 1.8 installation
    - If you have multiple Java installations, update the **PATH** variable and add **$JAVA_HOME/bin**
- Gradle 3.0+ ([download](https://gradle.org/gradle-download/))
    - Add the Gradle installation path to the **PATH** variable.
- Maven 3.x ([download](http://maven.apache.org/download.cgi))
    - Add the Maven installation path to the **PATH** variable.

If you're behing a proxy, you'll likely need to set up some additional configuration files. See the [Configuratoin](#hgConfig) at
the bottom of the page. 

----------

<a name="build"/>
## 2. Build Hydrograph UI Executable

The Hydrograph code base consists of three components - the engine, the server, and the UI. Since the Hydrograph UI relies on both the server
and engine components, we'll start by building these projects. 

### a. Download the source code from Github
Please download the latest source code from the master branch of [GitHub](https://github.com/capitalone/Hydrograph).If you download the 
source code as a zip file, you will require to extract the zip file on your environment.

### b. Build the Engine project
Once you've dowloaded the code, change the directory to **hydrograph.engine** and execute the following command to build the engine component: 

~~~~
gradle build
~~~~

This will build the the project and create the required JARs. You can verify that the JARs are present at following relative paths:

~~~~
hydrograph.engine/hydrograph.engine.command-line/build/libs
hydrograph.engine/hydrograph.engine.core/build/libs
hydrograph.engine/hydrograph.engine.expression/build/libs
hydrograph.engine/hydrograph.engine.spark/build/libs
hydrograph.engine/hydrograph.engine.transformation/build/libs
~~~~

After verifying that the JAR files are present, you can install the project in your local Maven repository:

~~~~
gradle install
~~~~

Once you complete this step, you can observe that the engine jars will be installed in .m2 directory at the following path:

~~~~~
{User Home Directory}/.m2/repository/Hydrograph
~~~~~ 

### c. Build the Server project

The hydrograph.server project consists of two different services - the execution tracking service which is used to report the 
status of each component, and the debug service which allows developers to view data at each watch point. 

#### i. Execution tracking service
Navigate to the directory **hydrograph.server/hydrograph.server.execution.tracking** and perform the following commands to build
the execution tracking service: 

~~~~
mvn clean install
~~~~

Just as with the engine, this creates the required JARs. You can verify that JAR files have been created in the following directory: 

~~~~
hydrograph.server/hydrograph.server.execution.tracking/target/
~~~~

The `mvn install` command also takes care of moving the JAR files to our local Maven repository. We can verify that the JARs are in our
local Maven repo by navigating to: 

~~~~
{User Home Directory}/.m2/repository/hydrograph
~~~~~ 

#### ii. Hydrograph debug service

To install the debug service navigate to the directory **hydrograph.server/hydrograph.server.debug** and build the 
project using the following command: 

~~~~
gradle build
~~~~

Just as with our previous build steps, we can verify that the artifacts have been created by navigating to the following directory

~~~~
hydrograph.server/hydrograph.server.debug/build/
~~~~

Once we've verified that the artifacts are present we can install them in our local Maven repo. 

~~~~
gradle install
~~~~

After installing the jars will be in the local Maven repository under the Hydrograph project. 
~~~~~
{Uswr Home Directory}/.m2/repository/Hydrograph
~~~~~ 

### d. Build the UI project
In order to successfully build the UI, we'll need to make sure that the artifacts created in the previous steps are in the proper
location for use by the UI. Navigate to the top level project directory **Hydrograph/** and issue the following command: 

~~~~~
gradle copyLibs
~~~~~

Now we can go ahead and build the UI by issuing the command below: 

~~~~
mvn clean install
~~~~

After a successful build, an operating System (Windows, Mac OS, etc.) specific Hydrograph IDE will be available under below relative path:
**/hydrograph.ui/hydrograph.ui.product/target/products/hydrograph.ui.perspective.ID**

---
<a name="launch-UI"/>

## 3. Launching Hydrograph UI

#### 3.1a. Windows users

1. Navigate to **/hydrograph.ui/hydrograph.ui.product/target/products/hydrograph.ui.perspective.ID** 
2. Launch through hydrograph.exe

#### 3.1b. Mac users

1. Navigate to **/hydrograph.ui/hydrograph.ui.product/target/products/hydrograph.ui.perspective.ID** 
2. Right click Hydrograph_build_date_Mac file that was just extracted
3. Select "Show Package Contents"
4. Launch Hydrograph from Contents/MacOS/Config/Hydrograph

#### 3.2. Validate Java

Once Hydrograph is launched, verify that the selected Java JRE matches the JAVA_HOME environment variable. 
From the top menu bar select Window-> Preference -> Java -> Installed JREs

---

<a name="hgConfig"/>
## 4. Configuration
If you wish to use a different repository to download Hydrograph dependencies, you will have to update the repository URLs in the default pom.xml or build.gradle & common.gradle.
A change in these files will ensure that any new projects that you create within the UI will have the correct configuration files.

#### 4.1 Updating configurations in Windows builds -
The Gradle configuration files on Windows builds can be found at - **\<Hydrograph_UI_path\>\\config\\gradle\\build** <br/>
The Maven configuration file can be found at - **\<Hydrograph_UI_path\>\config\maven**

#### 4.2 Updating configurations in Mac builds -
The Gradle configuration files in Mac builds can be found at **\<Hydrograph_UI_path\>/Contents/Eclipse/config/gradle/build** <br/>
The Maven configuration files can be found at **\<Hydrograph_UI_path\>/Contents/Eclipse/config/maven**

#### 4.3 Proxy Configuration
If your behind a proxy, it's possible that you'll need to configure some additional files for Gradle and Maven to
properly download dependencies. 


For Gradle you'll need to create a `gradle.properties` file that contains information on the proxy server. Gradle will look for 
this file in the directory defined by the **$GRADLE_USER_HOME** environment variable.  

On Linux and Mac we suggest setting the **$GRADLE_USER_HOME** in  your ~/.profile as the user's home directory.  

On Windows TODO

Once you've set the environment variable and created the file be sure to configure the following properties in the file

```java
systemProp.http.proxyHost=<proxyHostname>
systemProp.http.proxyPort=<proxyPortNum>
systemProp.https.proxyHost=<proxyHostname>
systemProp.https.proxyPort=<proxyPortNum>
```




