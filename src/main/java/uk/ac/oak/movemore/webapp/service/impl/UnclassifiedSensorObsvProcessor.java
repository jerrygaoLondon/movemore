package uk.ac.oak.movemore.webapp.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;

import uk.ac.oak.movemore.webapp.dao.ObservationsDao;
import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.model.Sensors;
import uk.ac.oak.movemore.webapp.service.SensorObservationManager;
import uk.ac.oak.movemore.webapp.service.SensorObservationsProcessor;
import uk.ac.oak.movemore.webapp.util.SensorMediaFileManager;
import uk.ac.oak.movemore.webapp.util.SensorTypeEnum;
import uk.ac.oak.movemore.webapp.vo.ObservationsVO;

public class UnclassifiedSensorObsvProcessor implements
		SensorObservationsProcessor {
	protected final Log log = LogFactory
			.getLog(UnclassifiedSensorObsvProcessor.class);

	@Override
	public Observations saveObservation(SensorObservationManager sensorObsvManager, Observations obsv) {
		if (obsv != null && obsv.getSensor() != null) {
			if (obsv.getSensor().getSensorType() == null
					|| SensorTypeEnum.SENSOR_TYPE_UNCLASSIFIED.getSensorType()
							.equals(obsv.getSensor().getSensorType())) {
				log.info (String
						.format("process observations for unclassified sensors, sensor name is {%s}, observation value is {%s}, obsv time is {%s}",
								obsv.getSensor().getName(), obsv.getValue(),
								obsv.getObsvTime()));
				
				return sensorObsvManager.save(obsv);
			}
		}
		return null;
	}	
	
	@Override
	public Observations saveObservation(
			SensorObservationManager sensorObsvManager, Observations obsv,
			InputStream uploadedFile, String fileName) {
		if (obsv != null && obsv.getSensor() != null) {
			if (obsv.getSensor().getSensorType() == null
					|| SensorTypeEnum.SENSOR_TYPE_UNCLASSIFIED.getSensorType()
							.equals(obsv.getSensor().getSensorType())) {
				log.info (String
						.format("process observations for unclassified sensors, sensor name is {%s}, observation value is {%s}, obsv time is {%s}",
								obsv.getSensor().getName(), obsv.getValue(),
								obsv.getObsvTime()));
				
				String fileRelativePath = "";
				try {
					SensorMediaFileManager fileManager = new SensorMediaFileManager();
					fileRelativePath = fileManager.onSumbit(uploadedFile, fileName, obsv.getSensor().getDevice().getDevicePhysicalId(), obsv.getSensor().getSensorPhysicalId());
				} catch (IOException e) {
					log.error("Failure when attempting to store the media file.");
				}
				obsv.setAttachment(fileRelativePath);
				return sensorObsvManager.save(obsv);
			}
		}
		return null;
	}

	@Override
	public JSONArray findObservationsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc, Integer offset, Integer limit) {
		
		if (SensorTypeEnum.SENSOR_TYPE_UNCLASSIFIED.getSensorType().equals(sensor.getSensorType()) || sensor.getSensorType() == null) {
			ObservationsDao observationsDao = (ObservationsDao)sensorObsvManager.getObservationDao(SensorTypeEnum.SENSOR_TYPE_UNCLASSIFIED);
			List<Observations> obsvList = observationsDao.findObservationsBySensor(sensor, startDate, endDate, orderByName, isAsc, offset, limit);
			
			List<ObservationsVO> obsvVOList = ObservationsVO.copyCollection(obsvList);
			return new JSONArray(obsvVOList);
		}
		return null;
	}

	@Override
	public JSONArray findObservationsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Integer offset, Integer limit) {
		if (SensorTypeEnum.SENSOR_TYPE_UNCLASSIFIED.getSensorType().equals(sensor.getSensorType()) || sensor.getSensorType() == null) {
			ObservationsDao observationsDao = (ObservationsDao)sensorObsvManager.getObservationDao(SensorTypeEnum.SENSOR_TYPE_UNCLASSIFIED);
			List<Observations> obsvList = observationsDao.findObservationsBySensor(sensor, offset, limit);
		
			List<ObservationsVO> obsvVOList = ObservationsVO.copyCollection(obsvList);
			return new JSONArray(obsvVOList);
		}
		return null;
	}

	@Override
	public JSONArray aggregateObservationResultsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate) {
		if (SensorTypeEnum.SENSOR_TYPE_UNCLASSIFIED.getSensorType().equals(sensor.getSensorType()) || sensor.getSensorType() == null) {
			return new JSONArray();
		}
		
		return null;
	}

	@Override
	public Boolean migrateUnclassifiedObservations(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			List<Observations> obsvList) {
		if (SensorTypeEnum.SENSOR_TYPE_UNCLASSIFIED.getSensorType().equals(sensor.getSensorType()) || sensor.getSensorType() == null) {
			return true;
		}
		
		return null;
	}

	@Override
	public List<Observations> queryUnclassifiedObservations(
			SensorObservationManager sensorObsvManager, Sensors sensor) {
		if (SensorTypeEnum.SENSOR_TYPE_UNCLASSIFIED.getSensorType().equals(sensor.getSensorType()) || sensor.getSensorType() == null) {
			return new ArrayList<Observations>();
		}
		return null;
	}

	@Override
	public JSONArray countSensorObsvSubCategories(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate) {
		if (SensorTypeEnum.SENSOR_TYPE_UNCLASSIFIED.getSensorType().equals(sensor.getSensorType()) || sensor.getSensorType() == null) {
			return new JSONArray();
		}
		
		return null;
	}

	@Override
	public Boolean normaliseObservation(SensorObservationManager sensorObsvManager, Long obsvId,
			Map<String, String> normalisationValues) {
		// TODO Auto-generated method stub
		return null;
	}
}
