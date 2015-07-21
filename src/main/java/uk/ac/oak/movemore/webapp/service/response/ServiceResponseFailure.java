package uk.ac.oak.movemore.webapp.service.response;

import java.io.Serializable;

public class ServiceResponseFailure extends JSONResponse implements Serializable{

	private static final long serialVersionUID = 1550721339652527874L;
	private String reason;
	private String message;
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
