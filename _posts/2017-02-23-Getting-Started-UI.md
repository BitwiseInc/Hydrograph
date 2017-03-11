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

Before setting up the Hydrograph UI on your local machine you'll need to make sure you've completed the following pre-reqs: 

- JDK 1.8.xx 64 bit ([download](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html))
    - Set the environment variable **JAVA_HOME** to the JDK 1.8 installation
    - If you have multiple Java installations, update the **PATH** variable and add **$JAVA_HOME/bin**
- Gradle 3.0+ ([download](https://gradle.org/gradle-download/))
    - Add the Gradle installation path to the **PATH** variable.
- Maven 3.x ([download](http://maven.apache.org/download.cgi))
    - Add the Maven installation path to the **PATH** variable.
- Setup Hadoop environment (Required if target environment is a Hadoop installation) 
    - Windows users
        - Download/build a working copy of a 64-bit Hadoop installation
        - [How to build Hadoop on Windows](https://wiki.apache.org/hadoop/Hadoop2OnWindows)
    - Mac users
        - Hadoop can be downloaded from apache software foundation community ([download](http://hadoop.apache.org/releases.html))
    - **NOTE**: Hydrograph has been tested with Hadoop 2.6.0 and 2.7.2

---

<a name="build"/>
## 2. Build Hydrograph UI Executable

The Hydrograph code base consists of three components - the engine, the server, and the UI. Since the Hydrograph UI relies on both the server
and engine components, we'll start by building these projects. 

### a. Download the source code from Github
Please download the latest source code from the master branch of Github from [here](https://github.com/capitalone/Hydrograph). <br/>
If you download the source code as a zip file, you will require to extract the zip file on your environment.

### b. Build the Engine project
Once you've dowloaded the code, change the directory to **hydrograph.engine** and execute the following command to build the engine component: 

~~~~
gradle build
~~~~

This will build the the project and create the required jars. You can verify that the jars are present under following relative paths:

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

Once you complete this step, you will observe that the engine jars will be installed in .m2 directory at the following path:

~~~~~
{User Home Directory}/.m2/repository/Hydrograph
~~~~~ 

### c. Build the Server project

The hydrograph.server project consists of two different services - the execution tracking services which is used to report the 
status of each component, and the debug service which allows developers to view data at each watch point. 

#### i. Execution tracking service
Navigate to the directory **hydrograph.server/hydrograph.server.execution.tracking**. <br/>
You will require to update the 
and execute the following command to build the 
execution tracking component of the Hydrograph server: 

~~~~
mvn clean install
~~~~

Just as with the engine, this creates the required jars. You can verify that jars have been created in the following directory: 

~~~~
hydrograph.server\hydrograph.server.execution.tracking\target\
~~~~

Once we've verified that the artifacts have been created correctly we can install the project in our local Maven 
repo and verify that the JARs are present: 

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
hydrograph.server\ hydrograph.server.debug\build\
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
One of the pre-requisites of building the Hydrograph UI is to copy the View Data service jars produced in step a.(ii) into the
following folder of the UI project - **hydrograph.ui\hydrograph.ui.product\resources\config\service\** <br/>

Execute the below command for building the project:

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
## 4. Hydrograph Configuration
If you wish to use a different repository to download Hydrograph dependencies, you will have to update the repository URLs in the default pom.xml or build.gradle & common.gradle.
A change in these files will ensure that any new projects that you create within the UI will have the correct configuration files.

#### 4.1 Updating configurations in Windows builds -
The Gradle configuration files on Windows builds can be found at - **\<Hydrograph_UI_path\>\\config\\gradle\\build** <br/>
The Maven configuration file can be found at - **\<Hydrograph_UI_path\>\config\maven**

#### 4.2 Updating configurations in Mac builds -
The Gradle configuration files in Mac builds can be found at **\<Hydrograph_UI_path\>/Contents/Eclipse/config/gradle/build** <br/>
The Maven configuration files can be found at **\<Hydrograph_UI_path\>/Contents/Eclipse/config/maven**
