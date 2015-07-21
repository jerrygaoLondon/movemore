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
import org.springframework.transaction.annotation.Transactional;

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
 * with the signal strength (e.g., "70:56:81:B2:38:2D,-1"), the observation
 * value will be processed and store in obsv_device_detection table
 * 
 * @author jieg
 * 
 */
public class WifiDeviceSensorObsvProcessor implements
		SensorObservationsProcessor {
	protected final Log log = LogFactory
			.getLog(WifiDeviceSensorObsvProcessor.class);

	@Override
	public Observations saveObservation(
			SensorObservationManager sensorObsvManager, Observations obsv) {
		if (obsv != null && obsv.getSensor() != null) {
			if (obsv.getSensor().getSensorType() != null
					&& SensorTypeEnum.SENSOR_TYPE_WIFI_SENSOR.getSensorType()
							.equals(obsv.getSensor().getSensorType())) {
				log.debug(String
						.format("process observations for wifi device sensors, sensor name is {%s}, observation value is {%s}, obsv time is {%s}",
								obsv.getSensor().getName(), obsv.getValue(),
								obsv.getObsvTime()));
				try {
					List<ObsvDeviceDetection> obsvDeviceList = decodeAndSetObsvParameters(obsv, obsv.getValue());
				
					for (ObsvDeviceDetection obsvDevice : obsvDeviceList) {
						obsv.setObsvDeviceDetect(obsvDevice);
						sensorObsvManager.save(obsv);
					}

					return obsv;
				} catch (Exception ex) {
					log.error(String
							.format("Error in processing observations for WIFI device sensors, sensor id is {%s}, sensor physical id is: {%s} sensor name is {%s}, observation value is {%s}, obsv time is {%s}",
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
	
	public List<ObsvDeviceDetection> decodeAndSetObsvParameters(Observations obsv,
			String obsvValue) {
		List<ObsvDeviceDetection> detectedDeviceList = new ArrayList<ObsvDeviceDetection>();
		
		try {
			JSONObject obsObj = new JSONObject(obsvValue);
			
			String detectedDeviceId = obsObj.getString("id");
			
			JSONArray obsvArray = obsObj.getJSONArray("obs");
			for (int i = 0; i < obsvArray.length(); i++) {
				ObsvDeviceDetection deviceDetectionObsv = new ObsvDeviceDetection(obsv, obsv.getObsvTime());
				
				deviceDetectionObsv.setMacAddress(detectedDeviceId);
				deviceDetectionObsv.setLatitude(obsv.getLatitude());
				deviceDetectionObsv.setLongitude(obsv.getLongitude());
				
				JSONObject json = obsvArray.getJSONObject(i);
				Integer power = json.getInt("power");
				Long firstTime = json.getLong("firstTime");
				boolean isLastTimeNull = json.isNull("lastTime");
				
				Long lastTime = null;
				if (!isLastTimeNull) {
					lastTime = json.getLong("lastTime");	
				}		
				Date _firstTime = new Date(firstTime);
				
				Date _lastTime = null;
				if (lastTime!=null) {
					_lastTime = new Date(lastTime);
				}
				
				deviceDetectionObsv.setSignalStrength(power);
				deviceDetectionObsv.setFirstObsvTime(_firstTime);
				deviceDetectionObsv.setLastObsvTime(_lastTime);
				
				detectedDeviceList.add(deviceDetectionObsv);
			}
		} catch (JSONException jsonEx) {
			log.error("Wifi device sensor observation value syntax error! try previous csv format instead.");
			log.error(jsonEx.toString());
			
			//TODO: Temporary code to be removed later
			String _obsvValue = obsv.getValue();
			String[] obsvValueArray = _obsvValue.split(",");
			ObsvDeviceDetection deviceDetectionObsv = new ObsvDeviceDetection(
					obsv, obsvValueArray[0], obsv.getObsvTime());

			deviceDetectionObsv.setLatitude(obsv.getLatitude());
			deviceDetectionObsv.setLongitude(obsv.getLongitude());
			deviceDetectionObsv.setSignalStrength(Integer
					.valueOf(obsvValueArray[1]));
			deviceDetectionObsv.setDevDetect(obsv);
			
			detectedDeviceList.add(deviceDetectionObsv);
		}
		
		return detectedDeviceList;
	}
	
	@Override
	public Observations saveObservation(
			SensorObservationManager sensorObsvManager, Observations obsv,
			InputStream uploadedFile, String fileName) {
		
		return saveObservation(sensorObsvManager, obsv);
	}

	@Override
	@Transactional
	public JSONArray findObservationsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc, Integer offset, Integer limit) {
		if (!SensorTypeEnum.SENSOR_TYPE_WIFI_SENSOR.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		
		ObsvDeviceDetectionDao obsvDeviceDetectionDao = (ObsvDeviceDetectionDao)sensorObsvManager.getObservationDao(SensorTypeEnum.SENSOR_TYPE_WIFI_SENSOR);
		
		List<ObsvDeviceDetection> obsvList = obsvDeviceDetectionDao.findDeviceObservationsBySensor(sensor, startDate, endDate, orderByName, isAsc, offset, limit);
		
		List<ObsvDeviceDetectionVO> obsvVOList = ObsvDeviceDetectionVO.copyCollection(obsvList);
		return new JSONArray(obsvVOList);
	}

	@Override
	public JSONArray findObservationsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Integer offset, Integer limit) {
		if (!SensorTypeEnum.SENSOR_TYPE_WIFI_SENSOR.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		
		ObsvDeviceDetectionDao obsvDeviceDetectionDao = (ObsvDeviceDetectionDao)sensorObsvManager.getObservationDao(SensorTypeEnum.SENSOR_TYPE_WIFI_SENSOR);
		
		List<ObsvDeviceDetection> obsvList = obsvDeviceDetectionDao.findDeviceObservationsBySensor(sensor, offset, limit);
		
		List<ObsvDeviceDetectionVO> obsvVOList = ObsvDeviceDetectionVO.copyCollection(obsvList);
		return new JSONArray(obsvVOList);
	}

	@Override
	public JSONArray aggregateObservationResultsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate) {
		if (!SensorTypeEnum.SENSOR_TYPE_WIFI_SENSOR.getSensorType().equals(sensor.getSensorType())) {
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
	public List<Observations> queryUnclassifiedObservations(
			SensorObservationManager sensorObsvManager, Sensors sensor) {
		if (!SensorTypeEnum.SENSOR_TYPE_WIFI_SENSOR.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}	
		return new ArrayList<Observations>();
	}

	@Override
	public Boolean migrateUnclassifiedObservations(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			List<Observations> obsvList) {
		if (!SensorTypeEnum.SENSOR_TYPE_WIFI_SENSOR.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}	
		return true;
	}
		
	@Override
	public JSONArray countSensorObsvSubCategories(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate) {
		if (!SensorTypeEnum.SENSOR_TYPE_WIFI_SENSOR.getSensorType().equals(sensor.getSensorType())) {
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

	public static void main (String[] args) {
		String obsvValue = "{\"id\":\"jQszvKOHDkvLWIc3weRVOKIi0+YG+Hletlxopqy75/k=\", \"obs\":[{\"power\": -78, \"firstTime\":1406133779048, \"lastTime\": 1406133856498}, {\"power\": -51, \"firstTime\":1406135346060, \"lastTime\": 1406133856498}]}";
	
		JSONObject obsObj = new JSONObject(obsvValue);
		
		String detectedDeviceId = obsObj.getString("id");
		System.out.println("detected wifi device id :"+ detectedDeviceId);
		
		JSONArray obsvArray = obsObj.getJSONArray("obs");
		for (int i = 0; i < obsvArray.length(); i++) {
			JSONObject json = obsvArray.getJSONObject(i);
			Integer power = json.getInt("power");
			Long firstTime = json.getLong("firstTime");
			Long lastTime = json.getLong("lastTime");
			
			Date _firstTime = new Date(firstTime);
			Date _lastTime = new Date(lastTime);
			
			System.out.println("power:"+power);
			System.out.println("firstTime:"+_firstTime);
			System.out.println("lastTime:"+_lastTime);
		}		
	}
}
