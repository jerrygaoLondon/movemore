package uk.ac.oak.movemore.webapp.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import uk.ac.oak.movemore.webapp.dao.ObsvCarRegPlateDetectionDao;
import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.model.ObsvCarRegPlateDetection;
import uk.ac.oak.movemore.webapp.model.Sensors;
import uk.ac.oak.movemore.webapp.service.SensorObservationManager;
import uk.ac.oak.movemore.webapp.service.SensorObservationsProcessor;
import uk.ac.oak.movemore.webapp.util.DateUtil;
import uk.ac.oak.movemore.webapp.util.SensorTypeEnum;
import uk.ac.oak.movemore.webapp.vo.ObsvCarRegPlateDetectionVO;

/**
 * We assume car registration plate sensors will send detected car plate number (encrypted or raw num),
 * the observation value will be processed and store in obsv_carregplate_detection table
 * 
 * @author jieg
 * 
 */
public class CarRegPlateSensorObsvProcessor implements
		SensorObservationsProcessor {
	protected final Log log = LogFactory.getLog(CarRegPlateSensorObsvProcessor.class);
	
	@Override
	public Observations saveObservation(SensorObservationManager sensorObsvManager, Observations obsv) {
		if (obsv != null && obsv.getSensor() != null) {
			if (obsv.getSensor().getSensorType() != null
					&& SensorTypeEnum.SENSOR_TYPE_CAR_REG_PLATE_SENSOR.getSensorType()
							.equals(obsv.getSensor().getSensorType())) {
				log.info(String
						.format("process observations for Car Registeration Plate sensors, sensor name is {%s}, observation value is {%s}, obsv time is {%s}",
								obsv.getSensor().getName(), obsv.getValue(),
								obsv.getObsvTime()));
				try {
					String carRegPlateNum = obsv.getValue();
					
					ObsvCarRegPlateDetection carPlateObsv = new ObsvCarRegPlateDetection(obsv, carRegPlateNum, obsv.getObsvTime());
					carPlateObsv.setLatitude(obsv.getLatitude());
					carPlateObsv.setLongitude(obsv.getLongitude());
					
					obsv.setObsvCarRegPlateDetect(carPlateObsv);
					carPlateObsv.setCarRegPlate(obsv);
					
					obsv = sensorObsvManager.save(obsv);
					
					return obsv;
				} catch (Exception ex) {
					log.error(String
							.format("Error in processing observations for Car Registeration Plate sensors, sensor id is {%s}, sensor physical id is: {%s} sensor name is {%s}, observation value is {%s}, obsv time is {%s}",
									obsv.getSensor().getSensorId(), obsv
											.getSensor().getSensorPhysicalId(),
									obsv.getSensor().getName(),
									obsv.getValue(), obsv.getObsvTime()));
					
					log.error(ex.toString());
				}
				
			}
		}
		return null;
	}

	@Override
	public JSONArray findObservationsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc, Integer offset, Integer limit) {
		if (!SensorTypeEnum.SENSOR_TYPE_CAR_REG_PLATE_SENSOR.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		
		ObsvCarRegPlateDetectionDao carRegPlateDetectionDao = (ObsvCarRegPlateDetectionDao)sensorObsvManager.getObservationDao(SensorTypeEnum.SENSOR_TYPE_CAR_REG_PLATE_SENSOR);
		
		List<ObsvCarRegPlateDetection> obsvCarList = carRegPlateDetectionDao.findCarRegPlateObservationsBySensor(sensor, startDate, endDate, orderByName, isAsc, offset, limit);
		
		List<ObsvCarRegPlateDetectionVO> obsvCarVOList = ObsvCarRegPlateDetectionVO.copyCollection(obsvCarList);
		return  new JSONArray(obsvCarVOList);
	}

	@Override
	public JSONArray findObservationsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Integer offset, Integer limit) {
		if (!SensorTypeEnum.SENSOR_TYPE_CAR_REG_PLATE_SENSOR.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		
		ObsvCarRegPlateDetectionDao carRegPlateDetectionDao = (ObsvCarRegPlateDetectionDao)sensorObsvManager.getObservationDao(SensorTypeEnum.SENSOR_TYPE_CAR_REG_PLATE_SENSOR);
		List<ObsvCarRegPlateDetection> obsvCarList = carRegPlateDetectionDao.findCarRegPlateObservationsBySensor(sensor, offset, limit);
		
		List<ObsvCarRegPlateDetectionVO> obsvCarVOList = ObsvCarRegPlateDetectionVO.copyCollection(obsvCarList);
		return new JSONArray(obsvCarVOList);
	}

	@Override
	public JSONArray aggregateObservationResultsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate) {
		if (!SensorTypeEnum.SENSOR_TYPE_CAR_REG_PLATE_SENSOR.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		ObsvCarRegPlateDetectionDao carRegPlateDetectionDao = (ObsvCarRegPlateDetectionDao)sensorObsvManager.getObservationDao(SensorTypeEnum.SENSOR_TYPE_CAR_REG_PLATE_SENSOR);
		
		List<Object[]> resultArrList = carRegPlateDetectionDao.aggregateDeviceObservationBySensor(sensor, startDate, endDate);
		
		JSONArray jsonArray = new JSONArray();
		for (Object[] resultRow : resultArrList) {
			JSONObject result = new JSONObject();
			
			Date obsvTime = (Date) resultRow[0];
			String utcTime = DateUtil.convertUTCDateToLocalTime(obsvTime);			 
			result.put("time", utcTime);
			result.put("count", (Long)resultRow[1]);
			 
			jsonArray.put(result);
		}
		
		return jsonArray;
	}

	@Override
	public List<Observations> queryUnclassifiedObservations(SensorObservationManager sensorObsvManager, Sensors sensor) {
		if (!SensorTypeEnum.SENSOR_TYPE_CAR_REG_PLATE_SENSOR.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		return new ArrayList<Observations>();
	}
	
	@Override
	public Boolean migrateUnclassifiedObservations(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			List<Observations> obsvList) {
		if (!SensorTypeEnum.SENSOR_TYPE_CAR_REG_PLATE_SENSOR.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		return true;
	}

	@Override
	public Observations saveObservation(
			SensorObservationManager sensorObsvManager, Observations obsv,
			InputStream uploadedFile, String fileName) {
		return saveObservation(sensorObsvManager, obsv);
	}

	@Override
	public JSONArray countSensorObsvSubCategories(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate) {
		if (!SensorTypeEnum.SENSOR_TYPE_CAR_REG_PLATE_SENSOR.getSensorType().equals(sensor.getSensorType())) {
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
