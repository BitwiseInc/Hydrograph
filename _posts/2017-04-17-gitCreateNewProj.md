---
layout: default
title: "Create Git ELT Projects"
category: ug
section: "gitCreateNewProj"
date: 2017-04-10 12:00:00
order: 07
---


<html>
<body>
<div class="page-header">
  <h1>How to create a new ELT project in Git Repository using Hydrograph</h1>
</div>

<p><span class="header-2">Create a new Git Repository for ELT projects</span></p>
<p><span>Follow your organizational process to create Git Repository for ELT projects. However, personal Git repositories can be created using <a href="https://help.github.com/articles/creating-a-new-repository/" target="_blank">Github</a>.</span></p>

<p><span class="header-2">Create a new ELT project in Git Repository using Hydrograph</span></p>
<p><span>Assumption: Git Repository already exists.</span></p>

<p><span>These steps will create a new ELT project in Git Repository. Steps 1-4 will clone the Git Repository on your local machine.</span></p>

<p><span>1) In project explorer window of Hydrograph, right click and select Import.</span></p>
<p><img alt="" src="{{ site.baseurl }}/assets/img/gitCreateNewProj/project_explorer_import_project.png"></p>

<p><span>2) From ELT project Import Wizard, select Git &gt; Projects from Git &gt;Next &gt; Select Clone URI &gt; Next. Enter details for Git source repository-</span></p>
<p><img alt="" src="{{ site.baseurl }}/assets/img/gitCreateNewProj/import_projects_from_git.png"></p>
<p><span>The authentication credentials can be stored into Hydrograph Secure Store. Also, protocol can be chosen between git, http, https, ssh etc.</span></p>

<p><span>3) After entering Git Repository details, click Next. It will display the window for Git branch selection.</span></p>

<p><span>If the Git repository is non-empty, it will list down available branches on Git. Default branch will be master. Select Git branch to clone and click Next.</span></p>
<p><img alt="" src="{{ site.baseurl }}/assets/img/gitCreateNewProj/git_branch_selection.png"></p>

<p><span>4) Click Next and configure the local storage for the Git Repository-</span></p>
<p><img alt="" src="{{ site.baseurl }}/assets/img/gitCreateNewProj/import_local_destination.png"></p>

<p><span>5) Now, click Next and select 'import using New Project Wizard' and click Finish.</span></p>
<p><img alt="" src="{{ site.baseurl }}/assets/img/gitCreateNewProj/git_select_wizard.png"></p>

<p><span>6) It will take to Hydrograph New Project wizard. Select ELT Project:</span></p>
<p><img alt="" src="{{ site.baseurl }}/assets/img/gitCreateNewProj/select_wizard_new_project.png"></p>

<p><span>7) Click Next and provide Project name and then click Finish.</span></p>
<p><img alt="" src="{{ site.baseurl }}/assets/img/gitCreateNewProj/git_repository_new_ELT_project.png"></p>

<p><span>8) These steps will create a new ELT Project in local Git Repository location.</span></p>
<p><img alt="" src="{{ site.baseurl }}/assets/img/gitCreateNewProj/project_explorer_newproj.png"></p>


</body></html>
