package uk.ac.oak.movemore.webapp.dao;

public class SensorNotFoundException extends Exception {

	private static final long serialVersionUID = -7461637286632305063L;

	public SensorNotFoundException(final String message) {
        super(message);
    }
}
