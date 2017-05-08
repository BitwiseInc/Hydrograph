---
layout: default
title: "Execution Tracking"
category: feat
section: "featExecTrkg"
date: 2017-05-02 12:00:00
order: 4
---


<div class="page-header">
  <h1>Hydrograph Features - Execution Tracking</h1>
</div>

<html>
<body>
<p><span>Execution Tracking is visual representation of execution status of every component while the job is in execution.
This functionality also enables to view the number of records processed by a component on User Interface along the tracking
log of job in execution.</span></p>
<p><span class="header-2">Points:</span></p>

<ul>
	<li><span><a href="#Enable-Disable-Execution-Tracking"> Settings to enable/disable Execution Tracking</a></span></li>
	<li><span><a href="#Execution-tracking-on-Job-Canvas"> Component Status of Execution tracking on Job Canvas</a></span></li>
	<li><span><a href="#Execution-Tracking-Console"> Execution Tracking Console</a></span></li>
</ul>

<p>&nbsp;</p>

<p><span class="header-2"><a name="Enable-Disable-Execution-Tracking"></a>1) Settings to enable/disable Execution Tracking:</span></p>
<p><span>To<strong> enable/disable </strong>Execution Tracking in&nbsp;an ELT project in Hydrograph, user is provided with options in the Window menu.</span></p>
<p><span>User can navigate to Window -&gt; Preferences -&gt; Execution Tracking to set the preferences.</span></p>
<p><img src="{{ site.baseurl }}/assets/img/featExecTrkg/Execution_Tracking_1.png" alt="Description"></p>


<p><span>On Preferences Window, select filter 'Execution Tracking' as below:</span></p>
<p><img src="{{ site.baseurl }}/assets/img/featExecTrkg/Execution_Tracking_2.png" alt="Description"></p>


<p><span>By default option for Tracking is checked i.e. Execution tracking is enabled for that particular project by default.</span></p>
<p><span>In Tracking Log path, browse and provide the directory path to save tracking log.</span></p>
<p><span>By default log is saved to build directory i.e. it is &lt;Installation Path&gt;\config\logger\JobTrackingLog\</span></p>
<p><span>To disable or if user does not want to view the tracking of job on User Interface, uncheck the Tracking option in above Window.</span></p>
<p><span><strong>Apply</strong> button applies the changes made in parameter settings of Execution Tracking.</span></p>
<p><span><strong>Restore Defaults</strong> button sets the default directory path for Tracking Log.</span></p>
<p><span><strong>OK</strong> successfully saves the applied changes and closes the window.</span></p>
<p><span><strong>Cancel</strong> button closes the Preferences window without applying the changes made. Default values will be restored and window will be closed.</span></p>
<p>&nbsp;</p>


<p><span class="header-2"><a name="Execution-tracking-on-Job-Canvas"></a> 2) Component Status of Execution tracking on Job Canvas:</span></p>
<p><span>On enabling the execution tracking from Preferences, user will start seeing the execution status and record count on each component when
a job is submitted for execution</span></p>
<p><span>This is applicable for all the run modes i.e. local/remote/debug/non-debug.</span></p>


<p><span>Icons at the bottom left of each component define the current execution status of the job.</span></p>
<ol>
<li type="a"><span>Pending state - when job is executed, icon <img src="{{ site.baseurl }}/assets/img/featExecTrkg/Execution_Tracking_Pending_Status.png"> for pending
status will display on job canvas on lower-left corner of all components.<br>
This status will remain until the particular component comes in running state.</span></li>
<li type="a"><span>Running state - component in running state will be displayed with icon
<img src="{{ site.baseurl }}/assets/img/featExecTrkg/Execution_Tracking_Running_Status.png"> until the state of execution of that
component is successful or failed.</span></li>
<li type="a"><span>Successful State  - component will display with icon <img src="{{ site.baseurl }}/assets/img/featExecTrkg/Execution_Tracking_Completed_Status.png">
to show successful status.</span></li>
<li type="a"><span>Failed/Killed State  - component will be in failed status displayed with icon
<img src="{{ site.baseurl }}/assets/img/featExecTrkg/Execution_Tracking_Failed_Status.png"> if run fails/killed in between execution of job.</span></li>
</ol>


<p><span>&nbsp;&nbsp;&nbsp; In case of subjob, as soon as its first component starts, the subjob component is marked with \
running icon <img src="{{ site.baseurl }}/assets/img/featExecTrkg/Execution_Tracking_Running_Status.png">. Untill then it will
be marked with pending icon <img src="{{ site.baseurl }}/assets/img/featExecTrkg/Execution_Tracking_Pending_Status.png">
<br>&nbsp;&nbsp;&nbsp; On successful completion of its last component, the subjob component will be marked with successful icon
<img src="{{ site.baseurl }}/assets/img/featExecTrkg/Execution_Tracking_Subjob_Success_Status.png">
<br>&nbsp;&nbsp;&nbsp; If any of its component fails the subjob will be marked with abend icon
<img src="{{ site.baseurl }}/assets/img/featExecTrkg/Execution_Tracking_Failed_Status.png">.</span></p>


<p><span>At the beginning of execution, all components record count will be initialized to 0. On component completion, the exact no. of
records processed by component will be displayed </span></p>

<p><span>Execution tracking results will be cleared out automatically upon re-running a job/ closing the job canvas. Editing and saving a job would
also result in clearing of the results.</span></p>

<br>

<p><span class="header-2"><a name="Execution-Tracking-Console"></a>3) Execution Tracking Console:</span></p>
<p><span>The execution tracking console displays the report of job in execution with the status and record process details. Console will display log with record
count details on each out port for all the components from the job as well as from the sub jobs.
<br>This tracking log will be maintained at the path mentioned by user in preferences.<br>If not specified by user, log will
be saved at default installation path (<installation path="">\config\logger\JobTrackingLog\).
</installation></span></p>

<p><span>Steps to view Execution Tracking Console:</span></p>

<p><span>1) From the Job Canvas Tool Bar, click on button for Execution Tracking Console (highlighted in Blue below):</span></p>
<p><img src="{{ site.baseurl }}/assets/img/featExecTrkg/Execution_Tracking_3.png"></p>

<p><span>2) Console will display run-time report of job execution. Report format will be as follows:</span></p>

<p><span>This sample report exhibits the following basic structure of tracking report on Execution Tracking Console:</span></p>
<img src="{{ site.baseurl }}/assets/img/featExecTrkg/Execution_Tracking_4.png">



</body></html>
    
