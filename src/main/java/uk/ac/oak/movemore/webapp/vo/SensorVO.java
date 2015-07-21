package uk.ac.oak.movemore.webapp.vo;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import uk.ac.oak.movemore.webapp.model.Sensors;

public class SensorVO implements Serializable{

	private static final long serialVersionUID = 554893609817023597L;
	
	private Long sensorId;
	private String sensorPhysicalId;
	private Long deviceId;
	private String devicePhysicalId;
	private String name;
	private String description;
	private Long sensorType;
	
//	public static final String MODEL_SCHEMA = "\"sensors\": {"
//			+ "\"sensorId\" : ";
	
	public Long getSensorId() {
		return sensorId;
	}
	public void setSensorId(Long sensorId) {
		this.sensorId = sensorId;
	}
	public String getSensorPhysicalId() {
		return sensorPhysicalId;
	}
	public void setSensorPhysicalId(String sensorPhysicalId) {
		this.sensorPhysicalId = sensorPhysicalId;
	}
	public Long getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}
	public String getDevicePhysicalId() {
		return devicePhysicalId;
	}
	public void setDevicePhysicalId(String devicePhysicalId) {
		this.devicePhysicalId = devicePhysicalId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getSensorType() {
		return sensorType;
	}
	public void setSensorType(Long sensorType) {
		this.sensorType = sensorType;
	}
	
	public void clone(Sensors sensor) {
		setSensorId(sensor.getSensorId());
		setName(sensor.getName());
		setSensorPhysicalId(sensor.getSensorPhysicalId());
		setDescription(sensor.getDescription());
		setDeviceId(sensor.getDevice().getDeviceId());
		setDevicePhysicalId(sensor.getDevice().getDevicePhysicalId());
		setSensorType(sensor.getSensorType() != null ? Long.valueOf(sensor.getSensorType()) : 0);
	}
	
	public static List<SensorVO> copyCollection(Collection<Sensors> sensorSet) {
		List<SensorVO> sensorVOList = new LinkedList<SensorVO>();
		
		for (Sensors sensor : sensorSet) {
			SensorVO sensorVO = new SensorVO();
			sensorVO.clone(sensor);
			sensorVOList.add(sensorVO);
		}
		
		return sensorVOList;
	}
}
