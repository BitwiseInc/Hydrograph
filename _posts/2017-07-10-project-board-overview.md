---
layout: default
title: "Project Board Overview"
category: con
section: "project-board-overview"
date: 2016-06-23 11:15:00
order: 2
---

<div class="page-header">
  <h1>Project Board Overview</h1>
</div>

The Hydrograph project board allows users to track the progress of enhancements to the tool. Users 
familiar with agile methodologies and other tracking tools such as JIRA will recognize the strategy 
used for tracking enhancements to Hydrograph. The project board uses 6 columns to represent the 
stages that enhancments must "travel through" in order to be added back to the project. The purpose 
of each column is as follows:

- Proposed 
	- Enhancements requested by users will appear in this column
	- See [Propose an Enhancement](proposeAnEnhancement) for more information

- Backlog
	- Enhancements that have been reviewed, accepted, and prioritized by the Hydrograph core team
	- The enhancement with the highest priority will appear at the top of the column

- In Progress
	- Enhancements that are under active development
	- These enhancements will be assigned to at least one developer

- Ready for Review
	- Enhancements that have been completed and are waiting for a code review by the Hydrograph 
		core team
	- At this stage developers will often open a pull-request to merge the enhancement back to the 
		appropriate project branch. 
		- See [Hydrograph Branching Model](branching_model) for more information

- Accepted
	- Enhancements at this stage have been merged into the corresponding sub-project branch (i.e. 
		`engine-integrator`, `service-integrator`, or `ui-integrator`) but they haven't been merged
		back to the `master` branch. 

- Merged
	- Enhancements at this stage have been merged back to the `master` branch and have been marked as
		part of a major release. These new features are now available to all users

To see enhancements that are currently in progress check out the 
[project board](https://github.com/capitalone/Hydrograph/projects/1) on GitHub. 
