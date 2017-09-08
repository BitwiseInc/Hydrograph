package hydrograph.ui.propertywindow.widgets.customwidgets.operational.external;

public enum ImportExportType {

	OPERATION("Operation"),
	EXPRESSION("Expression"),
	FIELDS("Fields");
	
	
	private String value;

	private ImportExportType(String value) {
		this.value = value;
	}

	/**
	 * Returns the value for enum
	*/
	public String getValue() {
		return this.value;
	}
	
	/**
	 * Equals method 
	*/
	public boolean equals(String property) {
		return this.value.equals(property);
	}
	

	
}
