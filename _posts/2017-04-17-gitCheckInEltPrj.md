---
layout: default
title: "Check In New ELT Project in Git"
category: ug
section: "gitCheckInEltPrj"
date: 2017-04-10 12:00:00
order: 08
---


<html>
<body>
<div class="page-header">
  <h1>How to check in New ELT Project into Git Repository</h1>
</div>

<p><span>Whenever a new ELT project needs to be checked in the Git Repository, the target Git repository needs to be cloned on your machine. Refer steps 1 through 6 from
<a href="{{ site.baseurl }}/gitCheckoutEltPrj">this</a> page for cloning Git Repository.</span></p>

<p><span>1)	Right click on the ELT project in Project Explorer &gt; Team &gt; Share Project</span></p>
<p><img alt="" src="{{ site.baseurl }}/assets/img/gitCheckInEltPrj/share_project_into_git_repo.png"></p>


<p><span>2)	It will take to Configure Git Repository window. Select desired Git Repository from drop-down and click Finish.</span></p>
<p><img alt="" src="{{ site.baseurl }}/assets/img/gitCheckInEltPrj/configure_git_repository.png"></p>

<p><span>3)	Go to Project Explorer, Right click on the project &gt; Team &gt; Commit</span></p>
<p><img alt="" src="{{ site.baseurl }}/assets/img/gitCheckInEltPrj/git_commit.png"></p>
<p><span>Note: There may be few empty folders in your ELT project. By default, those folders will not be pushed to upstream repository. If you want to keep those folders in upstream Git repository, create a .gitigonre file with following 4-line content:</span></p>

<p><span># Ignore everything in this directory</span></p>
<p><span>*</span></p>
<p><span># Except this file</span></p>
<p><span>!.gitignore</span></p>

<p><span>However, once you add something to these empty folders, you would need to remove or update the .gitignore file accordingly.</span></p>

<p><span>Similarly, if you want to not check in certain files/folders and also, want to avoid them showing up in commit window, create a .gitignore and list such files/folders in it. For example, a .gitignore file with below content will exclude folders bin, target and files .settings, .classpath from checking in:</span></p>
<p><span>/target</span></p>
<p><span>/bin</span></p>
<p><span>/.settings</span></p>
<p><span>/.classpath</span></p>
                        

<p><span>5)	Enter commit message and drag files from unstaged changes into staged changes then click Commit.</span></p>
<p><img alt="" src="{{ site.baseurl }}/assets/img/gitCheckInEltPrj/git_staging.png"></p>
<p><img alt="" src="{{ site.baseurl }}/assets/img/gitCheckInEltPrj/git_staging_staged_changes.png"></p>

<p><span>6)	Go to Project Explorer, right click on the project &gt; Team &gt; Push to upstream</span></p>
<p><img alt="" src="{{ site.baseurl }}/assets/img/gitCheckInEltPrj/git_push_upstream.png"></p>
<p><span>It should show following confirmation screen:</span></p>
<p><img alt="" src="{{ site.baseurl }}/assets/img/gitCheckInEltPrj/push_results.png"></p>


</body></html>
