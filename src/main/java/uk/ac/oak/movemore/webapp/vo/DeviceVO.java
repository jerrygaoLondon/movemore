package uk.ac.oak.movemore.webapp.vo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import uk.ac.oak.movemore.webapp.model.Device;

public class DeviceVO implements Serializable{

	private static final long serialVersionUID = 5602817167350566472L;

	private Long deviceId;
	private String name;
	private String description;
	private String devicePhysicalId;
	private Double longitude;
	private Double latitude;
	private String lastKnownIP;
	private Float batteryLevel;
	
	public Long getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
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
	public String getDevicePhysicalId() {
		return devicePhysicalId;
	}
	public void setDevicePhysicalId(String devicePhysicalId) {
		this.devicePhysicalId = devicePhysicalId;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public String getLastKnownIP() {
		return lastKnownIP;
	}
	public void setLastKnownIP(String lastKnownIP) {
		this.lastKnownIP = lastKnownIP;
	}
	
	public Float getBatteryLevel() {
		return batteryLevel;
	}
	public void setBatteryLevel(Float batteryLevel) {
		this.batteryLevel = batteryLevel;
	}
	public void clone(Device device) {
		setDeviceId(device.getDeviceId());
		setName(device.getName());
		setDescription(device.getDescription());
		setDevicePhysicalId(device.getDevicePhysicalId());
		setLastKnownIP(device.getLastKnownIP());
		setLongitude(device.getLongitude());
		setLatitude(device.getLatitude());
		setBatteryLevel(device.getBatteryLevel());
	}
	
	public static List<DeviceVO> copyCollection (List<Device> deviceList) {
		List<DeviceVO> deviceVOList = new LinkedList<DeviceVO>();
		for (Device device : deviceList) {
			DeviceVO deviceVO = new DeviceVO();
			deviceVO.clone(device);
			deviceVOList.add(deviceVO);
		}
		return deviceVOList;
	}
}
