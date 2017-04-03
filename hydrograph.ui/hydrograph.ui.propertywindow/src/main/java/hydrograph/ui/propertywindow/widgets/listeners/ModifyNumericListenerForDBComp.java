package hydrograph.ui.propertywindow.widgets.listeners;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;

public class ModifyNumericListenerForDBComp implements IELTListener{
	private ControlDecoration txtDecorator;
	
	@Override
	public int getListenerType() {
		return SWT.Modify;
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
				String string = ((Text) widgetList[0]).getText();
				if (event.type == SWT.Modify) {
					if (StringUtils.isNotBlank(string)) {
						Matcher matchs = Pattern.compile(Constants.NUMERIC_REGEX).matcher(string);
						if (matchs.matches()|| ParameterUtil.isParameter(string)) {
							txtDecorator.hide();
							((Text) widgetList[0]).setToolTipText("");
							((Text) widgetList[0]).setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
						} else {
							txtDecorator.show();
							txtDecorator.setDescriptionText(Messages.DB_NUMERIC_PARAMETERZIATION_ERROR);
							((Text) widgetList[0]).setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
						}
					} else {
						txtDecorator.show();
						txtDecorator.setDescriptionText(Messages.DB_NUMERIC_PARAMETERZIATION_ERROR);
						((Text) widgetList[0]).setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
					}
				}else{
					txtDecorator.hide();
					((Text) widgetList[0]).setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
				}
			}
		};
		return listener;
	}

}
