package uk.ac.oak.movemore.webapp.service.response;

import java.io.Serializable;
import uk.ac.oak.movemore.webapp.model.Device;

public class SensorDetail implements Serializable{

	private static final long serialVersionUID = 4316111216829795098L;

	//The observation time sent from sensor
	private String name;
	//The system time received from sensor
	private String description;
	//sensor observed value
	private Device device;
	
	private Long sensorId;
	
	
	public String getName() {
		return name;
	}

	public void setName(String nm) {
		this.name=nm;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String desc) {
		this.description = desc;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device dev) {
		this.device=dev;
	}

	public Long getSensorId() {
		return sensorId;
	}

	public void setSensorId(Long sensorId) {
		this.sensorId = sensorId;
	}
	
}
