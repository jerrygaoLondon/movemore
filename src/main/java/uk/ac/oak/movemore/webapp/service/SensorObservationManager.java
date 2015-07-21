package uk.ac.oak.movemore.webapp.service;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.appfuse.dao.GenericDao;
import org.appfuse.service.GenericManager;

import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.model.ObsvCarRegPlateDetection;
import uk.ac.oak.movemore.webapp.model.ObsvDeviceDetection;
import uk.ac.oak.movemore.webapp.model.ObsvPeopleCount;
import uk.ac.oak.movemore.webapp.model.Sensors;
import uk.ac.oak.movemore.webapp.service.response.JSONResponse;
import uk.ac.oak.movemore.webapp.util.SensorTypeEnum;

public interface SensorObservationManager extends
		GenericManager<Observations, Long> {

	public JSONResponse pushSensorObservation(String sensorId, String value,
			String obsvTime);

	public JSONResponse getSensorObservationById(String obsvId);

	public JSONResponse getSensorObservationBySensorId(String sensorId);

	/**
	 * 
	 * @param sensorId
	 *            (sensor physical id, e.g., mac address, host name)
	 * @param time
	 * @param values
	 *            (can be detected Mac addresses, detected face number, hashed
	 *            number plate)
	 * @return
	 */
	Response saveObservation(String sensorPhysicalId, String time,
			List<String> values);

	Set<ObsvDeviceDetection> findDetectedDeviceObsvsBySensor(Sensors sensor);

	Set<ObsvPeopleCount> findPeopleCounterObservationsBySensor(Sensors sensor);

	Set<ObsvCarRegPlateDetection> findCarRegPlateObservationsBySensor(
			Sensors sensor);

	@SuppressWarnings("rawtypes")
	GenericDao getObservationDao(SensorTypeEnum sensorType);

	public Response getSensorObservationBySensorId(String sensorId,
			String startDate, String endDate, String orderByName,
			Boolean isAsc, Integer offset, Integer limit);

	Response sensorDataBulkUpLoad(String sensorPhysicalId,
			InputStream fileInputSteam, String fileName);
	
	Response normaliseObservation(Long obsvId, String finalActivityType,
			String finalLatitude, String finalLongitude, String finalConfidence);
}
