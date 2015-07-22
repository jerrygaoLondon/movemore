package uk.ac.oak.movemore.webapp.service.impl;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lt.overdrive.trackparser.domain.Track;
import lt.overdrive.trackparser.domain.TrackPoint;
import lt.overdrive.trackparser.domain.Trail;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.oak.movemore.webapp.dao.ObsvActivityDetectionDao;
import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.model.ObsvActivityDetection;
import uk.ac.oak.movemore.webapp.model.ObsvActivityNorm;
import uk.ac.oak.movemore.webapp.model.Sensors;
import uk.ac.oak.movemore.webapp.service.SensorObservationManager;
import uk.ac.oak.movemore.webapp.service.SensorObservationsProcessor;
import uk.ac.oak.movemore.webapp.util.DateUtil;
import uk.ac.oak.movemore.webapp.util.SensorTypeEnum;
import uk.ac.oak.movemore.webapp.util.StandardDateFormatter;
import uk.ac.oak.movemore.webapp.vo.ObsvActivityDetectionVO;

public class ActivitySensorObsvProcessor implements SensorObservationsProcessor {
	protected final Log log = LogFactory
			.getLog(ActivitySensorObsvProcessor.class);

	@Override
	public Observations saveObservation(
			SensorObservationManager sensorObsvManager, Observations obsv) throws JSONException, Exception {
		if (obsv.getSensor().getSensorType() != null
				&& SensorTypeEnum.SENSOR_TYPE_ACTIVITY_SENSOR.getSensorType()
						.equals(obsv.getSensor().getSensorType())) {
			log.info(String
					.format("process observations for Activity sensors, sensor name is {%s}, observation value is {%s}, obsv time is {%s}",
							obsv.getSensor().getName(), obsv.getValue(),
							obsv.getObsvTime()));
			try {
				JSONObject jsonObj = new JSONObject(obsv.getValue());				
				
				Integer stepsDoneToday = jsonObj.getInt("stepsDoneToday");
				
				JSONObject activityObj = jsonObj.getJSONObject("activityDescription");
				Integer activityType = activityObj.getInt("type");
				Long duration = activityObj.getLong("duration");				
				String obsvTimeStr = activityObj.getString("time");
				Date obsvTime = StandardDateFormatter.parse(obsvTimeStr);
				Double confidence = activityObj.getDouble("confidence");
				
				Integer floorClimbed=jsonObj.getInt("floorClimbed");				
				
				Double longitude = null;
				Double latitude = null;
				Double locAccuracy = null;
				
				JSONObject locObj = jsonObj.getJSONObject("activityLocation");
				locAccuracy=locObj.getDouble("accuracy");
				longitude = jsonObj.getDouble("longitude");
				latitude = jsonObj.getDouble("latitude");

				ObsvActivityDetection obsvActivityDetection = new ObsvActivityDetection(
						longitude, latitude, obsvTime);
				obsvActivityDetection.setFloorClimbed(floorClimbed);
				obsvActivityDetection.setActivityType(activityType);
				obsvActivityDetection.setConfidence(confidence);
				obsvActivityDetection.setStepsDoneToday(stepsDoneToday);
				obsvActivityDetection.setDuration(duration);
				obsvActivityDetection.setLocAccuracy(locAccuracy);

				obsv.setLatitude(latitude);
				obsv.setLongitude(longitude);
				
				obsvActivityDetection.setActivitySensor(obsv);
				obsv.setObsvActivityDetect(obsvActivityDetection);

				obsv = sensorObsvManager.save(obsv);
				return obsv;

			} catch(JSONException jsonEx) { 
				log.error(String
						.format("JSON Data Error in processing observations for Activity sensors, sensor id is {%s}, sensor physical id is: {%s} sensor name is {%s}, observation value is {%s}, obsv time is {%s}",
								obsv.getSensor().getSensorId(), obsv
										.getSensor().getSensorPhysicalId(),
								obsv.getSensor().getName(), obsv.getValue(),
								obsv.getObsvTime()));
				log.error(jsonEx.toString());
				throw jsonEx;
			} catch (Exception ex) {
				log.error(String
						.format("Error in processing observations for Activity sensors, sensor id is {%s}, sensor physical id is: {%s} sensor name is {%s}, observation value is {%s}, obsv time is {%s}",
								obsv.getSensor().getSensorId(), obsv
										.getSensor().getSensorPhysicalId(),
								obsv.getSensor().getName(), obsv.getValue(),
								obsv.getObsvTime()));
				log.error(ex.toString());
				throw ex;
			}
		}
		return null;
	}

	/**
	 * Bulk upload tcx/gps training activity sensor data
	 * 
	 * @param trail
	 */
	public void saveObservations(Trail trail, Sensors sensor,
			SensorObservationManager sensorObsvManager) {
		List<Track> trackSet = trail.getTracks();
		log.info(String.format("Start to process [%s] activity track data",
				trackSet == null ? 0 : trackSet.size()));

		Observations obsv;
		for (Track track : trackSet) {
			List<TrackPoint> trackPoints = track.getPoints();

			for (TrackPoint tp : trackPoints) {

				Double latitude = tp.getLatitude();
				Double longitude = tp.getLongitude();
				Float speed = tp.getSpeed();
				// UTC time
				org.joda.time.DateTime trkTime = tp.getTime();
				Timestamp obsvTime = new Timestamp(trkTime.getMillis());

				obsv = new Observations(sensor, tp.toString(), obsvTime);
				obsv.setLatitude(latitude);
				obsv.setLongitude(longitude);

				ObsvActivityDetection obsvActivityDetection = new ObsvActivityDetection(
						longitude, latitude, obsvTime);
				obsvActivityDetection.setActivityType(8);
				if (speed != null) {
					obsvActivityDetection.setSpeed(Double.valueOf(speed));
				}
				obsvActivityDetection.setActivitySensor(obsv);
				obsv.setObsvActivityDetect(obsvActivityDetection);

				sensorObsvManager.save(obsv);
			}
		}
		log.info(String.format(
				"Bulk uploading for activity sensor [%s] completed!",
				sensor.getSensorPhysicalId()));
		// example code to extract gpx unique elements
		// java.lang.reflect.Field fromListField =
		// track.getPoints().getClass().getDeclaredField("fromList");
		// fromListField.setAccessible(true);
		// List<WptType> fromlist =
		// (List<WptType>)fromListField.get(track.getPoints());
		// for(WptType wptType : fromlist) {
		// System.out.println(wptType.getLon()+","+wptType.getLat()+","+wptType.getTime());
		// }
	}

	public static void main(String[] args) {
		// String obsvValue =
		// "{\"activity\":\"Still\",\"time\":\"2014-07-14 17:40:24.903\",\"finalActivity\":\"Still\",\"confidence\":77,\"longitude\":-1.4803285,\"latitude\":53.3807484,\"locAccuracy\":14.142,\"id\":0,\"speed\":0.0,\"bearing\":0.0}";

		String obsvValue1 = "{\"activity\":\"In Vehicle\",\"finalActivity\":\"In Vehicle\",\"time\":1405597621816,\"longitude\":0.0,\"latitude\":0.0,\"id\":0,\"locAccuracy\":0.0,\"bearing\":0.0,\"speed\":12.5,\"confidence\":77}";

		JSONObject jsonObj = new JSONObject(obsvValue1);
		String activityType = jsonObj.getString("finalActivity");
		Double confidence = jsonObj.getDouble("confidence");
		Double longitude = jsonObj.getDouble("longitude");
		Double latitude = jsonObj.getDouble("latitude");
		Double locAccuracy = jsonObj.getDouble("locAccuracy");
		Double speed = jsonObj.getDouble("speed");
		Timestamp timeStamp = new Timestamp(jsonObj.getLong("time"));

		System.out
				.println(String
						.format("sensor reading is activityType[%s], confidence[%s], longitude[%s], latitude[%s], locAccuracy[%s], speed[%s] timeStamp[%s]",
								activityType, confidence, longitude, latitude,
								locAccuracy, speed, timeStamp));
	}

	/*
	 * String[] activityTypes = {"Unknown", "On Foot", "On Bicycle", "Still",
	 * "Running", "Tilting", "Walking", "In Vehicle"}; private String[]
	 * valueExtraction (String input) { String[] activityValues = new String[2];
	 * 
	 * for (String activityType : activityTypes) {
	 * if(input.startsWith(activityType)) { activityValues[0] = activityType;
	 * 
	 * activityValues[1] = input.replaceAll("[A-Za-z]", "").trim(); } } return
	 * activityValues; }
	 */

	@Override
	public JSONArray findObservationsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc,
			Integer offset, Integer limit) {
		if (!SensorTypeEnum.SENSOR_TYPE_ACTIVITY_SENSOR.getSensorType().equals(
				sensor.getSensorType())) {
			return null;
		}

		ObsvActivityDetectionDao obsvActivityDetectionDao = (ObsvActivityDetectionDao) sensorObsvManager
				.getObservationDao(SensorTypeEnum.SENSOR_TYPE_ACTIVITY_SENSOR);

		// TODO: when ActivitySensor send us UTC date, remove the conversion
		List<ObsvActivityDetection> obsvList = obsvActivityDetectionDao
				.findActivityObservationsBySensor(sensor,
						DateUtil.convertUTCDateToLocalDate(startDate),
						DateUtil.convertUTCDateToLocalDate(endDate),
						orderByName, isAsc, offset, limit);

		List<ObsvActivityDetectionVO> obsvActivityDetectionVOList = ObsvActivityDetectionVO
				.copyCollection(obsvList);

		return new JSONArray(obsvActivityDetectionVOList);
	}

	@Override
	public JSONArray findObservationsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Integer offset, Integer limit) {
		if (!SensorTypeEnum.SENSOR_TYPE_ACTIVITY_SENSOR.getSensorType().equals(
				sensor.getSensorType())) {
			return null;
		}

		return findObservationsBySensor(sensorObsvManager, sensor, null, null,
				null, null, offset, limit);
	}

	@Override
	public JSONArray aggregateObservationResultsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate) {
		if (!SensorTypeEnum.SENSOR_TYPE_ACTIVITY_SENSOR.getSensorType().equals(
				sensor.getSensorType())) {
			return null;
		}

		return new JSONArray();
	}

	@Override
	public List<Observations> queryUnclassifiedObservations(
			SensorObservationManager sensorObsvManager, Sensors sensor) {
		if (!SensorTypeEnum.SENSOR_TYPE_ACTIVITY_SENSOR.getSensorType().equals(
				sensor.getSensorType())) {
			return null;
		}
		return new ArrayList<Observations>();
	}

	@Override
	public Boolean migrateUnclassifiedObservations(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			List<Observations> obsvList) {
		if (!SensorTypeEnum.SENSOR_TYPE_ACTIVITY_SENSOR.getSensorType().equals(
				sensor.getSensorType())) {
			return null;
		}

		return true;
	}

	@Override
	public Observations saveObservation(
			SensorObservationManager sensorObsvManager, Observations obsv,
			InputStream uploadedFile, String fileName) throws JSONException, Exception {

		return saveObservation(sensorObsvManager, obsv);
	}

	@Override
	public JSONArray countSensorObsvSubCategories(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate) {

		if (!SensorTypeEnum.SENSOR_TYPE_ACTIVITY_SENSOR.getSensorType().equals(
				sensor.getSensorType())) {
			return null;
		}

		JSONArray jsonResults = new JSONArray();

		ObsvActivityDetectionDao obsvActivityDetectionDao = (ObsvActivityDetectionDao) sensorObsvManager
				.getObservationDao(SensorTypeEnum.SENSOR_TYPE_ACTIVITY_SENSOR);

		List<Object[]> countRes = obsvActivityDetectionDao
				.countSensorObsvSubCategories(sensor, startDate, endDate);

		for (Object[] resArr : countRes) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(String.valueOf(resArr[0]), String.valueOf(resArr[1]));
			jsonResults.put(jsonObj);
		}

		return jsonResults;
	}

	@Override
	public Boolean normaliseObservation(
			SensorObservationManager sensorObsvManager, Long obsvId,
			Map<String, String> normalisationValues) {

		ObsvActivityDetectionDao obsvActivityDao = (ObsvActivityDetectionDao) sensorObsvManager
				.getObservationDao(SensorTypeEnum.SENSOR_TYPE_ACTIVITY_SENSOR);

		ObsvActivityDetection obsvActivityDetection = obsvActivityDao
				.get(obsvId);

		String finalActivityType = normalisationValues.get("finalActivityType");
		String finalLatitude = normalisationValues.get("finalLatitude");
		String finalLongitude = normalisationValues.get("finalLongitude");
		String finalConfidence = normalisationValues.get("finalConfidence");

		if (!obsvActivityDetection.getActivityType().equals(finalActivityType)
				|| !isSameLocation(obsvActivityDetection, finalLatitude,
						finalLongitude)) {
			Double finalLat = Double.valueOf(finalLatitude == null ? "0.0" : finalLatitude);
			Double finalLog = Double.valueOf(finalLongitude == null ? "0.0" : finalLongitude);
			Double finalConf = Double.valueOf(finalConfidence == null ? "0.0" : finalConfidence);
			
			if (obsvActivityDetection.getObsvActivityNorm() != null) {
				if (StringUtils.isNotEmpty(finalActivityType)) {
					obsvActivityDetection.getObsvActivityNorm().setFinalActivityType(Integer.valueOf(finalActivityType));
				}
				if (StringUtils.isNotEmpty(finalLatitude)) {
					obsvActivityDetection.getObsvActivityNorm().setFinalLatitude(finalLat);
				}
				if (StringUtils.isNotEmpty(finalLongitude)) {
					obsvActivityDetection.getObsvActivityNorm().setFinalLongitude(finalLog);
				}
				if (StringUtils.isNotEmpty(finalConfidence)) {
					obsvActivityDetection.getObsvActivityNorm().setFinalConfidence(finalConf);
				}				
			} else {
				ObsvActivityNorm obsvActivityNorm = new ObsvActivityNorm(finalLat, finalLog, Integer.valueOf(finalActivityType), finalConf);
				
				obsvActivityDetection.setObsvActivityNorm(obsvActivityNorm);
				obsvActivityNorm.setActivityObsv(obsvActivityDetection);
			}
			
			obsvActivityDao.save(obsvActivityDetection);
			
		}

		return true;
	}

	private boolean isSameLocation(ObsvActivityDetection obsvActivityDetection,
			String finalLatitude, String finalLongitude) {
		return obsvActivityDetection.getLatitude().equals(
				Float.valueOf(finalLatitude))
				&& obsvActivityDetection.getLongitude().equals(finalLongitude);
	}
}
