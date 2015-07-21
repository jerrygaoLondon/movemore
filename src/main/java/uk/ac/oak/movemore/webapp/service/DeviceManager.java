package uk.ac.oak.movemore.webapp.service;

import org.appfuse.service.GenericManager;

import uk.ac.oak.movemore.webapp.dao.DeviceNotFoundException;
import uk.ac.oak.movemore.webapp.model.Device;
import uk.ac.oak.movemore.webapp.service.response.JSONResponse;

public interface DeviceManager extends GenericManager<Device, Long> {

	JSONResponse updateDeviceLocation(String deviceId, Float latitude,
			Float longitude);

	JSONResponse registerNewDevice(String devicePhysicalId, Float latitude,
			Float longtitude, String deviceName);
	
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
	public JSONResponse registerOrUpdateDeviceInfo(String devicePhysicalId, String deviceName,
			String sensorPhysicalId, String sensorName, Double latitude,
			Double longitude, Float batteryLevel, String sensorDescription, String sensorType);
}
