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
import org.json.JSONObject;

import uk.ac.oak.movemore.webapp.dao.ObsvVoiceDecibelDetectionDao;
import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.model.ObsvVoiceDecibelDetection;
import uk.ac.oak.movemore.webapp.model.Sensors;
import uk.ac.oak.movemore.webapp.service.SensorObservationManager;
import uk.ac.oak.movemore.webapp.service.SensorObservationsProcessor;
import uk.ac.oak.movemore.webapp.util.SensorMediaFileManager;
import uk.ac.oak.movemore.webapp.util.SensorTypeEnum;
import uk.ac.oak.movemore.webapp.vo.ObsvVoiceDecibelDetectionVO;

public class VoiceSensorObsvProcessor implements SensorObservationsProcessor {
	protected final Log log = LogFactory.getLog(VoiceSensorObsvProcessor.class);
	
	@Override
	public Observations saveObservation(
			SensorObservationManager sensorObsvManager, Observations obsv) {
		if (obsv != null && obsv.getSensor() != null) {
			if (obsv.getSensor().getSensorType() != null
					&& SensorTypeEnum.SENSOR_TYPE_VOICE_SENSOR
							.getSensorType().equals(
									obsv.getSensor().getSensorType())) {
				log.info(String
						.format("process observations for Voice Decibel sensors, sensor name is {%s}, observation value is {%s}, obsv time is {%s}",
								obsv.getSensor().getName(), obsv.getValue(),
								obsv.getObsvTime()));
				try {
					
					//Double decibel = Double.valueOf(obsv.getValue());					
					JSONObject jsonObj = new JSONObject(obsv.getValue());
					Double decibel = jsonObj.getDouble("soundlevel");
					//String unit = jsonObj.getString("unit");
					
					ObsvVoiceDecibelDetection voiceDetection = new ObsvVoiceDecibelDetection (decibel, obsv.getLongitude(), obsv.getLatitude(), obsv.getObsvTime());
					
					voiceDetection.setVoiceSensor(obsv);
					obsv.setObsvVoiceDetect(voiceDetection);
					
					obsv = sensorObsvManager.save(obsv);
					
					return obsv;					
				} catch (NumberFormatException numEx) {
					log.error(String
							.format("Voice decibel sensor observation value syntax error. Expect numeric value while the actual value is %s. The exception is %s ",
									obsv.getValue(), numEx.toString()));
				}
			}
		}
		return null;
	}
	
	@Override
	public Observations saveObservation(
			SensorObservationManager sensorObsvManager, Observations obsv,
			InputStream uploadedFile, String fileName) {
		
		if (obsv != null && obsv.getSensor() != null) {
			if (obsv.getSensor().getSensorType() != null
					&& SensorTypeEnum.SENSOR_TYPE_VOICE_SENSOR
							.getSensorType().equals(
									obsv.getSensor().getSensorType())) {
				log.info(String
						.format("process observations for Voice Decibel sensors, sensor name is {%s}, observation value is {%s}, obsv time is {%s}",
								obsv.getSensor().getName(), obsv.getValue(),
								obsv.getObsvTime()));
				try {
					
					//Double decibel = Double.valueOf(obsv.getValue());					
					JSONObject jsonObj = new JSONObject(obsv.getValue());
					Double decibel = jsonObj.getDouble("soundlevel");
					//String unit = jsonObj.getString("unit");
					
					ObsvVoiceDecibelDetection voiceDetection = new ObsvVoiceDecibelDetection (decibel, obsv.getLongitude(), obsv.getLatitude(), obsv.getObsvTime());
					
					String fileRelativePath = "";
					try {
						SensorMediaFileManager fileManager = new SensorMediaFileManager();
						fileRelativePath = fileManager.onSumbit(uploadedFile, fileName, obsv.getSensor().getDevice().getDevicePhysicalId(), obsv.getSensor().getSensorPhysicalId());
					} catch (IOException e) {
						log.error("Failure when attempting to store the media file.");
					}
					
					voiceDetection.setAttachment(fileRelativePath);
					obsv.setAttachment(fileRelativePath);
					
					voiceDetection.setVoiceSensor(obsv);
					obsv.setObsvVoiceDetect(voiceDetection);
					
					obsv = sensorObsvManager.save(obsv);
					
					return obsv;					
				} catch (NumberFormatException numEx) {
					log.error(String
							.format("Voice decibel sensor observation value syntax error. Expect numeric value while the actual value is %s. The exception is %s ",
									obsv.getValue(), numEx.toString()));
				}
			}
		}
		
		return null;
	}

	@Override
	public JSONArray findObservationsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc, Integer offset, Integer limit) {
		if (!SensorTypeEnum.SENSOR_TYPE_VOICE_SENSOR.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		
		ObsvVoiceDecibelDetectionDao voiceDetectionDao = (ObsvVoiceDecibelDetectionDao)sensorObsvManager.getObservationDao(SensorTypeEnum.SENSOR_TYPE_VOICE_SENSOR);
		List<ObsvVoiceDecibelDetection> obsvList = voiceDetectionDao.findPeopleCounterObservationsBySensor(sensor, startDate, endDate, orderByName, isAsc, offset, limit);
		
		List<ObsvVoiceDecibelDetectionVO> obsvVOList =  ObsvVoiceDecibelDetectionVO.copyCollection(obsvList);
		
		return new JSONArray(obsvVOList);
	}

	@Override
	public JSONArray findObservationsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Integer offset, Integer limit) {
		if (!SensorTypeEnum.SENSOR_TYPE_VOICE_SENSOR.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		
		ObsvVoiceDecibelDetectionDao voiceDetectionDao = (ObsvVoiceDecibelDetectionDao)sensorObsvManager.getObservationDao(SensorTypeEnum.SENSOR_TYPE_VOICE_SENSOR);
		List<ObsvVoiceDecibelDetection> obsvList = voiceDetectionDao.findPeopleCounterObservationsBySensor(sensor, offset, limit);
		
		List<ObsvVoiceDecibelDetectionVO> obsvVOList =  ObsvVoiceDecibelDetectionVO.copyCollection(obsvList);
		return new JSONArray(obsvVOList);
	}

	@Override
	public JSONArray aggregateObservationResultsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate) {
		if (!SensorTypeEnum.SENSOR_TYPE_VOICE_SENSOR.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}		
		
		return new JSONArray();
	}
	
	@Override
	public Boolean migrateUnclassifiedObservations(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			List<Observations> obsvList) {
		if (!SensorTypeEnum.SENSOR_TYPE_VOICE_SENSOR.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		return true;
	}

	@Override
	public List<Observations> queryUnclassifiedObservations(
			SensorObservationManager sensorObsvManager, Sensors sensor) {
		if (!SensorTypeEnum.SENSOR_TYPE_VOICE_SENSOR.getSensorType().equals(sensor.getSensorType())) {
			return null;
		}
		return new ArrayList<Observations>();
	}

	@Override
	public JSONArray countSensorObsvSubCategories(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate) {
		if (!SensorTypeEnum.SENSOR_TYPE_VOICE_SENSOR.getSensorType().equals(sensor.getSensorType())) {
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
