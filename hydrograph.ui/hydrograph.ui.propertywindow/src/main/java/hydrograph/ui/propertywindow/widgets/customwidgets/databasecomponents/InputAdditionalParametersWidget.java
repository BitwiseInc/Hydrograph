package hydrograph.ui.propertywindow.widgets.customwidgets.databasecomponents;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.factory.ListenerFactory;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.utils.Utils;
import hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.RuntimeConfig;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;

public class InputAdditionalParametersWidget extends AbstractWidget {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(InputAdditionalParametersWidget.class);
	private String propertyName;
	private Map<String, Object> initialMap;
	private RuntimeConfig runtimeConfig;
	private Shell shell;
	private ArrayList<AbstractWidget> widgets;
	private List<String> schemaFields;
	private LinkedHashMap<String, Object> tempPropertyMap;
	private Cursor cursor;

	/**
	 * Instantiates a new AdditionalParametersWidget widget.
	 * 
	 * @param componentConfigProp
	 *            the component configuration property
	 * @param componentMiscProps
	 *            the component miscellaneous properties
	 * @param propDialogButtonBar
	 *            the property dialog button bar
	 */
	public InputAdditionalParametersWidget(ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps, PropertyDialogButtonBar propDialogButtonBar) {
		super(componentConfigProp, componentMiscProps, propDialogButtonBar);

		this.propertyName = componentConfigProp.getPropertyName();
		if (componentConfigProp.getPropertyValue() != null
				&& (Map.class).isAssignableFrom(componentConfigProp.getPropertyValue().getClass())) {
			this.initialMap = (Map<String, Object>) componentConfigProp.getPropertyValue();
		}
		if (initialMap == null) {
			this.initialMap = new LinkedHashMap<String, Object>();
		}
	}

	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget subGroup) {

		ELTDefaultSubgroupComposite additionalParameterComposite = new ELTDefaultSubgroupComposite(
				subGroup.getContainerControl());
		additionalParameterComposite.createContainerWidget();
		this.shell = additionalParameterComposite.getContainerControl().getShell();
		this.runtimeConfig = (RuntimeConfig) widgetConfig;
		
		Utils.INSTANCE.loadProperties();
		cursor = subGroup.getContainerControl().getDisplay().getSystemCursor(SWT.CURSOR_HAND);
		
		ELTDefaultLable additionalParameterLabel = new ELTDefaultLable(runtimeConfig.getLabel());
		additionalParameterComposite.attachWidget(additionalParameterLabel);
		setPropertyHelpWidget((Control) additionalParameterLabel.getSWTWidgetControl());

		ELTDefaultButton additionalParameterButton = new ELTDefaultButton(Constants.EDIT);

		additionalParameterComposite.attachWidget(additionalParameterButton);

		try {
			additionalParameterButton.attachListener(ListenerFactory.Listners.RUNTIME_BUTTON_CLICK.getListener(),
					propertyDialogButtonBar, new ListenerHelper(this.getClass().getName(), this),
					additionalParameterButton.getSWTWidgetControl());

		} catch (Exception exception) {
			logger.error("Error occured while attaching listener to Runtime Properties window", exception);
		}

	}

	@Override
	public LinkedHashMap<String, Object> getProperties() {
		tempPropertyMap = new LinkedHashMap<>();
		tempPropertyMap.put(this.propertyName, this.initialMap);
		setToolTipErrorMessage();
		return tempPropertyMap;
	}

	@Override
	public boolean isWidgetValid() {
		return validateAgainstValidationRule(initialMap);
	}

	@Override
	public void addModifyListener(Property property, ArrayList<AbstractWidget> widgetList) {
		widgets = widgetList;
	}

	public void newWindowLauncher() {

		this.schemaFields = getPropagatedSchema();
		initialMap = new LinkedHashMap<>(initialMap);
		InputAdditionalParametersDialog inputAdditionalParametersDialog = new InputAdditionalParametersDialog(shell,
				runtimeConfig.getWindowLabel(), propertyDialogButtonBar, schemaFields, initialMap,cursor);
		inputAdditionalParametersDialog.open();
		initialMap = inputAdditionalParametersDialog.getAdditionalParameterDetails();

		showHideErrorSymbol(widgets);
		propertyDialogButtonBar.enableApplyButton(true);
	}

	/**
	 * Set the tool tip error message
	 */
	protected void setToolTipErrorMessage() {
		String toolTipErrorMessage = null;

		setToolTipMessage(toolTipErrorMessage);
	}

	/**
	 * Propogates the schema from GridRow
	 */
	protected List<String> getPropagatedSchema() {
		List<String> list = new ArrayList<String>();
		Schema schema = (Schema) getComponent().getProperties().get(Constants.SCHEMA_PROPERTY_NAME);
		if (schema != null && schema.getGridRow() != null) {
			List<GridRow> gridRows = schema.getGridRow();
			if (gridRows != null) {
				for (GridRow gridRow : gridRows) {
					if (StringUtils.equalsIgnoreCase(gridRow.getDataTypeValue(), "java.lang.Integer")
							|| StringUtils.equalsIgnoreCase(gridRow.getDataTypeValue(), "java.lang.Long")) {
						list.add(gridRow.getFieldName());
					}
				}
			}
		}
		return list;
	}

}
