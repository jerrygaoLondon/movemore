package uk.ac.oak.movemore.webapp.service.response;

import java.io.Serializable;

public class SensorServiceSuccess extends JSONResponse  implements Serializable {

	private static final long serialVersionUID = 8871092650939056483L;

	private String sensorId;

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}
	
}
