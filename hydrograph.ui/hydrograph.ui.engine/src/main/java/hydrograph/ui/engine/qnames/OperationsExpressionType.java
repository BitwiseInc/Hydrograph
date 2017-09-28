package hydrograph.ui.engine.qnames;

import javax.xml.namespace.QName;

/**
 * OperationsExpressionType Enum represent the "includeExternalExpression,includeExternalOperation
 * operation,expression  values.
 * 
 * @author Bitwise
 *
 */
public enum OperationsExpressionType {

	INCLUDE_EXTERNAL_EXPRESSION("includeExternalExpression"), 
	INCLUDE_EXTERNAL_OPERATION("includeExternalOperation"), 
	OPERATION("operation"), 
	EXPRESSION("expression");

	private final String value;

	private OperationsExpressionType(String v) {
		value = v;
	}

	/**
	 * Return the value of enum type.
	 * @return
	 */
	public String value() {
		return value;
	}

	/**
	 * Return the QName instance.
	 * @return QName
	 */
	public QName getQName() {
		return new QName(this.value);
	}
}