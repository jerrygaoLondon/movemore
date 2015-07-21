package uk.ac.oak.movemore.webapp.dao;

public class DeviceNotFoundException extends Exception {

	private static final long serialVersionUID = -4785462351073240772L;

	public DeviceNotFoundException(final String message) {
        super(message);
    }
}
