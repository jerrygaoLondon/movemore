package uk.ac.oak.movemore.webapp.service;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.model.Sensors;

public interface SensorObservationsProcessor {
	Observations saveObservation(SensorObservationManager sensorObsvManager,
			Observations obsv) throws JSONException, Exception;

	Observations saveObservation(SensorObservationManager sensorObsvManager,
			Observations obsv, InputStream uploadedFile, String fileName)
			throws JSONException, Exception;

	JSONArray findObservationsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc,
			Integer offset, Integer limit);

	JSONArray findObservationsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Integer offset, Integer limit);

	JSONArray aggregateObservationResultsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate);

	List<Observations> queryUnclassifiedObservations(
			SensorObservationManager sensorObsvManager, Sensors sensor);

	Boolean migrateUnclassifiedObservations(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			List<Observations> obsvList);

	JSONArray countSensorObsvSubCategories(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate);

	Boolean normaliseObservation(SensorObservationManager sensorObsvManager,
			Long obsvId, Map<String, String> normalisationValues);
}
