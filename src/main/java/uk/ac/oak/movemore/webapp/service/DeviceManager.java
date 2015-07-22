package uk.ac.oak.movemore.webapp.service;

import javax.ws.rs.core.Response;

import org.appfuse.service.GenericManager;

import uk.ac.oak.movemore.webapp.dao.DeviceNotFoundException;
import uk.ac.oak.movemore.webapp.model.Device;

public interface DeviceManager extends GenericManager<Device, Long> {

	Response updateDeviceLocation(String deviceId, Double latitude,
			Double longitude);

	Response registerNewDevice(String devicePhysicalId, Double latitude,
			Double longtitude, String deviceName);
	
	Device addOrUpdateDevice(String devicePhysicalId, String deviceName, Double latitude, Double longtitude, Float batteryLevel);
	
	Device findDeviceByPhysicalId(String devicePhysicalId) throws DeviceNotFoundException;
	
	/**
	 * 
	 * @param devicePhysicalId
	 * @param deviceName
	 * @param sensorPhysicalId
	 * @param sensorName
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public Response registerOrUpdateDeviceInfo(String devicePhysicalId, String deviceName,
			String sensorPhysicalId, String sensorName, Double latitude,
			Double longitude, Float batteryLevel, String sensorDescription, String sensorType);
}
