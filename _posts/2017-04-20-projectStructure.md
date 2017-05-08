---
layout: default
title: "Hydrograph Project Structure"
category: strt
section: "proj-struct"
date: 2017-04-20 11:45:00
order: 9
---


<div class="page-header">
  <h1>Hydrograph Project Structure</h1>
</div>


The Hydrograph ELT project contains the following folders by default -\\
![Hydrograph Project Structure]({{ site.baseurl }}/assets/img/projStruct/projectStruct.png){:height="25%" width="25%"}.

#### bin/
The bin folder will contain the compiled classes. While this directory is not required for final deployments, this is an important folder for local builds.

#### build/
The build directory contains the project jar containing the build of the custom Java transformation classes created for the project. The project-name.jar created in this folder will be passed to the Hydrograph engine during execution of the job.

#### externalSchema/
The externalSchema folder should contain any external schemas you wish to import in your input/output components. 

#### globalparam/
The globalparam folder contains the global parameters defined for any of the jobs in your project. If you wish to maintain few parameters which are common across all the jobs in the entire project, you can define them in one or more .properties file present in the globalparam folder. An example would be the Dev, QA / Prod server names which will be common across all the jobs in the project.

#### jobs/
The jobs folder will contain two files for each job you create -
.job - each job will have a .job file which is the graphical representation of the job you create. This is the file you see on the job canvas.
.xml - For each .job file, there is a .xml file that is automatically created/updated whenever you make updates to the job. This .xml file is required for the Hydrograph engine to understand the job created by the user. The Hydrograph engine parses this XML file and creates corresponding Spark flows which are submitted to the Spark master.

#### param/
The param folder contains the job specific parameters. Even if you are not using any paramters, a default job_name.properties file is created in this folder for each job you create. This is a required file and you cannot delete this file without deleting the actual job.

#### scripts/
The scripts/ folder can be used to host the wrapper scripts that are useful to invoke the Hydrograph engine with the correct parameters.

#### src/
The src/ folder contains the Java classes you create to define custom transformations in components like filters, transforms, aggregates, normalize and cumulate. 



