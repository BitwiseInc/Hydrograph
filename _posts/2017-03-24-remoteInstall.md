---
layout: default
title: "Remote Install"
category: strt
section: "deploy-engine"
date: 2017-02-23 11:45:00
order: 2
---

<div class="page-header">
  <h1>Remote Install</h1>
</div>

This section will guide you through the necessary steps to set up your environment for executing Hydrograph jobs remotely. 
In order to execute jobs remotely you'll need to build the engine and server components of the Hydrograph project. 
For more information about how to build these components please see [Local Development Install](Getting-Started-UI).

1. [Pre-requisites](#pre-requisites)
2. [Deploying Hydrograph components in your execution environment(s)](#Deploying-Hydrograph)
- a. [Hydrograph Engine](#Hydrograph-Engine)
- b. [Hydrograph Service](#Hydrograph-Service)

<a name="pre-requisites"/>

## 1. Pre-requisites  
Before setting up Hydrograph on your remote execution environment you'll need to ensure that you've installed the following pre-requisites: 
  - Spark 2.0.2
    - If you have a Hadoop installation  you will need the Spark version to match your Hadoop version
    - e.g. If you are running Hadoop 2.6.0 you will require **spark-2.0.2-bin-hadoop2.6.tgz**

----------

<a name="pre-requisites"/>

## 2. Deploying Hydrograph components in your execution environment(s)  

Hydrograph is made up of three components - the developer UI, the XML custom code, and the backend execution. For a general overview of the core
Hydrograph components please see [High Level Architecture](architecture). In addition to the backend engine, there is a Hydrograph server that is 
responsible for monitoring job execution. For the purpose of deploying Hydrograph in a remote environment we'll be concerned with the server and engine
projects. 

<a name="Hydrograph-Engine"/>

### a. Hydrograph Engine  
The Hydrograph engine is responsible for reading the Hydrograph job XML and creating Spark flows. Once you build the **hydrograph.engine project**,
 you will get the dependent libraries and the following Hydrograph jars:

- hydrograph.engine.core
- hydrograph.engine.transformations
- hydrograph.engine.plugins
- hydrograph.engine.expression
- hydrograph.engine.command-line
- hydrograph.engine.spark

In order to set up the engine properly in your remote environment you'll need to use the following directory structure: 

hydrograph-engine  
&nbsp;&nbsp;&nbsp;|_ configs<br/>
&nbsp;&nbsp;&nbsp;|_ libs<br/>
&nbsp;&nbsp;&nbsp;|_ scripts<br/>

#### **configs** 
This directory contains the configuration files for the Hydrograph engine. In addition to the configurations it contains property
files from the following projects - hydrograph.engine/hydrograph.engine.spark/src/main/resources/ and hydrograph.engine/hydrograph.engine.core/src/main/resources/

#### **libs** 
This directory should contain all of the JAR files for the Hydrograph engine modules along with any additional dependencies. 

#### **scripts**
This directory contains scripts that are used to execute Hydrograph jobs. 

1. **hydrograph-env.ksh**: 
This file contains configurations required for hydrograph-exec script, in order to successfully execute jobs in a remote environment
please configure the following properties: <br/>
<table  border="1px">
<th><tr><td align="center">**Property**</td><td align="center">**Example**</td><td align="center">**Description**</td></tr></th>
<tr><td>HYDROGRAPH_HOME</td><td>HYDROGRAPH_HOME=/code/hydrograph/spark-engine</td>
<td>Update above path only if you change the base directory path from the default one.</td></tr>
<tr><td>SPARK_LIB</td><td>SPARK_LIB=/opt/spark</td><td>Location of the spark installation directory.</td></tr>
</table>
A sample script is available [here]

2. **hydrograph-exec** -<br/>
This script can be called either via the UI or the command line to trigger a Hydrograph job executions. 
A sample script is available [here]


<a name="Hydrograph-Service"/>

### b. Hydrograph Server  
Hydrograph provides a way to track the execution of jobs as well as to view the sample data generated as part of job execution. These features are available on Hydrograph UI tool,
but in order to use them we need to enable a services which keep track of job execution and provide the generated data to Hydrograph UI.

In order to set up the Hydrograph server, you'll need to create a directory named 'server' under the base directory. This directory will contain libraries, logs and configuration folders required for the server. 
Go ahead and create a group of directories with the following structure:  

hydrograph-server  
&nbsp;&nbsp;&nbsp;|_ bin<br/>
&nbsp;&nbsp;&nbsp;|_ configs<br/>
&nbsp;&nbsp;&nbsp;|_ libs<br/>
&nbsp;&nbsp;&nbsp;|_ scripts<br/>

#### libs 
Execution service, view data service, and all required dependency jars are placed under this directory. 

#### bin 
The Hydrograph service jar is placed in this directory. 

#### configs
This directory contains some of the essential configuration files needed to customize server behavior.  

**1. ServiceConfig.properties**

Configurations related to port and Kerberos are placed in this file.  
Key properties to configure:  
  - **$portId** - Port on which the service will be listening for the requests from Hydrograph UI and Engine clients.
  - **enableKerberos** - To enable or disable the Kerberos authentication
  - **tempLocationPath** - specifies the location of execution tracking status logs

**2. mail.properties**  

The Hydrograph server sends notifications to registered participants for certain events (such as server shutting down abruptly).
This configuration file contains recipients for the notifications.  
Properties to configure:  
  - **mail_smtp_host** - smpt host name or ip address
  - **mail_smtp_from** - from attribute in the e-mail
  - **mail_smtp_to** - list of participants who will receive the e-mail.

**3. log4j.properties**  

This file contains configurations for logging. Some properties that you might want to update include:  
  - **MaxFileSize** - size of logs in KB's or MB's
  - **MaxBackupIndex** - number of backup files in to be archieved
  - **File** - where to create the log file.

**4.hydrographViewDataService-exec.sh**  

Use this script to start/stop the view data service.A sample of the script can be found [here]
