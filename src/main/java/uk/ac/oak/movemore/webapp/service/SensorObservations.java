package uk.ac.oak.movemore.webapp.service;

import java.io.Serializable;
import java.util.Date;

public class SensorObservations implements Serializable{

	private static final long serialVersionUID = -9051640104721424398L;

	private String sensorPhysicalId;
	//private List<String> macAddresses;
	private Date time;
	
	public String getSensorPhysicalId() {
		return sensorPhysicalId;
	}
	public void setSensorPhysicalId(String sensorPhysicalId) {
		this.sensorPhysicalId = sensorPhysicalId;
	}
//	public List<String> getMacAddresses() {
//		return macAddresses;
//	}
//	public void setMacAddresses(List<String> macAddresses) {
//		this.macAddresses = macAddresses;
//	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	
	
}
