/*******************************************************************************
 * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package hydrograph.ui.tooltip.tooltips;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

import hydrograph.ui.common.datastructures.tooltip.PropertyToolTipInformation;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.SWTResourceManager;
import hydrograph.ui.common.util.WordUtils;
import hydrograph.ui.datastructure.property.JoinConfigProperty;
import hydrograph.ui.datastructure.property.JoinMappingGrid;
import hydrograph.ui.datastructure.property.LookupConfigProperty;
import hydrograph.ui.datastructure.property.LookupMappingGrid;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.widgets.utility.FilterOperationClassUtility;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;
/**
 * 
 * Class for component tooltip
 * 
 * @author Bitwise
 *
 */

public class ComponentTooltip extends AbstractInformationControl implements IInformationControlExtension2 {
	
	

	private static final Logger logger = LogFactory.INSTANCE.getLogger(ComponentTooltip.class);
	private Component component; 
	private ToolBarManager toolBarManager=null;
	private Map<String,PropertyToolTipInformation> componentToolTipInformation;
	private Composite tooltipContainer;
	private static final String OPERATION_CLASS="OPERATION_CLASS";
	private static final String TRUE="true";
	private static final String FALSE="false";
	private static final String JOIN_CONFIG="join_config";
	private static final String HASH_JOIN="hash_join";
	private static final String IN0="IN0";
	private static final String IN1="IN1";
	private static final String JOIN_KEY="Join key";
	private static final String RECORD_REQUIRED="Record Required";
	private static final String DRIVER_KEY="Driver Key : ";
	private static final String LOOKUP_KEY="Lookup Key : ";
	private static final String LOOKUP_PORT="Lookup Port : ";
	private static final String LIST="list";
	private static final String OPERATION_FIELDS="operation_fields";
	private static final String SECONDARY_KEYS="secondary_keys";
	private static final String KEY_FIELDS="key_fields";
	private static final String KEY_FIELDS_SORT_COMPONENT="Key_fields_sort";
	private static final String MATCH_VALUE="match_value";
	private static final String LIST_DATA_TYPE="LIST";
	private static final String NO_OF_RECORDS="no_of_records";
	private static final String SCHEMA="schema";
	private static final String EXTERNAL_SCHEMA_PATH="External schema path : ";
	private static final String IN_PORT_COUNT="inPortCount|unusedPortCount";
	private static final String OPERATION="operation";
	private static final String JOIN_MAPPING="join_mapping";
	private static final String LOOKUP_MAPPING="hash_join_map";
	private static final String ISSUE_PROPERTY_NAME="Other Issues";
	private TransformMapping transformMapping;
	private JoinMappingGrid joinMappingGrid;
	private LookupMappingGrid lookupMappingGrid;
	/**
	 * 
	 * create tooltip with toolbar
	 * 
	 * @param parent
	 * @param toolBarManager
	 * @param propertyToolTipInformation
	 */
	public ComponentTooltip(Component component,Shell parent, ToolBarManager toolBarManager,Map<String,PropertyToolTipInformation> propertyToolTipInformation) {
		super(parent, toolBarManager);
		this.toolBarManager= getToolBarManager();
		this.component=component;
		this.componentToolTipInformation = propertyToolTipInformation;		
		logger.debug("ComponentTooltip.ComponentTooltip: Creating tooltip with toolbar: " + this.toolBarManager + " , " + this.componentToolTipInformation.toString());
		create();
	}
	
	/**
	 * 
	 * Create tooltip with status bar
	 * 
	 * @param parent - shell
	 * @param status - status message
	 * @param propertyToolTipInformation - Information to be display in tooltip
	 */
	public ComponentTooltip(Component component,Shell parent, String status,Map<String,PropertyToolTipInformation> propertyToolTipInformation) {
		super(parent, status);
		this.component=component;
		this.componentToolTipInformation = propertyToolTipInformation;
		logger.debug("ComponentTooltip.ComponentTooltip: Creating tooltip with statusbar: " + status + " , " + this.componentToolTipInformation.toString());
		create();
	}
	
	/**
	 * 
	 * Returns true if it is has toolbar, false otherwise
	 * 
	 * @return true/false
	 */
	public boolean hasToolBarManager(){
		if(toolBarManager != null ){
			logger.debug("ComponentTooltip.hasToolBarManager(): true");
			return true;
		}else{
			logger.debug("ComponentTooltip.hasToolBarManager(): false");
			return false;
		}		
	}
		
	@Override
	protected void createContent(Composite parent) {
		ScrolledComposite scrolledComposite = createScrolledComposite(parent);
		
		final Composite container = createToolTipContainer(parent,
				scrolledComposite);
				
		addToolTipContents(container);
		
		refreshScrolledComposite(scrolledComposite, container);
	}

	private void refreshScrolledComposite(ScrolledComposite scrolledComposite,
			final Composite container) {
		scrolledComposite.setContent(container);
		scrolledComposite.setMinSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * builds tooltip by adding property information on tooltip container
	 * 
	 * @param container
	 */
	private void addToolTipContents(final Composite container) {
		for(String property: componentToolTipInformation.keySet()){
			PropertyToolTipInformation propertyInfo = componentToolTipInformation.get(property);
			  if(OPERATION.equalsIgnoreCase(propertyInfo.getPropertyName()))
			    {
			    if(propertyInfo.getPropertyValue()==null)
			    {	
			    transformMapping=new TransformMapping();	
			    propertyInfo.setPropertyValue(transformMapping);
			    }
			    }
			  else  if(JOIN_MAPPING.equalsIgnoreCase(propertyInfo.getPropertyName()))
			  {
				  if(propertyInfo.getPropertyValue()==null)
				    {	
					  joinMappingGrid=new JoinMappingGrid();	
				    propertyInfo.setPropertyValue(joinMappingGrid);
				    }
				  
			  }	 
			  else  if(LOOKUP_MAPPING.equalsIgnoreCase(propertyInfo.getPropertyName()))
			  {
				  if(propertyInfo.getPropertyValue()==null)
				    {	
					  lookupMappingGrid=new LookupMappingGrid();	
				    propertyInfo.setPropertyValue(lookupMappingGrid);
				    }
				  
			  }	 
			if(propertyInfo.isShowAsTooltip()){
					addPropertyInformationToToolTip(container, propertyInfo);
			}
		}
	}

	/**
	 * add Property Information To ToolTip
	 * @param container
	 * @param propertyInfo
	 */
	private void addPropertyInformationToToolTip(final Composite container,
			PropertyToolTipInformation propertyInfo) {
		if(propertyInfo.getPropertyValue()!=null)
		{
		if(OPERATION_CLASS.equalsIgnoreCase(propertyInfo.getPropertyName())){	
			logger.debug("ComponentTooltip.addToolTipContents(): Its Opeartion class");
			addOperationClassPropertyToToolTip(container, propertyInfo);
		}
		    else if(OPERATION.equalsIgnoreCase(propertyInfo.getPropertyName()))
		    {
		     transformMapping=(TransformMapping)propertyInfo.getPropertyValue();
	         addlinkToAddPassThroughFieldsInMappingWindow(container,propertyInfo);		
		    }	
		    else if(JOIN_MAPPING.equalsIgnoreCase(propertyInfo.getPropertyName()))
		    {
		    	joinMappingGrid=(JoinMappingGrid)propertyInfo.getPropertyValue();
		    	addlinkToAddPassThroughFieldsInMappingWindow(container,propertyInfo);		
		    	
		    }
		    else if(LOOKUP_MAPPING.equalsIgnoreCase(propertyInfo.getPropertyName()))
		    {
		    	lookupMappingGrid=(LookupMappingGrid)propertyInfo.getPropertyValue();
		    	addlinkToAddPassThroughFieldsInMappingWindow(container,propertyInfo);		
		    	
		    }
			else if (JOIN_CONFIG.equalsIgnoreCase(propertyInfo.getPropertyName())) {
				int joinKeyIndex = 0, recordRequiredIndex = 0;
				if (propertyInfo.getPropertyValue() != null
						&& !((ArrayList<JoinConfigProperty>) propertyInfo.getPropertyValue()).isEmpty()) {
					for (JoinConfigProperty joinConfigProperty : ((ArrayList<JoinConfigProperty>) propertyInfo
							.getPropertyValue())) {
						addJoinKeysInTooltip(container, joinConfigProperty.getJoinKey(), joinKeyIndex);
						joinKeyIndex++;
					}
					for (JoinConfigProperty joinConfigProperty : ((ArrayList<JoinConfigProperty>) propertyInfo
							.getPropertyValue())) {
						String recordRequired = joinConfigProperty.getRecordRequired() == 0 ? TRUE
								: FALSE;
						addRecordRequiredInTooltip(container, recordRequired, recordRequiredIndex);
						recordRequiredIndex++;
					}
				}
			} else if (HASH_JOIN.equalsIgnoreCase(propertyInfo.getPropertyName())) {
				if (propertyInfo.getPropertyValue() != null) {
					LookupConfigProperty lookupConfigProperty = (LookupConfigProperty) propertyInfo.getPropertyValue();
					String lookupPort = lookupConfigProperty.isSelected() ? IN0 :IN1;
					addLookupConfigurationDetailsInTooltip(container, lookupConfigProperty.getDriverKey(),
							lookupConfigProperty.getLookupKey(), lookupPort);
				}
			} else if (SCHEMA.equalsIgnoreCase(propertyInfo.getPropertyName())) {
				if (propertyInfo.getPropertyValue() != null) {
					if(((Schema) propertyInfo.getPropertyValue()).getIsExternal())
					{
					String externalSchemaPath = ((Schema) propertyInfo.getPropertyValue()).getExternalSchemaPath();
					Label externalSchemaPathLabel = setExternalSchemaInTooltip(container, externalSchemaPath);
					showErrorMessageWhenFieldIsEmpty(externalSchemaPath, externalSchemaPathLabel,
							Messages.EXTERNAL_SCHEMA_PATH_ERROR_MESSAGE);
					}
				}
			} else {
				logger.debug("ComponentTooltip.addToolTipContents(): Its other property or with Opeartion class=null");
				addPropertyToToolTip(container, propertyInfo);
			}
		}
		
	}

	private void addlinkToAddPassThroughFieldsInMappingWindow(final Composite container,
			final PropertyToolTipInformation propertyInfo) {
		String propertyNameCapitalized = getCapitalizedName(propertyInfo);
		final Link link = new Link(container, SWT.NONE);
		String tempText= propertyNameCapitalized+" : <a>" + Constants.ADD_FIELDS_AS_PASSTHROUGH_FIELDS+ "</a>";		
		link.setText(tempText);
		if(component.getTargetConnections().isEmpty())
		link.setEnabled(false);	
		link.setBackground(container.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		addMouseHoverListenerToLink(link,container);
		addSelectionListenerToLink(propertyInfo, link);
	}

	private void addSelectionListenerToLink(final PropertyToolTipInformation propertyInfo, final Link link) {
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			if(OPERATION.equalsIgnoreCase(propertyInfo.getPropertyName()))
		     transformMapping.setAddPassThroughFields(true);
			else if(JOIN_MAPPING.equalsIgnoreCase(propertyInfo.getPropertyName()))
			{
				joinMappingGrid.setAddPassThroughFields(true);
			}
			else if(LOOKUP_MAPPING.equalsIgnoreCase(propertyInfo.getPropertyName()))
			{
				lookupMappingGrid.setAddPassThroughFields(true);
			}	
				link.setLinkForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 0,0,255));
		    }
		});
	}
	
     private void addMouseHoverListenerToLink(Link link,final Composite container) {
		link.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseHover(MouseEvent e) {
				container.setFocus();
	 		}
		});
	}

	private Label setExternalSchemaInTooltip(final Composite container, String externalSchemaPath) {
		Label externalSchemaPathLabel = new Label(container, SWT.NONE);
		externalSchemaPathLabel.setText(EXTERNAL_SCHEMA_PATH + externalSchemaPath);
		externalSchemaPathLabel.setBackground(container.getDisplay().getSystemColor(
				SWT.COLOR_INFO_BACKGROUND));
		externalSchemaPathLabel.addListener(SWT.MouseUp, getMouseClickListener(container));
		return externalSchemaPathLabel;
	}
	private void addJoinKeysInTooltip(final Composite container, String joinKey, int index)
	{
		Label joinKeyLabel = new Label(container, SWT.NONE);
		joinKeyLabel.setText(JOIN_KEY+index+":"+joinKey);
		joinKeyLabel.setBackground(container.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		joinKeyLabel.addListener(SWT.MouseUp, getMouseClickListener(container));
		showErrorMessageWhenFieldIsEmpty(joinKey,joinKeyLabel,Messages.JOIN_KEY_ERROR_MESSAGE);
	}
	
	private void addRecordRequiredInTooltip(final Composite container, String recordRequired, int index)
	{
		Label recordRequiredLabel = new Label(container, SWT.NONE);
		recordRequiredLabel.setText(RECORD_REQUIRED+index+":"+recordRequired);
		recordRequiredLabel.setBackground(container.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		recordRequiredLabel.addListener(SWT.MouseUp, getMouseClickListener(container));
	}

	private void addLookupConfigurationDetailsInTooltip(final Composite container, String driverKey, String lookupKey, String lookupPort)
	{
		Label driverKeyLabel = new Label(container, SWT.NONE);
		Label lookupKeyLabel = new Label(container, SWT.NONE);
		Label lookupPortLabel = new Label(container, SWT.NONE);
		driverKeyLabel.setText(DRIVER_KEY+driverKey);
		lookupKeyLabel.setText(LOOKUP_KEY+lookupKey);
		lookupPortLabel.setText(LOOKUP_PORT+lookupPort);
		driverKeyLabel.setBackground(container.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		driverKeyLabel.addListener(SWT.MouseUp, getMouseClickListener(container));
		lookupKeyLabel.setBackground(container.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		lookupKeyLabel.addListener(SWT.MouseUp, getMouseClickListener(container));
		lookupPortLabel.setBackground(container.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		lookupPortLabel.addListener(SWT.MouseUp, getMouseClickListener(container));
		showErrorMessageWhenFieldIsEmpty(driverKey,driverKeyLabel,Messages.DRIVER_KEY_ERROR_MESSAGE);
		showErrorMessageWhenFieldIsEmpty(lookupKey,lookupKeyLabel,Messages.LOOKUP_KEY_ERROR_MESSAGE);
	}
	private void showErrorMessageWhenFieldIsEmpty(String propertyValue,Control label,String errorMessage)
	{
		ControlDecoration decorator = WidgetUtility.addDecorator(label,errorMessage);
		if (StringUtils.isEmpty(propertyValue)) {
			decorator.show();
		} else {
			decorator.hide();
		}
	}
	/**
	 * add operation class property to tooltip container
	 * 
	 * @param container
	 * @param propertyInfo
	 */
	private void addOperationClassPropertyToToolTip(final Composite container,
			PropertyToolTipInformation propertyInfo) {
		String propertyNameCapitalized = getCapitalizedName(propertyInfo);
		
		final String filePath = propertyInfo.getPropertyValue().toString();
		logger.debug("ComponentTooltip.addOperationClassPropertyToToolTip(): Opeartion class filePath=" + filePath);
		
		Link link = createOperationClassLink(container, propertyInfo,
				propertyNameCapitalized, filePath);
				
		showErrors(propertyInfo, link);
	}

	/**
	 * Create operation class link
	 * 
	 * @param container
	 * @param propertyInfo
	 * @param propertyNameCapitalized
	 * @param filePath
	 * @return
	 */
	private Link createOperationClassLink(final Composite container,
			PropertyToolTipInformation propertyInfo,
			String propertyNameCapitalized, final String filePath) {
		Link link = new Link(container, SWT.NONE);
		String tempText= propertyNameCapitalized + " : <a>" + propertyInfo.getPropertyValue().toString() + "</a>";		
		link.setText(tempText);
		link.setBackground(container.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		
		addListenerToOpenOpeartionClassFile(filePath, link);
		
		logger.debug("ComponentTooltip.createOperationClassLink(): created opeartion class link=" + link);
		
		return link;
	}
   
	/**
	 * Add listener to open operation class file
	 * 
	 * @param filePath
	 * @param link
	 */
	private void addListenerToOpenOpeartionClassFile(final String filePath,
			Link link) {
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.debug("ComponentTooltip.widgetSelected(): Link clicked");
				super.widgetSelected(e);
				boolean flag = FilterOperationClassUtility.INSTANCE.openFileEditor(null,filePath);
				if (!flag) {
					logger.debug("ComponentTooltip.widgetSelected(): Link clicked - error - File " + filePath + " Not Found");
					WidgetUtility.errorMessage("File Not Found"); 
				} else {
					logger.debug("ComponentTooltip.widgetSelected(): Link clicked - hiding tooltip");
					setVisible(false);
				}
			}
		});
		
		logger.debug("ComponentTooltip.addListenerToOpenOpeartionClassFile(): added opeartion class link listener");
	}

	/**
	 * get Capitalized property name
	 * @param propertyInfo
	 * @return
	 */
	private String getCapitalizedName(PropertyToolTipInformation propertyInfo) {
		logger.debug("ComponentTooltip.getCapitalizedName(): propertyInfo: " + propertyInfo.toString());
		
		String propertyName = propertyInfo.getPropertyName();
		String propertyNameCapitalized = WordUtils.capitalize(propertyName.toLowerCase(), '_').replace("_", " ");
				
		logger.debug("ComponentTooltip.getCapitalizedName(): propertyNameCapitalized: " + propertyNameCapitalized);
		
		return propertyNameCapitalized;
	}

	/**
	 * Creates container for tooltip
	 * @param parent
	 * @param scrolledComposite
	 * @return
	 */
	private Composite createToolTipContainer(Composite parent,
			ScrolledComposite scrolledComposite) {
		final Composite container = addComposite(scrolledComposite);
		container.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		addToolTipContainerFocusListener(container);
		
		logger.debug("ComponentTooltip.createToolTipContainer() - created Tooltip container " + container);
		return container;
	}
	
	/**
	 * adds listener to focus container when mouse hover on container 
	 * @param container
	 */
	private void addToolTipContainerFocusListener(final Composite container) {
		container.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseHover(MouseEvent e) {
				container.setFocus();
				logger.debug("ComponentTooltip.addToolTipContainerFocusListener() - mouseHover - container focused");
			}
		});
		logger.debug("ComponentTooltip.addToolTipContainerFocusListener() - added container focus listener");
	}

	/**
	 * Add property to tooltip
	 * 
	 * @param container
	 * @param propertyInfo
	 */
	private void addPropertyToToolTip(final Composite container, PropertyToolTipInformation propertyInfo) {
		if (StringUtils.equalsIgnoreCase(ISSUE_PROPERTY_NAME, propertyInfo.getPropertyName())) {
			addTooltipWindowSplitter(container);

			Label lblTextProperty = addPropertyInTooltipWindow(container, propertyInfo);
			
			lblTextProperty.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		} else {
			Label lblTextProperty = addPropertyInTooltipWindow(container, propertyInfo);

			showErrors(propertyInfo, lblTextProperty);
		}
	}

	private Label addPropertyInTooltipWindow(final Composite container, PropertyToolTipInformation propertyInfo) {
		Label lblTextProperty = new Label(container, SWT.NONE);
		String propertyNameCapitalized = getCapitalizedName(propertyInfo);

		logger.debug("ComponentTooltip.addPropertyToToolTip() - propertyInfo=" + propertyInfo.toString());

		addText(propertyInfo, lblTextProperty, propertyNameCapitalized);

		lblTextProperty.setBackground(container.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		lblTextProperty.addListener(SWT.MouseUp, getMouseClickListener(container));
		return lblTextProperty;
	}

	private void addTooltipWindowSplitter(final Composite container) {
		Label separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	/**
	 * Add text to tooltip
	 * @param propertyInfo
	 * @param lblTextProperty
	 * @param propertyNameCapitalized
	 */
	private void addText(PropertyToolTipInformation propertyInfo,
			Label lblTextProperty, String propertyNameCapitalized) {
		if(propertyInfo.getPropertyValue() != null){
			addPropertyNameValueText(propertyInfo, lblTextProperty,
					propertyNameCapitalized);
		}else{
			addPropertyWithNoValue(lblTextProperty, propertyNameCapitalized);
		}
	}

	/**
	 * Add text with no value
	 * @param lblTextProperty
	 * @param propertyNameCapitalized
	 */
	private void addPropertyWithNoValue(Label lblTextProperty,
			String propertyNameCapitalized) {
		logger.debug("ComponentTooltip.addPropertyToToolTip() - property with no value");
		lblTextProperty.setText(propertyNameCapitalized + " : ");
	}

	/**
	 * Add text with name:value
	 * 
	 * @param propertyInfo
	 * @param lblTextProperty
	 * @param propertyNameCapitalized
	 */
	private void addPropertyNameValueText(
			PropertyToolTipInformation propertyInfo, Label lblTextProperty,
			String propertyNameCapitalized) {
		if (LIST_DATA_TYPE.equalsIgnoreCase(propertyInfo.getTooltipDataType())) {
			logger.debug("ComponentTooltip.addPropertyToToolTip() - property type is LIST=");
			String propertyValue = propertyInfo.getPropertyValue().toString()
					.substring(1, propertyInfo.getPropertyValue().toString().length() - 1);
			String formattedPropertyValue = "";
			if (SECONDARY_KEYS.equalsIgnoreCase(propertyInfo.getPropertyName())
					|| KEY_FIELDS_SORT_COMPONENT.equalsIgnoreCase(propertyInfo.getPropertyName())) {
				formattedPropertyValue = setFormattingOfSecondaryKeysAndPrimaryKeys(propertyValue,
						formattedPropertyValue);
				if(KEY_FIELDS_SORT_COMPONENT.equalsIgnoreCase(propertyInfo.getPropertyName()))
				{
					propertyNameCapitalized=WordUtils.capitalize(propertyInfo.getPropertyName().substring(0,10).toLowerCase(), '_').replace("_", " ");
				}
				lblTextProperty.setText(propertyNameCapitalized + " : " + formattedPropertyValue);
			} else {
				lblTextProperty.setText(propertyNameCapitalized + " : " + propertyValue);
			}
		} else {
			logger.debug("ComponentTooltip.addPropertyToToolTip() - property type is Text/Map=");
			if (MATCH_VALUE.equalsIgnoreCase(propertyInfo.getPropertyName())) {
				String propertyValue = propertyInfo.getPropertyValue().toString();
				String formattedPropertyValue = propertyValue.substring(propertyValue.lastIndexOf("=") + 1).replace(
						"]", "");
				if (!"null".equalsIgnoreCase(formattedPropertyValue.trim())) {
					lblTextProperty.setText(propertyNameCapitalized + " : " + formattedPropertyValue);
				} else {
					lblTextProperty.setText(propertyNameCapitalized + " : " + "First");
				}
			} else if (NO_OF_RECORDS.equalsIgnoreCase(propertyInfo.getPropertyName())) {
				String formattedPropertyName = propertyNameCapitalized.toLowerCase().substring(0, 1).toUpperCase()
						+ propertyNameCapitalized.toLowerCase().substring(1);
				lblTextProperty.setText(formattedPropertyName + " : " + propertyInfo.getPropertyValue().toString());
			}
			else if(IN_PORT_COUNT.equalsIgnoreCase(propertyInfo.getPropertyName())){
				String formattedPropertyName = propertyNameCapitalized.substring(0,11);
				lblTextProperty.setText(formattedPropertyName + " : " + propertyInfo.getPropertyValue());
			}
			else {
				lblTextProperty.setText(propertyNameCapitalized + " : " + propertyInfo.getPropertyValue().toString());
			}
		}
	}

	private String setFormattingOfSecondaryKeysAndPrimaryKeys(String propertyValue, String formattedPropertyValue) {
		String[] rowData = propertyValue.split(",");

		for (int i = 0; i < rowData.length; i++) {
			formattedPropertyValue = formattedPropertyValue + rowData[i].replace("=", "(");
			if (!"".equalsIgnoreCase(formattedPropertyValue)) {
				formattedPropertyValue = formattedPropertyValue + ")";
				if (i != rowData.length - 1) {
					formattedPropertyValue = formattedPropertyValue + ",";
				}
			}
		}
		return formattedPropertyValue;
	}

	/**
	 *  add scrolled composite to make tooltip scrollable.
	 * @param scrolledComposite
	 * @return
	 */
	private Composite addComposite(ScrolledComposite scrolledComposite) {
		final Composite container = new Composite(scrolledComposite, SWT.NONE);
		container.setLayout(new GridLayout(1, true));
		return container;
	}

	/**
	 * Create scrolled composite
	 * @param parent
	 * @return
	 */
	private ScrolledComposite createScrolledComposite(Composite parent) {
		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		logger.debug("ComponentTooltip.createScrolledComposite() - created scrolled composite " + scrolledComposite);
		
		return scrolledComposite;
	}

	/**
	 * Show errors balloon in case property is having errors.
	 * 
	 * @param propertyInfo
	 * @param lblLinkProperty
	 */
	private void showErrors(PropertyToolTipInformation propertyInfo,
			Control lblLinkProperty) {
		if(propertyInfo.getErrorMessage()!=null){
			logger.debug("ComponentTooltip.showErrors() - Showing error balloon on property " + propertyInfo.getPropertyName() + 
					", Error: " + propertyInfo.getErrorMessage());
			ControlDecoration lblDecorator = WidgetUtility.addDecorator(lblLinkProperty, propertyInfo.getErrorMessage());
			if (LIST.equalsIgnoreCase(propertyInfo.getTooltipDataType())) {
				ArrayList<String> keyFieldList;
				LinkedHashMap<String, Object> secondaryKeysList;
				if (KEY_FIELDS.equalsIgnoreCase(propertyInfo.getPropertyName())||OPERATION_FIELDS.equalsIgnoreCase(propertyInfo.getPropertyName())) {
					keyFieldList = (ArrayList<String>) propertyInfo.getPropertyValue();
					if (keyFieldList.size() == 0) {
						lblDecorator.show();
					} else {
						lblDecorator.hide();
					}
				} 
				if(SECONDARY_KEYS.equalsIgnoreCase(propertyInfo.getPropertyName())||KEY_FIELDS_SORT_COMPONENT.equalsIgnoreCase(propertyInfo.getPropertyName())) {
					secondaryKeysList = (LinkedHashMap<String, Object>) propertyInfo.getPropertyValue();
					if (secondaryKeysList.size() == 0) {
						lblDecorator.show();
					} else {
						lblDecorator.hide();
					}
				}

			} else if(propertyInfo.getPropertyValue() != null && StringUtils.equalsIgnoreCase(propertyInfo.getPropertyName().toString(), "PORT")){
				if(propertyInfo.getPropertyValue().toString().matches(Constants.PORT_VALIDATION_REGEX)|| ParameterUtil.isParameter(propertyInfo.getPropertyValue().toString())){
					lblDecorator.hide();
				}else{
					lblDecorator.show();
				}
			}else {
				if (propertyInfo.getPropertyValue() != null && (!propertyInfo.getPropertyValue().equals(""))) {
					lblDecorator.hide();
				} else {
					lblDecorator.show();
				}
			}
		}
	}

	/**
	 * 
	 * Mouse click listener to set focus on tooltip container
	 * 
	 * @param container
	 * @return
	 */
	private Listener getMouseClickListener(final Composite container) {
		return new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				logger.debug("ComponentTooltip.getMouseClickListener() - setting foucs to container");
				container.setFocus();
			}
		};
	}

	/**
	 * returns tooltip container object
	 * @return Composite - tooltip composite
	 */
	public Composite getTooltipContainer(){
		return tooltipContainer;
	}
	
	@Override
	public void setInput(Object input) {
		// Do nothing
	}

	
	@Override
	public boolean hasContents() {
		return true;
	}

	@Override
	public void setVisible(boolean visible) {
		Shell shell = getShell();
		if (shell.isVisible() == visible) {
			return;
		}

		if (!visible) {
			super.setVisible(false);
			setInformation(null);
			return;
		}
		
		super.setVisible(true);
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
	}

	@Override
	protected void handleDispose() {
		super.handleDispose();
	}

	@Override
	public void dispose() {
		super.dispose();
	}
	
	/**
	 * 
	 * Returns true if it is toolbar tooltip
	 * 
	 * @return boolean
	 */
	public boolean isToolBarToolTip(){
		if(toolBarManager==null){
			return false;
		}else{
			return true;
		}
	}
}
