package uk.ac.oak.movemore.webapp.service;

import org.appfuse.service.GenericManager;

import uk.ac.oak.movemore.webapp.dao.SensorNotFoundException;
import uk.ac.oak.movemore.webapp.model.Device;
import uk.ac.oak.movemore.webapp.model.Sensors;
import uk.ac.oak.movemore.webapp.service.response.JSONResponse;

public interface SensorManager extends GenericManager<Sensors, Long> {

	JSONResponse addNewSensor(String devicePhysicalId, String sensorPhysicalId,
			String sensorName, Integer sensorType);

	/**
	 * 
	 * @param sensorId
	 *            system id
	 * @return
	 */
	JSONResponse removeSensor(String sensorId);

	Sensors addOrUpdateSensor(Device device, String sensorPhysicalId,
			String sensorName, String sensorDescription, String sensorType);

	Sensors updateSensorType(Long sensorId, Integer sensorType);

	boolean isSensorPhysicalIdExist(String devicePhysicalId);

	Sensors findSensorByPhysicalId(String devicePhysicalId)
			throws SensorNotFoundException;
}
