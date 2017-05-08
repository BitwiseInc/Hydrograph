---
layout: default
title: "Expression Editor"
category: feat
section: "featExprEditor"
date: 2017-04-28 12:00:00
order: 3
---


<div class="page-header">
  <h1>Hydrograph Features - Expression Editor</h1>
</div>

<html>
<body>

<p><span>The<b> Expression Editor</b>&nbsp;provides a full-fledged IDE editing experience that includes IntelliSense and contextual
highlighting which enables you to create expression manually or by using drag and drop functionality. Expression can be validated using
JAVA compiler after it is entered to ensure if your expression is syntactically correct.</span></p>


<p class="center"><img alt="Fig 1" src="{{ site.baseurl }}/assets/img/featExprEditor/expression_editor_window.png"></p>
<p class="center"><span>Fig 1</span></p>

<p><a name="Features"></a><span class="header-2">Features</span></p>
<p><span>Expression Editor has following features:</span></p><p><span>
</span></p><ul>
	<li><span><b>Fields pane</b>:Displays user selected input fields from available schema to be used in expression.</span></li>
	<p class="center"><img alt="Fig 1" src="{{ site.baseurl }}/assets/img/featExprEditor/field_pane.png"></p>
	<li><span><b>Category pane </b>:Category are the classes under which built in or user defined functions are defined.</span></li>
	<p class="center"><img alt="Fig 1" src="{{ site.baseurl }}/assets/img/featExprEditor/category_pane.png"></p>
	<li><span><b>Function Pane</b>:Displays the list of functions for selected category which can be drag and drop on expression pane or on evaluate window to
        create/modify the expression.
	 You can use search box in pane to search for functions that match the characters that you type.</span></li>
	<p class="center"><img alt="Fig 1" src="{{ site.baseurl }}/assets/img/featExprEditor/function_pane.png"></p>
	<p><span>You can also include user defined functions and category (classes) by registering external jars.</span></p><p><span>
	</span></p><p><span>User can register the external jars from project properties window.</span></p><p><span>
	</span></p><p><span>When user selects the Expression Jars option on property window (left pane) Expression jar's window will be displayed (on right pane )
        as shown in below Snapshot 8, through which user will able to register external jars that he / she may requires while writing expressions .</span></p><p><span>
	</span></p><p><span>Packages from the jars will displayed to user, user selected packages or class will be displayed in category pane and
        functions with respect to those classes will be displayed in function pane.</span></p><p><span>
	</span></p><p><span>While batch run, user needs to update the property file at installation path with all the packages that he/she needs registered.</span></p><p><span>
    </span></p><p class="center"><img alt="Fig 1" src="{{ site.baseurl }}/assets/img/featExprEditor/jar_added_window.png"></p>
    <li><span><b>Function Description pane</b>:Displays usage of functions as specified in the java doc description.</span></li>
    <p class="center"><img alt="Fig 1" src="{{ site.baseurl }}/assets/img/featExprEditor/function_description_pane.png"></p>
    <li><span><b>Operator list</b>:Displays the list of built-in operators that you can use to create the expression.</span></li>
    <p></p>
    <li><span><b>Expression pane</b>:To build an expression, here in this pane you can type or drag and drop the items from other pane.</span></li>
    <p class="center"><img alt="Fig 1" src="{{ site.baseurl }}/assets/img/featExprEditor/expression_pane.png"></p>
    <li><span><b>Validate expression</b>:Click validate to determine whether the syntax of expression is correct.</span></li>
    <p></p>
    <li><span><b>Evaluate Expression Window </b>:Click Evaluate to evaluate an expression. Evaluating an expression lets you immediately see the output
    so that you can verify and determine if the logic is correct without running complete graph. You can provide the constant value as a test data and can modify the
    expression based on the results displayed in output pane on this window.</span></li>
    <p class="center"><img alt="Fig 1" src="{{ site.baseurl }}/assets/img/featExprEditor/evaluate_expression_window.png"></p>
    <li><span><b>Word wrap</b>:Wrap the expression written in expression pane.</span></li>
    <p></p>
    <li><span><b>Ok button</b>:will apply and confirm the defined expressions for single particular output field and will close the editor.</span></li>
    <p></p>
    <li><span><b>Close button</b>:will not apply or save the defined expression and will close the editor by showing warning message to user if he really want to
    close editor without retaining the expression.</span></li>
 </ul>


</body></html>
    
