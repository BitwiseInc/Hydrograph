package hydrograph.ui.common.exceptions;

public class ExternalTransformException extends RuntimeException {

	private static final long serialVersionUID = 8795458449201116198L;
	private String message;
	
	public ExternalTransformException(String message) {
		this.message=message;
	}
	
	@Override
	public String getMessage() {
		return super.getMessage();
	}
	
}
