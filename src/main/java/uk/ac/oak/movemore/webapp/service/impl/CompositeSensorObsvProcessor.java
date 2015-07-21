package uk.ac.oak.movemore.webapp.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.model.Sensors;
import uk.ac.oak.movemore.webapp.service.SensorObservationManager;
import uk.ac.oak.movemore.webapp.service.SensorObservationsProcessor;

public class CompositeSensorObsvProcessor implements
		SensorObservationsProcessor {

	private Collection<SensorObservationsProcessor> processors = new ArrayList<SensorObservationsProcessor>();

	@Override
	public Observations saveObservation(
			SensorObservationManager sensorObsvManager, Observations obsv)
			throws JSONException, Exception {
		for (SensorObservationsProcessor sensorObsvProcessor : processors) {
			Observations sensorObsv = sensorObsvProcessor.saveObservation(
					sensorObsvManager, obsv);
			if (sensorObsv != null) {
				return sensorObsv;
			}
		}
		return null;
	}

	@Override
	public Observations saveObservation(
			SensorObservationManager sensorObsvManager, Observations obsv,
			InputStream uploadedFile, String fileName) throws JSONException,
			Exception {

		for (SensorObservationsProcessor sensorObsvProcessor : processors) {
			Observations sensorObsv = sensorObsvProcessor.saveObservation(
					sensorObsvManager, obsv, uploadedFile, fileName);
			if (sensorObsv != null) {
				return sensorObsv;
			}
		}

		return null;
	}

	@Override
	public JSONArray countSensorObsvSubCategories(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate) {
		for (SensorObservationsProcessor sensorObsvProcessor : processors) {
			JSONArray result = sensorObsvProcessor
					.countSensorObsvSubCategories(sensorObsvManager, sensor,
							startDate, endDate);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Provides access to the sensor data "processors" list
	 * 
	 * @return a mutable ordered collection of processors
	 */
	public Collection<SensorObservationsProcessor> getProcessors() {
		return this.processors;
	}

	@Override
	public JSONArray findObservationsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc,
			Integer offset, Integer limit) {
		for (SensorObservationsProcessor sensorObsvProcessor : processors) {
			JSONArray result = sensorObsvProcessor.findObservationsBySensor(
					sensorObsvManager, sensor, startDate, endDate, orderByName,
					isAsc, offset, limit);
			if (result != null) {
				return result;
			}
		}

		return null;
	}

	@Override
	public JSONArray findObservationsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Integer offset, Integer limit) {
		for (SensorObservationsProcessor sensorObsvProcessor : processors) {
			JSONArray result = sensorObsvProcessor.findObservationsBySensor(
					sensorObsvManager, sensor, offset, limit);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	public JSONArray aggregateObservationResultsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate) {

		for (SensorObservationsProcessor sensorObsvProcessor : processors) {
			JSONArray result = sensorObsvProcessor
					.aggregateObservationResultsBySensor(sensorObsvManager,
							sensor, startDate, endDate);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	public Boolean migrateUnclassifiedObservations(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			List<Observations> obsvList) {
		for (SensorObservationsProcessor sensorObsvProcessor : processors) {
			Boolean result = sensorObsvProcessor
					.migrateUnclassifiedObservations(sensorObsvManager, sensor,
							obsvList);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	public List<Observations> queryUnclassifiedObservations(
			SensorObservationManager sensorObsvManager, Sensors sensor) {
		for (SensorObservationsProcessor sensorObsvProcessor : processors) {
			List<Observations> result = sensorObsvProcessor
					.queryUnclassifiedObservations(sensorObsvManager, sensor);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	public Boolean normaliseObservation(
			SensorObservationManager sensorObsvManager, Long obsvId,
			Map<String, String> normalisationValues) {
		for (SensorObservationsProcessor sensorObsvProcessor : processors) {
			Boolean result = sensorObsvProcessor.normaliseObservation(
					sensorObsvManager, obsvId, normalisationValues);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

}
