package uk.ac.oak.movemore.webapp.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.oak.movemore.webapp.dao.ObsvDeviceDetectionDao;
import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.model.ObsvDeviceDetection;
import uk.ac.oak.movemore.webapp.model.Sensors;
import uk.ac.oak.movemore.webapp.service.SensorObservationManager;
import uk.ac.oak.movemore.webapp.service.SensorObservationsProcessor;
import uk.ac.oak.movemore.webapp.util.DateUtil;
import uk.ac.oak.movemore.webapp.util.SensorTypeEnum;
import uk.ac.oak.movemore.webapp.vo.ObsvDeviceDetectionVO;

/**
 * We assume bluetooth sensor will send detected device bluetooth mac address
 * the observation value will be processed and store in obsv_device_detection
 * @author jieg
 *
 */
public class BluetoothSensorObsvProcessor implements SensorObservationsProcessor {
	protected final Log log = LogFactory.getLog(BluetoothSensorObsvProcessor.class);
	
	@Override
	public Observations saveObservation(SensorObservationManager sensorObsvManager, Observations obsv) {
		if (obsv != null && obsv.getSensor() != null) {
			if (obsv.getSensor().getSensorType() != null
					&& SensorTypeEnum.SENSOR_TYPE_BLUETOOTH_SENSOR.getSensorType()
							.equals(obsv.getSensor().getSensorType())) {
				log.info(String
						.format("process observations for Bluetooth sensors, sensor name is {%s}, observation value is {%s}, obsv time is {%s}",
								obsv.getSensor().getName(), obsv.getValue(),
								obsv.getObsvTime()));
				try {
					ObsvDeviceDetection deviceDetectionObsv = new ObsvDeviceDetection(obsv, obsv.getObsvTime());
					
					deviceDetectionObsv.setLatitude(obsv.getLatitude());
					deviceDetectionObsv.setLongitude(obsv.getLongitude());
					
					decodeAndSetObsvParameters(deviceDetectionObsv, obsv.getValue());
					
					obsv.setObsvDeviceDetect(deviceDetectionObsv);
					
					obsv = sensorObsvManager.save(obsv);
					
					return obsv;
				} catch (Exception ex) {
					log.error(String
							.format("Error in processing observations for Bluetooth device sensors, sensor id is {%s}, sensor physical id is: {%s} sensor name is {%s}, observation value is {%s}, obsv time is {%s}",
									obsv.getSensor().getSensorId(), obsv
											.getSensor().getSensorPhysicalId(),
									obsv.getSensor().getName(),
									obsv.getValue(), obsv.getObsvTime()));
				}
				
				return null;
			}
		}
		return null;
	}

	public void decodeAndSetObsvParameters(ObsvDeviceDetection obsvDeviceDetection,
			String obsvValue) {
		try {
			JSONObject result = new JSONObject(obsvValue);
			String detectedDeviceId = result.getString("id");
			Long firstTime = result.getLong("firstTime");
			Long lastTime = null;
			
			boolean isLastTimeNull = result.isNull("lastTime");
			if (!isLastTimeNull) {
				lastTime = result.getLong("lastTime");
			}
			
			Date _firstTime = new Date(firstTime);
			Date _lastTime = null;
			
			if (lastTime!=null) {
				_lastTime = new Date(lastTime);
			}
			
			obsvDeviceDetection.setMacAddress(detectedDeviceId);
			obsvDeviceDetection.setFirstObsvTime(_firstTime);
			obsvDeviceDetection.setLastObsvTime(_lastTime);
		} catch (JSONException jsonEx) {
			log.error("Bluetooth sensor observation value syntax error! set obsvValue directly as device id instead.");
			log.error(jsonEx.toString());
			obsvDeviceDetection.setMacAddress(obsvValue);
		}
	}
	
	@Override
	public JSONArray findObservationsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc, Integer offset, Integer limit) {
		if (!SensorTypeEnum.SENSOR_TYPE_BLUETOOTH_SENSOR.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		
		ObsvDeviceDetectionDao obsvDeviceDetectionDao =  (ObsvDeviceDetectionDao) sensorObsvManager.getObservationDao(SensorTypeEnum.SENSOR_TYPE_BLUETOOTH_SENSOR);
		
		List<ObsvDeviceDetection> obsvList = obsvDeviceDetectionDao.findDeviceObservationsBySensor(sensor, startDate, endDate, orderByName, isAsc, offset, limit);
		
		List<ObsvDeviceDetectionVO> obsvDeviceDetectionVOList = ObsvDeviceDetectionVO.copyCollection(obsvList);
		
		return new JSONArray(obsvDeviceDetectionVOList);
	}

	@Override
	public JSONArray findObservationsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Integer offset, Integer limit) {
		if (!SensorTypeEnum.SENSOR_TYPE_BLUETOOTH_SENSOR.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		
		ObsvDeviceDetectionDao obsvDeviceDetectionDao =  (ObsvDeviceDetectionDao) sensorObsvManager.getObservationDao(SensorTypeEnum.SENSOR_TYPE_BLUETOOTH_SENSOR);
		
		List<ObsvDeviceDetection> obsvList = obsvDeviceDetectionDao.findDeviceObservationsBySensor(sensor, offset, limit);
		
		List<ObsvDeviceDetectionVO> obsvDeviceDetectionVOList = ObsvDeviceDetectionVO.copyCollection(obsvList);
		return new JSONArray(obsvDeviceDetectionVOList);
	}

	@Override
	public JSONArray aggregateObservationResultsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate) {
		if (!SensorTypeEnum.SENSOR_TYPE_BLUETOOTH_SENSOR.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		
		ObsvDeviceDetectionDao obsvDeviceDetectionDao =  (ObsvDeviceDetectionDao) sensorObsvManager.getObservationDao(SensorTypeEnum.SENSOR_TYPE_BLUETOOTH_SENSOR);
		
		List<Object[]> resultArrList = obsvDeviceDetectionDao.aggregateDeviceObservationBySensor(sensor, startDate, endDate);
		
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
		if (!SensorTypeEnum.SENSOR_TYPE_BLUETOOTH_SENSOR.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		
		return new ArrayList<Observations>();
	}

	@Override
	public Boolean migrateUnclassifiedObservations(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			List<Observations> obsvList) {
		if (!SensorTypeEnum.SENSOR_TYPE_BLUETOOTH_SENSOR.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		return true;
	}

	@Override
	public Observations saveObservation(
			SensorObservationManager sensorObsvManager, Observations obsv,
			InputStream uploadedFile, String fileName) {
		
		return saveObservation (sensorObsvManager, obsv);
	}	
	
	@Override
	public JSONArray countSensorObsvSubCategories(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate) {
		if (!SensorTypeEnum.SENSOR_TYPE_BLUETOOTH_SENSOR.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		return new JSONArray();
	}
	
	

	@Override
	public Boolean normaliseObservation(SensorObservationManager sensorObsvManager, Long obsvId,
			Map<String, String> normalisationValues) {
		return null;
	}

	public static void main(String[] args) {
		//String obsvValue = "{\"id\":\"daqn2vV25OkmFnPE9rQNBFKqLDbiBegOvV4jQwyiRxo=\", \"firstTime\":1406133779048, \"lastTime\": 1406133856498}";
		String obsvValue = "{\"id\":\"daqn2vV25OkmFnPE9rQNBFKqLDbiBegOvV4jQwyiRxo=\", \"firstTime\":1406133779048, \"lastTime\": null}";
		
		JSONObject result = new JSONObject(obsvValue);
		String detectedDeviceId = result.getString("id");
		Long firstTime = result.getLong("firstTime");
		
	//	Object lastTimeObj = result.get("lastTime");
		
		boolean isLastTimeNull = result.isNull("lastTime");
		System.out.println("isLastTimeNull:"+isLastTimeNull);
		
		Long lastTime = null;
		
		if (!isLastTimeNull) {
			lastTime = result.getLong("lastTime");
		}
		
		Date _firstTime = new Date(firstTime);
		Date _lastTime = null;
		
		if (lastTime != null) {
			_lastTime = new Date(lastTime);
		}
		
		System.out.println ("BT Device ID (Address):"+detectedDeviceId);
		System.out.println ("firstTime:"+ _firstTime.toString());
		System.out.println ("lastTime:"+ _lastTime.toString());
	}
	
}
