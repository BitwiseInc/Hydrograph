---
layout: default
title: "View Data"
category: feat
section: "featViewDta"
date: 2017-04-28 12:00:00
order: 1
---


<div class="page-header">
  <h1>Hydrograph Features - View Data</h1>
</div>

<html>
<body>
<p>
        <span class="header-1">View Data</span>
</p>
<p>
        <span><b>View Data</b>&nbsp; functionality provides facility to
                view and edit data records in between 2 flows on job canvas.</span>
</p><p>
        <span class="header-2">To view/Watch records:</span>
</p>
<ul>
        <li><p>
                        <span>Before execution of job, add watch point in between the
                                flow that represents the data you want to view.</span>
                </p></li>
        <li><p>
                        <span>By right click on the data flow to be viewed, select
                                Watch Point and Add Watch Point to the flow.</span>
                </p></li>
        <li><p>
                        <span>Similarly user can remove the Watch Point added if user
                                does not want to view the intermediate data, by selecting Watch
                                Point -&gt; Remove Watch Point as shown in below snapshot.</span>
                </p></li>

</ul>
<p>
        <img alt="" src="{{ site.baseurl }}/assets/img/featViewDta/addwatchpoint.png" width="100%">
</p>
<p>
        <img alt="" src="{{ site.baseurl }}/assets/img/featViewDta/watchpointadded.png">
</p>

<p class="center"><span>In port of the flow (right-end of flow) colored in red
                        square represent the Watch point added on flow. (Highlighted in
                        yellow)</span></p>

<ul>
        <li><p>
                        <span>After the execution of job, user can watch records
                                .Watch Records dialog displays the records in between the flows
                                requested by user.</span>
                </p></li>
        <li><p>
                        <a name="differentViews"><span>Depending on the display type user selects, the data
                                will be viewed on Watch record Window. The data is visible in <b>grid,</b>
                                <b>formatted,</b> and <b>unformatted </b>text as shown below:
                        </span></a>
                </p></li>
</ul>
<p><img alt="" src="{{ site.baseurl }}/assets/img/featViewDta/viewrecordsdifferentviews.png" width="100%"></p>

<p class="center"><span>By default data is displayed in Grid view in Data Viewer
                Window.</span></p>
<ul>
        <li><p>
                        <span>View data also provides the preferences where user can
                                also change the number of records that needs to be displayed per
                                page, delimiter for the data that needs to display in unformatted
                                text, quote characters or max size of file that needs to be
                                downloaded or viewed on watch record window.</span>
                </p></li>
        <li><p>
                        <span>With the Pagination feature at bottom left corner of
                                the Data Viewer window user can navigate through the next or
                                previous pages</span>
                </p></li>
</ul>

<img alt="" src="{{ site.baseurl }}/assets/img/featViewDta/pagination.png">


<ul>
        <li><p>
                        <span>User can also directly jump to the required page using
                                bottom right corner panel on the view data window.</span>
                </p></li>
</ul>

<img alt="" src="{{ site.baseurl }}/assets/img/featViewDta/jumptopage.png">
<ul>
        <li><p>
                        <span>User can also view data records for prior run. Please refer <a href="">View data history</a> for details.
                        </span>
                </p></li>
</ul>


</body></html>
    
