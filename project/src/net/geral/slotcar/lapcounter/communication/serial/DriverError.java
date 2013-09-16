package net.geral.slotcar.lapcounter.communication.serial;

public enum DriverError {
	NONE("No Errors"),
	NO_SUCH_PORT("Port does not exist"),
	PORT_IN_USE("Port already in use"),
	IO("Communication failed"),
	UNSUPPORTED_COMM_OPERATION("Unsupported operation"),
	UNKNOWN("Failed"),
	BOOT_TIMEOUT("Boot Timeout"),
	VERSION_TIMEOUT("Version Timeout"),
	INVALID_RESPONSE("Invalid Response"),
	WRONG_FIRMWARE("Wrong Firmware Version"),
	READ_BUFFER_OVERFLOW("Read Buffer Overflow"),
	END_OF_COMMUNICATION("End of Communication"),
	// end of list
	;
	
	public final String	title;
	
	private DriverError(String title) {
		this.title = title;
	}
}
