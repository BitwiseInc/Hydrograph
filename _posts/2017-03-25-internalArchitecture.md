---
layout: default
title: "Internal Architecture"
category: hidden
section: "architecture"
date: 2015-11-20 14:44:55
order: 2
---

<div class="page-header">
<h1>Internal Architecture</h1>
</div>

<h3>User Interface</h3>

The Hydrograph UI contains 23 plug-ins
<br>

<div class="left">

<img src="{{ site.baseurl }}/assets/img/ui_architecture.png">
<br>Figure 1. Hydrograph UI Flow Diagram.

</div>
<br>

<table class="table table-hover" style="width:100%">
  <tr class = "info"><i>
    <td> Plug-in </td>
    <td> Description </td>
  </i></tr>

  <tr>
    <td> hydrograph.ui.common </td>
    <td> Contains common features/functionalities used across individual plug-ins like creating JAXB classes for components and their policies, caching feature for components; and other utility classes. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.common.datastructures </td>
    <td> Contains common POJO classes used by other plug-ins. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.common.interfaces </td>
    <td> Stores interfaces that are used across other plug-ins/modules. Intended to avoid cyclic dependencies. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.perspective </td>
    <td> Provides basic empty workbench window, which will be extended by other modules. This project is the starting point of the tool. It provides ELT perspective with required views and menu options. It also provides styling and theme for the product. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.menus </td>
    <td> Extends ELT perspective with menu contribution and it also provides key bindings (short cuts) for perspective views. This project provides specific and custom actions and their handlers for the menu options provided by ELT perspective. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.project.structure  </td>
    <td> Provides project structure wizards. Enables creating new ELT projects in the tool. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.model </td>
    <td> This plug-in has model classes for components and their categories. It also provides reusable classes for model entities used in job like component, links, ports. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.graph.figure </td>
    <td> Provides UI features like icons, colors, borders for components, ports, links. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.graph </td>
    <td> Provides Graphical Editing Framework (GEF) editor for Jobs. Also provides functionalities like UI editor, creating new jobs, Saving jobs in forms xml’s, running jobs. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.propertywindow </td>
    <td> Provides property window for each and every component placed on canvas. It also provides widget factory giving custom widgets, listeners tied up with various widget events. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.tooltip </td>
    <td> Provides tooltip functionality for components. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.engine </td>
    <td> Creates engine xml when Job is saved by user. It contains all JAXB generated java classes for engine xsd’s. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.xstream </td>
    <td> Converts complete Job (Binary Objects) into xml along with UI related information. This conversion will be done whenever a Job is saved. Plug-in will also convert UI related xml into Graph (Binary Objects) when particular Job is opened in graphical editor. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.xstream.annotations </td>
    <td> Helper plug-in for hydrograph.ui.xstream and used for knowing what UI attributes need to be filtered when Job's engine xml is created. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.aggregator </td>
    <td> Contains  aggregator pom  which lists all Eclipse components which should be built as plug-ins when maven build runs using Tycho. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.parent </td>
    <td> Houses parent pom. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.product </td>
    <td> Contains product configuration file, branding information and list of all plug-in dependencies. Also stores product resources, component configuration xmls and icons. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.validators </td>
    <td> Validates component properties. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.platformspecificplugins </td>
    <td> Manages platform specific configurations. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.parametergrid </td>
    <td> Manages job parameters. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.logging </td>
    <td> Provides log factory. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.joblogger </td>
    <td> Facilitates logging in console and files. </td>
  </tr>

  <tr>
    <td> hydrograph.ui.help </td>
    <td> Provides support. </td>
  </tr>

</table>

<h3>Engine</h3>
The Hydrograph Engine contains 4 sub-projects

<table class="table table-hover" style="width:100%">
  <tr class = "info"><i>
    <td> Project </td>
    <td> Description </td>
  </i></tr>

  <tr>
    <td> elt_cascading </td>
    <td> Heart of the execution engine. All the cascading related code of the execution engine resides in this project. Has all the component assemblies and their allied classes. </td>
  </tr>

  <tr>
    <td> elt_core </td>
    <td> Contains the XSD, JAXB and other core classes. </td>
  </tr>

  <tr>
    <td> elt_transform </td>
    <td> Contains all the classes that are exposed to end users to help in writing custom transformations in their graphs. </td>
  </tr>

  <tr>
    <td> elt_command_line </td>
    <td> Contains the main class of the engine and the API to communicate with UI. </td>
  </tr>
</table>

<br>

<div class="left">

<img src="{{ site.baseurl }}/assets/img/dependencies.png">
<br>Figure 2. Hydrograph Engine Project Dependencies.
</div>
<br>

<h4>elt_command_line Plug-ins</h4>

<table class="table table-hover" style="width:100%">
  <tr class = "info"><i>
    <td> Plug-in </td>
    <td> Description </td>
  </i></tr>

  <tr>
    <td> hydrograph.engine.command-line </td>
    <td> Contains classes which parse command line arguments. Execution of the job begins in this package. </td>
  </tr>
</table>

<h4>elt_cascading Plug-ins</h4>

<table class="table table-hover" style="width:100%">
  <tr class = "info"><i>
    <td> Plug-in </td>
    <td> Description </td>
  </i></tr>

  <tr>
    <td> hydrograph.engine.assembly.entity </td>
    <td> Contains all the entity classes for the assemblies. The entity classes are POJOs which hold the parameter information of a component. The XML attributes like id, phase, operations etc. are stored in entity classes. Each component will have its own entity class. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.assembly.entity.elements </td>
    <td> A sub-package of hydrograph.engine.assembly.entity. Contains all the classes which represent the complex and reused attributes of the components. For e.g. ‘out socket’ is a complex attribute, i.e. it consists more than one sub attribute. Hence, ‘out socket’ has its own entity element class. However, some other commonly reused attribute like ‘pathURI’ is reused across every input / output component. However, ‘pathURI’ is a simple string attribute, hence it is part of every input / output component entity.</td>
  </tr>

  <tr>
    <td> hydrograph.engine.assembly.entity.base </td>
    <td> Base classes containing common attributes of all entities.</td>
  </tr>

  <tr>
    <td> hydrograph.engine.assembly.entity.utils </td>
    <td> Contains classes which assists in smooth functioning of entities. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.assembly.generator </td>
    <td> Contains all the generators. Generators are used to fill the assembly specific entities.</td>
  </tr>

  <tr>
    <td> hydrograph.engine.assembly.generator.base </td>
    <td> Contains base classes containing common attributes of all generators</td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.assembly </td>
    <td> Contains assemblies which perform a particular functionality. Example being Input assembly which will read data from the input file.</td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.assembly.base </td>
    <td> Contains base classes containing common attributes of all assemblies. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.assembly.constants </td>
    <td> Contains all constant attributes used in assemblies. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.assembly.context </td>
    <td> Contains all the custom contexts. These contexts are filled with assembly related properties.</td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.assembly.infra </td>
    <td> Contains classes which provide component specific properties to respective assemblies.</td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.assembly.utils </td>
    <td> Contains all classes which assists all the assemblies in populating field related properties. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.avro.scheme </td>
    <td> Contains all the custom Avro scheme to read and write data in Avro files. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.coercetype </td>
    <td> Contains all the custom coerce types (datatypes in cascading). This is done especially to add support for BigDecimal datatypes.</td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.custom.handlers </td>
    <td> Contains all the handlers. Handlers provide an abstraction for various manipulation operations.</td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.debug </td>
    <td> Contains POJOs which hold the information related to a debug point. Information such as fromComponentId, fromSocketId, path, etc.</td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.filters </td>
    <td> Contains all the custom cascading filters. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.flow </td>
    <td> Contains all the custom Flow listeners used to perform certain operations based on state of a flow. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.functions </td>
    <td> Contains all the custom cascading functions such as UniqueSequenceNumberOperation which assists in assigning unique numbers to tuples. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.functions.patitioner </td>
    <td> Contains all custom partitioners. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.hive.parquet.scheme </td>
    <td> Contains all the custom parquet hive schemes. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.hive.text.scheme </td>
    <td> Contains all the custom hive schemes. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.integration </td>
    <td> Contains classes which deal with building and executing the flow. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.joiners </td>
    <td> Contains all the custom joiner which assists in achieving seamless join functionality. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.parquet.scheme </td>
    <td> Contains all the custom parquet schemes to read and write parquet files. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.scheme </td>
    <td> Contains all the custom schemes like FixedWidth, TextDelimitedAndFixedWidth scheme to read flat files. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.tap </td>
    <td> Contains all the custom cascading taps. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.tuplegenerator </td>
    <td> Contains classes which assist in tuple generation. These classes are specifically used in generateRecord component.</td>
  </tr>

  <tr>
    <td> hydrograph.engine.cascading.utilities </td>
    <td> Contains classes which assist in manipulating cascading records. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.commandtype.component </td>
    <td> Classes of all command type components like RunProgram, hplsql, etc.</td>
  </tr>

  <tr>
    <td> hydrograph.engine.debug.utils </td>
    <td> Contains all the utilities classes required by debug functionality. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.flow.utils </td>
    <td> Contains classes which assist in Flow Manipulation. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.hadoop.inputformat </td>
    <td> Contains all the custom inputformats. Inputformat sends blocks of raw data to the recordreader. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.hadoop.recordreader </td>
    <td> Contains all the custom recordreaders. Recordreaders fetches records from the input data block provided by inputformat.</td>
  </tr>

  <tr>
    <td> hydrograph.engine.hadoop.utils </td>
    <td> Contains classes which assist engine classes to obtain Hadoop properties like JobConf.</td>
  </tr>

  <tr>
    <td> hydrograph.engine.schemapropagation </td>
    <td> Contains classes which assist in propagation of schema across all components.</td>
  </tr>

  <tr>
    <td> hydrograph.engine.utilities </td>
    <td> Contains all utility classes for the assemblies. The utility classes contain reusable code to be used by all engine classes.</td>
  </tr>

</table>

<h4>elt_transform Plug-ins</h4>

<table class="table table-hover" style="width:100%">
  <tr class = "info"><i>
    <td> Plug-in </td>
    <td> Description </td>
  </i></tr>

  <tr>
    <td> hydrograph.engine.transformation.standard.functions </td>
    <td> Contains all the common functions which are used in custom classes. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.transformation.userfunctions.aggregate </td>
    <td> Contains all common aggregation functions such as count, min, max,etc. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.transformation.userfunctions.base </td>
    <td> Contains base classes which provide a skeleton for writing custom user functions. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.transformation.userfunctions.cumulate </td>
    <td> Contains all common cumulation functions. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.transformation.userfunctions.filter </td>
    <td> Contains all common filter functions. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.transformation.userfunctions.helpers </td>
    <td> Contains classes which create an abstraction over performing a particular function. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.transformation.userfunctions.normalize </td>
    <td> Contains all common normalization functions. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.transformation.userfunctions.partition </td>
    <td> Contains all common partition functions. </td>
  </tr>

  <tr>
    <td> hydrograph.engine.transformation.userfunctions.transform </td>
    <td> Contains all common transformation functions. </td>
  </tr>

</table>  
