package uk.ac.oak.movemore.webapp.dao;

import java.util.List;

import uk.ac.oak.movemore.webapp.model.Sensors;

public interface SensorsDao extends org.appfuse.dao.GenericDao<Sensors, Long>{
	boolean isSensorPhysicalIdExist(String devicePhysicalId);
	Sensors findSensorByPhysicalId(String devicePhysicalId) throws SensorNotFoundException;
	List<Sensors> getAllSensors();
	/**
	 * count sensors by type
	 * @return String[]: 0: sensor type; 1: count(i.e., number of sensors); 2: sensor type name
	 */
	List<String[]> countSensorsByType();
}
