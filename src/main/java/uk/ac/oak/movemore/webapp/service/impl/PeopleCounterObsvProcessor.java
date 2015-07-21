package uk.ac.oak.movemore.webapp.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;

import uk.ac.oak.movemore.webapp.dao.ObsvPeopleCountDao;
import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.model.ObsvPeopleCount;
import uk.ac.oak.movemore.webapp.model.Sensors;
import uk.ac.oak.movemore.webapp.service.SensorObservationManager;
import uk.ac.oak.movemore.webapp.service.SensorObservationsProcessor;
import uk.ac.oak.movemore.webapp.util.SensorTypeEnum;
import uk.ac.oak.movemore.webapp.vo.ObsvPeopleCountVO;

/**
 * We assume bluetooth sensor will send detected people number, the observation value
 * will be processed and store in obsv_people_counter table
 * 
 * @author jieg
 * 
 */
public class PeopleCounterObsvProcessor implements SensorObservationsProcessor {
	protected final Log log = LogFactory.getLog(PeopleCounterObsvProcessor.class);
	
	@Override
	public Observations saveObservation(SensorObservationManager sensorObsvManager, Observations obsv) {
		if (obsv != null && obsv.getSensor() != null) {
			if (obsv.getSensor().getSensorType() != null
					&& SensorTypeEnum.SENSOR_TYPE_PEOPLE_COUNTER.getSensorType()
							.equals(obsv.getSensor().getSensorType())) {
				log.info(String
						.format("process observations for People Counting sensors, sensor name is {%s}, observation value is {%s}, obsv time is {%s}",
								obsv.getSensor().getName(), obsv.getValue(),
								obsv.getObsvTime()));
				try {
					Integer peopleNumber = Integer.valueOf(obsv.getValue());
					
					ObsvPeopleCount peopleCounterObsv = new ObsvPeopleCount(obsv,
							peopleNumber, obsv.getObsvTime());
					peopleCounterObsv.setLatitude(obsv.getLatitude());
					peopleCounterObsv.setLongitude(obsv.getLongitude());
					
					obsv.setObsvPeopleCount(peopleCounterObsv);
					peopleCounterObsv.setpCount(obsv);
					
					return sensorObsvManager.save(obsv);
				} catch (Exception ex) {
					log.error(String
							.format("Error in processing observations for people number counting sensors, sensor id is {%s}, sensor physical id is: {%s} sensor name is {%s}, observation value is {%s}, obsv time is {%s}",
									obsv.getSensor().getSensorId(), obsv
											.getSensor().getSensorPhysicalId(),
									obsv.getSensor().getName(),
									obsv.getValue(), obsv.getObsvTime()));
					log.error(ex.toString());

				}
				return null;
			}
		}
		return null;
	}
	
	@Override
	public Observations saveObservation(
			SensorObservationManager sensorObsvManager, Observations obsv,
			InputStream uploadedFile, String fileName) {
		
		return saveObservation(sensorObsvManager, obsv);
	}

	@Override
	public JSONArray findObservationsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc, Integer offset, Integer limit) {
		if (!SensorTypeEnum.SENSOR_TYPE_PEOPLE_COUNTER.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		ObsvPeopleCountDao obsvPeopleCountDao = (ObsvPeopleCountDao) sensorObsvManager.getObservationDao(SensorTypeEnum.SENSOR_TYPE_PEOPLE_COUNTER);
		
		List<ObsvPeopleCount> obsvList = obsvPeopleCountDao.findPeopleCounterObservationsBySensor(sensor, startDate, endDate, orderByName, isAsc, offset, limit);
		
		List<ObsvPeopleCountVO> obsvVOList = ObsvPeopleCountVO.copyCollection(obsvList);
		return new JSONArray(obsvVOList);
	}

	@Override
	public JSONArray findObservationsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Integer offset, Integer limit) {
		if (!SensorTypeEnum.SENSOR_TYPE_PEOPLE_COUNTER.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		
		ObsvPeopleCountDao obsvPeopleCountDao = (ObsvPeopleCountDao) sensorObsvManager.getObservationDao(SensorTypeEnum.SENSOR_TYPE_PEOPLE_COUNTER);
		
		List<ObsvPeopleCount> obsvList = obsvPeopleCountDao.findPeopleCounterObservationsBySensor(sensor, offset, limit);
		
		List<ObsvPeopleCountVO> obsvVOList = ObsvPeopleCountVO.copyCollection(obsvList);
		return new JSONArray(obsvVOList);
	}

	@Override
	public JSONArray aggregateObservationResultsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate) {
		if (!SensorTypeEnum.SENSOR_TYPE_PEOPLE_COUNTER.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		
		return new JSONArray();
	}
	
	@Override
	public Boolean migrateUnclassifiedObservations(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			List<Observations> obsvList) {
		if (!SensorTypeEnum.SENSOR_TYPE_PEOPLE_COUNTER.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		return true;
	}

	@Override
	public List<Observations> queryUnclassifiedObservations(SensorObservationManager sensorObsvManager, Sensors sensor) {
		if (!SensorTypeEnum.SENSOR_TYPE_PEOPLE_COUNTER.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		return new ArrayList<Observations>();
	}

	@Override
	public JSONArray countSensorObsvSubCategories(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate) {
		if (!SensorTypeEnum.SENSOR_TYPE_PEOPLE_COUNTER.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		
		return new JSONArray();
	}

	@Override
	public Boolean normaliseObservation(SensorObservationManager sensorObsvManager, Long obsvId,
			Map<String, String> normalisationValues) {
		// TODO Auto-generated method stub
		return null;
	}
}
