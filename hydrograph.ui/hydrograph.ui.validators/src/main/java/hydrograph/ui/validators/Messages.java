package hydrograph.ui.validators;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {
	private static final String BUNDLE_NAME = "resources.messages";
	public static String PORT_VALIDATION_ERROR;
	public static  String OPERATION_CLASS_IS_NOT_PRESENT;
	public static  String OPERATION_CLASS_IS_BLANK;
	public static  String EXPRESSION_IS_INVALID;
	public static  String EXPRESSION_IS_BLANK;
	public static  String EXTERNAL_FILE_PATH_IS_BLANK;
	public static  String INPUT_FIELD_S_DOESN_T_MATCH_WITH_AVAILABLE_FIELDS;
	public static  String IS_MANDATORY;
	public static  String OPERATION_ID_IS_BLANK;
	public static  String EXPRESSION_ID_IS_BLANK;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}