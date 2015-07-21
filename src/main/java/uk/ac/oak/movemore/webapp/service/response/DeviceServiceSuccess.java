package uk.ac.oak.movemore.webapp.service.response;

import java.io.Serializable;

public class DeviceServiceSuccess extends JSONResponse implements Serializable{

	private static final long serialVersionUID = -2732511599860148922L;

	private String deviceId;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
}
