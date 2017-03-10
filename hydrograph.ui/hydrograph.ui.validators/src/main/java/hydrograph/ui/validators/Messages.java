package hydrograph.ui.validators;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {
	private static final String BUNDLE_NAME = "resources.messages";
	public static String PORT_VALIDATION_ERROR;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}