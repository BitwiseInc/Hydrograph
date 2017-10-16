package hydrograph.ui.propertywindow.widgets.listeners;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;

public class FocusInExcelFileNameListener implements IELTListener {

	ControlDecoration txtDecorator;

	@Override
	public int getListenerType() {
		return SWT.FocusIn;
	}

	@Override
	public Listener getListener(PropertyDialogButtonBar propertyDialogButtonBar, ListenerHelper helpers,
			Widget... widgets) {
		final Widget[] widgetList = widgets;
		if (helpers != null) {
			txtDecorator = (ControlDecoration) helpers.get(HelperType.CONTROL_DECORATION);
		}
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				String charSet = ((Text) widgetList[0]).getText().trim();
				if (SWT.FocusIn == event.type) {
					if (StringUtils.isNotBlank(charSet)) {
						String extension = "";
						int i = charSet.lastIndexOf('.');
						if (i > 0) {
						    extension = charSet.substring(i);
						}
						if ((StringUtils.equals(extension, ".xls") || StringUtils.equals(extension,".xlsx")) || ParameterUtil.isParameter(charSet)) {
							txtDecorator.hide();
							((Text) widgetList[0]).setToolTipText("");
						} else {
							txtDecorator.show();
							((Text) widgetList[0]).setToolTipText(
									"Please provide proper file name with extension .xls or .xlsx for creation of an Excelsheet");
							txtDecorator.setDescriptionText(
									"Please provide proper file name with extension .xls or .xlsx for creation of an Excelsheet");
							((Text) widgetList[0])
									.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry(255, 255, 255));
						}
					} else {
						txtDecorator.show();
						((Text) widgetList[0]).setToolTipText(txtDecorator.getDescriptionText());
						((Text) widgetList[0])
								.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry(255, 255, 204));
					}

				}
			}
		};
		return listener;
	}

}
