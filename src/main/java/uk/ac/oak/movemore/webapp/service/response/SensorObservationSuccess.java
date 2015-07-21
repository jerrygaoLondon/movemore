package uk.ac.oak.movemore.webapp.service.response;

import java.io.Serializable;

public class SensorObservationSuccess extends JSONResponse implements Serializable{
	
	private static final long serialVersionUID = 8765507711676888901L;
	
	private String obsvId;
		
	public String getObsvId() {
		return obsvId;
	}
	public void setObsvId(String obsvId) {
		this.obsvId = obsvId;
	}
}
