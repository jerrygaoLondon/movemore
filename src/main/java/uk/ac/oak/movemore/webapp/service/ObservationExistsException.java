package uk.ac.oak.movemore.webapp.service;

public class ObservationExistsException extends Exception {

	private static final long serialVersionUID = 8346206228828074667L;

	public ObservationExistsException(final String message) {
		super(message);
	}
}
