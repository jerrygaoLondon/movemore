package uk.ac.oak.movemore.webapp.service.impl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.oak.movemore.webapp.dao.ObsvOBDDetectionDao;
import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.model.ObsvOBDDetection;
import uk.ac.oak.movemore.webapp.model.Sensors;
import uk.ac.oak.movemore.webapp.service.SensorObservationManager;
import uk.ac.oak.movemore.webapp.service.SensorObservationsProcessor;
import uk.ac.oak.movemore.webapp.util.OBDDataUnitEnum;
import uk.ac.oak.movemore.webapp.util.SensorTypeEnum;
import uk.ac.oak.movemore.webapp.vo.ObsvOBDDetectionVO;

public class OBDCarSpeedSensorObsvProcessor implements
		SensorObservationsProcessor {
	protected final Log log = LogFactory
			.getLog(OBDCarSpeedSensorObsvProcessor.class);

	@Override
	public Observations saveObservation(
			SensorObservationManager sensorObsvManager, Observations obsv)
			throws JSONException {
		if (obsv != null && obsv.getSensor() != null) {
			if (obsv.getSensor().getSensorType() != null
					&& SensorTypeEnum.SENSOR_TYPE_OBD_CAR_SPEED_SENSOR
							.getSensorType().equals(
									obsv.getSensor().getSensorType())) {
				log.info(String
						.format("process observations for OBD Car speed sensors, sensor name is {%s}, observation value is {%s}, obsv time is {%s}",
								obsv.getSensor().getName(), obsv.getValue()
										.substring(0, 110) + "...",
								obsv.getObsvTime()));

				ObsvOBDDetection obsvOBDDetection = new ObsvOBDDetection(
						obsv.getLongitude(), obsv.getLatitude(),
						obsv.getObsvTime());
				try {
					if (isJSONArray(obsv.getValue())) {
						decodeAndSetOBDJSONArrayParameters(obsvOBDDetection,
								obsv.getValue());
					} else if (isJSONObject(obsv.getValue())) {
						decodeAndSetOBDJSONObjectParameters(obsvOBDDetection,
								obsv, obsv.getValue());
					} else {
						log.error("Unexpected Error!!! OBD sensor observation value syntax error. Expect JSON Object (or previous JSON Array format) while actual value is ["
								+ obsv.getValue() + "]");
						return null;
					}

					obsvOBDDetection.setObdSensor(obsv);
					obsv.setObsvOBDDetect(obsvOBDDetection);

					obsv = sensorObsvManager.save(obsv);
					return obsv;

				} catch (JSONException jsonEx) {
					log.error(String
							.format("OBD sensor observation value syntax error. Expect JSON Array while the actual value is %s. The exception is %s ",
									obsv.getValue(), jsonEx.toString()));
					throw jsonEx;
				}
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

	String decodeOBDParameterErrorMsgTemplate = "Error while processing OBD parameter [%s], the value is %s. The exception: %s";

	public void decodeAndSetOBDJSONArrayParameters(
			ObsvOBDDetection obsvOBDDetection, String obsvValue) {
		Double fuelEconomy = null;
		Double vehicleSpeed = null;
		Double ambientAirTemperature = null;
		Double engineRPM = null;
		Double fuelLevel = null;

		Double engineCoolantTemperature = null;
		BigDecimal engineLoad = null;
		Time engineRuntime = null;
		Double barometricPressure = null;
		Double fuelConsumption = null;
		Double fuelPressure = null;
		Double massAirFlow = null;
		Double intakeManifoldPressure = null;

		JSONArray jsonArr = new JSONArray(obsvValue);

		for (int i = 0; i < jsonArr.length(); i++) {
			JSONObject json = jsonArr.getJSONObject(i);
			String name = json.getString("key");
			String value = json.getString("value");

			if (fuelEconomy == null
					&& OBDDataUnitEnum.OBD_DATA_FUEL_ECONOMY.getType().equals(
							name)) {
				try {
					fuelEconomy = Double.valueOf(value
							.replaceAll(
									OBDDataUnitEnum.OBD_DATA_FUEL_ECONOMY
											.getUnit(), "").trim());
				} catch (Exception ex) {
					log.error(String.format(decodeOBDParameterErrorMsgTemplate,
							name, value, ex.toString()));
				}
			}

			if (vehicleSpeed == null
					&& OBDDataUnitEnum.OBD_DATA_VEHICLE_SPEED.getType().equals(
							name)) {
				try {
					vehicleSpeed = Double.valueOf(value.replaceAll(
							OBDDataUnitEnum.OBD_DATA_VEHICLE_SPEED.getUnit(),
							"").trim());
				} catch (Exception ex) {
					log.error(String.format(decodeOBDParameterErrorMsgTemplate,
							name, value, ex.toString()));
				}
			}

			if (ambientAirTemperature == null
					&& OBDDataUnitEnum.OBD_DATA_AMBIENT_AIR_TEMPERATURE
							.getType().equals(name)) {
				try {
					ambientAirTemperature = Double.valueOf(value.replaceAll(
							OBDDataUnitEnum.OBD_DATA_AMBIENT_AIR_TEMPERATURE
									.getUnit(), "").trim());
				} catch (Exception ex) {
					log.error(String.format(decodeOBDParameterErrorMsgTemplate,
							name, value, ex.toString()));
				}
			}

			if (engineRPM == null
					&& OBDDataUnitEnum.OBD_DATA_ENGINE_RPM.getType().equals(
							name)) {
				try {
					engineRPM = Double.valueOf(value.replaceAll(
							OBDDataUnitEnum.OBD_DATA_ENGINE_RPM.getUnit(), "")
							.trim());
				} catch (Exception ex) {
					log.error(String.format(decodeOBDParameterErrorMsgTemplate,
							name, value, ex.toString()));
				}
			}

			if (fuelLevel == null
					&& OBDDataUnitEnum.OBD_DATA_FUEL_LEVEL.getType().equals(
							name)) {
				try {
					fuelLevel = Double.valueOf(value.replaceAll(
							OBDDataUnitEnum.OBD_DATA_FUEL_LEVEL.getUnit(), "")
							.trim());
				} catch (Exception ex) {
					log.error(String.format(decodeOBDParameterErrorMsgTemplate,
							name, value, ex.toString()));
				}
			}

			if (engineCoolantTemperature == null
					&& OBDDataUnitEnum.OBD_DATA_ENGINE_COOLANT_TEMPERATURE
							.getType().equals(name)) {
				try {
					engineCoolantTemperature = Double.valueOf(value.replaceAll(
							OBDDataUnitEnum.OBD_DATA_ENGINE_COOLANT_TEMPERATURE
									.getUnit(), "").trim());
				} catch (Exception ex) {
					log.error(String.format(decodeOBDParameterErrorMsgTemplate,
							name, value, ex.toString()));
				}
			}

			if (engineLoad == null
					&& OBDDataUnitEnum.OBD_DATA_ENGINE_LOAD.getType().equals(
							name)) {
				try {
					engineLoad = BigDecimal.valueOf(Double.valueOf(value
							.replaceAll(
									OBDDataUnitEnum.OBD_DATA_ENGINE_LOAD
											.getUnit(), "").trim()));
				} catch (Exception ex) {
					log.error(String.format(decodeOBDParameterErrorMsgTemplate,
							name, value, ex.toString()));
				}
			}

			if (engineRuntime == null
					&& OBDDataUnitEnum.OBD_DATA_Engine_RUNTIME.getType()
							.equals(name)) {
				try {
					engineRuntime = Time.valueOf(value);
				} catch (Exception ex) {
					log.error(String.format(decodeOBDParameterErrorMsgTemplate,
							name, value, ex.toString()));
				}
			}

			if (barometricPressure == null
					&& OBDDataUnitEnum.OBD_DATA_BAROMETRIC_PRESSURE.getType()
							.equals(name)) {
				try {
					barometricPressure = Double.valueOf(value.replaceAll(
							OBDDataUnitEnum.OBD_DATA_BAROMETRIC_PRESSURE
									.getUnit(), "").trim());
				} catch (Exception ex) {
					log.error(String.format(decodeOBDParameterErrorMsgTemplate,
							name, value, ex.toString()));
				}
			}

			if (fuelConsumption == null
					&& OBDDataUnitEnum.OBD_DATA_FUEL_CONSUMPTION.getType()
							.equals(name)) {
				try {
					fuelConsumption = Double.valueOf(value
							.replaceAll(
									OBDDataUnitEnum.OBD_DATA_FUEL_CONSUMPTION
											.getUnit(), "").trim());
				} catch (Exception ex) {
					log.error(String.format(decodeOBDParameterErrorMsgTemplate,
							name, value, ex.toString()));
				}
			}

			if (fuelPressure == null
					&& OBDDataUnitEnum.OBD_DATA_FUEL_PRESSURE.getType().equals(
							name)) {
				try {
					fuelPressure = Double.valueOf(value.replaceAll(
							OBDDataUnitEnum.OBD_DATA_FUEL_PRESSURE.getUnit(),
							"").trim());
				} catch (Exception ex) {
					log.error(String.format(decodeOBDParameterErrorMsgTemplate,
							name, value, ex.toString()));
				}
			}

			if (massAirFlow == null
					&& OBDDataUnitEnum.OBD_DATA_MASS_AIR_FLOW.getType().equals(
							name)) {
				try {
					massAirFlow = Double.valueOf(value.replaceAll(
							OBDDataUnitEnum.OBD_DATA_MASS_AIR_FLOW.getUnit(),
							"").trim());
				} catch (Exception ex) {
					log.error(String.format(decodeOBDParameterErrorMsgTemplate,
							name, value, ex.toString()));
				}
			}

			if (intakeManifoldPressure == null
					&& OBDDataUnitEnum.OBD_DATA_INTAKE_MANIFOLD_PRESSURE
							.getType().equals(name)) {
				try {
					intakeManifoldPressure = Double.valueOf(value.replaceAll(
							OBDDataUnitEnum.OBD_DATA_INTAKE_MANIFOLD_PRESSURE
									.getUnit(), "").trim());
				} catch (Exception ex) {
					log.error(String.format(decodeOBDParameterErrorMsgTemplate,
							name, value, ex.toString()));
				}
			}
		}

		obsvOBDDetection.setAmbientAirTemperature(ambientAirTemperature);
		obsvOBDDetection.setEngineRPM(engineRPM);
		obsvOBDDetection.setFuelEconomy(fuelEconomy);
		obsvOBDDetection.setFuelLevel(fuelLevel);
		obsvOBDDetection.setVehicleSpeed(vehicleSpeed);

		obsvOBDDetection.setEngineCoolantTemperature(engineCoolantTemperature);
		obsvOBDDetection.setEngineLoad(engineLoad);
		obsvOBDDetection.setEngineRuntime(engineRuntime);
		obsvOBDDetection.setBarometricPressure(barometricPressure);
		obsvOBDDetection.setFuelConsumption(fuelConsumption);
		obsvOBDDetection.setFuelPressure(fuelPressure);
		obsvOBDDetection.setMassAirFlow(massAirFlow);
		obsvOBDDetection.setIntakeManifoldPressure(intakeManifoldPressure);
	}

	public void decodeAndSetOBDJSONObjectParameters(
			ObsvOBDDetection obsvOBDDetection, Observations obsv,
			String obsvValue) {
		JSONObject jsonObj = new JSONObject(obsvValue);

		Date obsvTime = new Timestamp(jsonObj.getLong("timestamp"));
		Double longitude = jsonObj.getDouble("longitude");
		Double latitude = jsonObj.getDouble("latitude");

		Double rpmValue = null;
		String rpmUnit = null;
		Double speedValue = null;
		String speedUnit = null;
		Double temperValue = null;
		String temperUnit = null;

		JSONObject readingsObj = (JSONObject) jsonObj.get("readings");
		// JSONObject rpmObj;
		// if (!readingsObj.isNull("rpm")) {
		// rpmObj = (JSONObject) readingsObj.get("rpm");
		// rpmValue = Double.valueOf(String.valueOf(rpmObj.get("value")));
		// rpmUnit = rpmObj.getString("unit");
		// }
		//
		// if
		// (!readingsObj.isNull(OBDDataUnitEnum.OBD_DATA_ENGINE_RPM.getType()))
		// {
		// rpmObj = (JSONObject)
		// readingsObj.get(OBDDataUnitEnum.OBD_DATA_ENGINE_RPM.getType());
		// rpmValue = Double.valueOf(String.valueOf(rpmObj.getString("value")));
		// rpmUnit = rpmObj.getString("unit");
		// }

		JSONObject speedObj;
		if (!readingsObj.isNull("speed")) {
			speedObj = (JSONObject) readingsObj.get("speed");
			try {
				speedValue = speedObj.getDouble("value");
			} catch (Exception de) {
				log.error(String.format(decodeOBDParameterErrorMsgTemplate,
						"speed", speedObj.get("value"), de.toString()));
			}
			speedUnit = speedObj.getString("unit");
		}

		if (!readingsObj.isNull(OBDDataUnitEnum.OBD_DATA_VEHICLE_SPEED
				.getType())) {
			speedObj = (JSONObject) readingsObj
					.get(OBDDataUnitEnum.OBD_DATA_VEHICLE_SPEED.getType());
			try {
				speedValue = speedObj.getDouble("value");
			} catch (Exception de) {
				log.error(String.format(decodeOBDParameterErrorMsgTemplate,
						OBDDataUnitEnum.OBD_DATA_VEHICLE_SPEED.getType(),
						speedObj.get("value"), de.toString()));
			}
			speedUnit = speedObj.getString("unit");
		}

		// JSONObject temperObj;

		// if (!readingsObj.isNull("temperature")) {
		// temperObj = (JSONObject) readingsObj.get("temperature");
		// try {
		// temperValue = Double.valueOf(String.valueOf(temperObj.get("value")));
		// } catch (Exception de) {
		// log.error(String.format(decodeOBDParameterErrorMsgTemplate,
		// "temperature", temperObj.get("value"), de.toString()));
		// }
		// temperUnit = temperObj.getString("unit");
		// }
		//
		// if
		// (!readingsObj.isNull(OBDDataUnitEnum.OBD_DATA_AMBIENT_AIR_TEMPERATURE.getType()))
		// {
		// temperObj = (JSONObject)
		// readingsObj.get(OBDDataUnitEnum.OBD_DATA_AMBIENT_AIR_TEMPERATURE.getType());
		// try {
		// temperValue = Double.valueOf(String.valueOf(temperObj.get("value")));
		// } catch (Exception de) {
		// log.error(String.format(decodeOBDParameterErrorMsgTemplate,
		// OBDDataUnitEnum.OBD_DATA_AMBIENT_AIR_TEMPERATURE.getType(),
		// temperObj.get("value"), de.toString()));
		// }
		// temperUnit = temperObj.getString("unit");
		// }

		obsvOBDDetection.setAmbientAirTemperature(temperValue);
		obsvOBDDetection.setVehicleSpeed(speedValue);
		obsvOBDDetection.setEngineRPM(rpmValue);
		obsvOBDDetection.setLatitude(latitude);
		obsvOBDDetection.setLongitude(longitude);
		obsvOBDDetection.setObsvTime(obsvTime);

		obsv.setLatitude(latitude);
		obsv.setLongitude(longitude);
		obsv.setObsvTime(obsvTime);
	}

	public static void main(String[] args) {
		// String value =
		// "[{\"name\":\"Fuel Economy\",\"value\":\"-1.0 l/100km\"},{\"name\":\"Engine RPM\",\"value\":\"899 RPM\"},{\"name\":\"Fuel Level\",\"value\":\"0.0%\"},{\"name\":\"Ambient Air Temperature\",\"value\":\"25C\"},{\"name\":\"Vehicle Speed\",\"value\":\"4km/h\"},{\"name\":\"Fuel Economy\",\"value\":\"-1.0 l/100km\"},{\"name\":\"Engine RPM\",\"value\":\"901 RPM\"},{\"name\":\"Fuel Level\",\"value\":\"0.0%\"},{\"name\":\"Ambient Air Temperature\",\"value\":\"25C\"},{\"name\":\"Vehicle Speed\",\"value\":\"0km/h\"},{\"name\":\"Fuel Economy\",\"value\":\"-1.0 l/100km\"},{\"name\":\"Engine RPM\",\"value\":\"899 RPM\"},{\"name\":\"Fuel Level\",\"value\":\"0.0%\"},{\"name\":\"Ambient Air Temperature\",\"value\":\"25C\"},{\"name\":\"Vehicle Speed\",\"value\":\"0km/h\"},{\"name\":\"Fuel Economy\",\"value\":\"-1.0 l/100km\"},{\"name\":\"Engine RPM\",\"value\":\"898 RPM\"}]";
		// String valueArray =
		// "[{\"key\":\"Engine RPM\",\"value\":\"1374 RPM\",\"time\":\"2014-07-25 11:13:17.525\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Engine Load\",\"value\":\"16.9%\",\"time\":\"2014-07-25 11:13:17.732\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Engine Runtime\",\"value\":\"00:00:00\",\"time\":\"2014-07-25 11:13:18.289\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Ambient Air Temperature\",\"value\":\"0C\",\"time\":\"2014-07-25 11:13:18.587\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Barometric Pressure\",\"value\":\"0kPa\",\"time\":\"2014-07-25 11:13:18.858\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Engine Coolant Temperature\",\"value\":\"87C\",\"time\":\"2014-07-25 11:13:19.063\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Fuel Level\",\"value\":\"0.0%\",\"time\":\"2014-07-25 11:13:19.638\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Fuel Economy\",\"value\":\"-1.0 l/100km\",\"time\":\"2014-07-25 11:13:19.93\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Fuel Consumption\",\"value\":\"-1.0\",\"time\":\"2014-07-25 11:13:20.223\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Fuel Pressure\",\"value\":\"0kPa\",\"time\":\"2014-07-25 11:13:20.501\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Mass Air Flow\",\"value\":\"8.24g/s\",\"time\":\"2014-07-25 11:13:20.711\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Intake Manifold Pressure\",\"value\":\"101kPa\",\"time\":\"2014-07-25 11:13:20.923\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Vehicle Speed\",\"value\":\"6km/h\",\"time\":\"2014-07-25 11:13:23.154\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Engine RPM\",\"value\":\"819 RPM\",\"time\":\"2014-07-25 11:13:23.36\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Engine Load\",\"value\":\"50.6%\",\"time\":\"2014-07-25 11:13:23.565\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Engine Runtime\",\"value\":\"00:00:00\",\"time\":\"2014-07-25 11:13:24.156\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Ambient Air Temperature\",\"value\":\"0C\",\"time\":\"2014-07-25 11:13:24.442\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Barometric Pressure\",\"value\":\"0kPa\",\"time\":\"2014-07-25 11:13:24.733\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Engine Coolant Temperature\",\"value\":\"87C\",\"time\":\"2014-07-25 11:13:24.938\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Fuel Level\",\"value\":\"0.0%\",\"time\":\"2014-07-25 11:13:25.531\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Fuel Economy\",\"value\":\"-1.0 l/100km\",\"time\":\"2014-07-25 11:13:25.831\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Fuel Consumption\",\"value\":\"-1.0\",\"time\":\"2014-07-25 11:13:26.124\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Fuel Pressure\",\"value\":\"0kPa\",\"time\":\"2014-07-25 11:13:26.414\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Mass Air Flow\",\"value\":\"25.38g/s\",\"time\":\"2014-07-25 11:13:26.62\",\"lon\":-1.4910115,\"lat\":53.3810097},{\"key\":\"Intake Manifold Pressure\",\"value\":\"118kPa\",\"time\":\"2014-07-25 11:13:26.825\",\"lon\":-1.4910115,\"lat\":53.3810097}]";

		// System.out
		// .println(String
		// .format("process observations for OBD Car speed sensors, observation value is {%s}",
		// value.substring(0, 110) + "..."));
		//
		// JSONArray jsonArr = new JSONArray(value);
		// for (int i = 0; i < jsonArr.length(); i++) {
		// JSONObject json = jsonArr.getJSONObject(i);
		// System.out.println(json.getString("key") + ","
		// + json.getString("value"));
		// }
		// // System.out.println(jsonArr.toString());
		//
		// System.out.println("Fuel Pressure".toUpperCase());
		String value = "{\"timestamp\":1411202519298,\"readings\":{\"rpm\":{\"value\":\"1762\",\"unit\":\"RPM\"},\"speed\":{\"value\":\"30\",\"unit\":\"C\"},\"temperature\":{\"value\":\"18.0\",\"unit\":\"18.0\"}},\"longitude\":-1.493503,\"latitude\":53.371209,\"sensorId\":\"356843053521889-OBD\",\"vin\":\"\"}";

		JSONObject jsonObj = new JSONObject(value);
		Date obsvTime = new Timestamp(jsonObj.getLong("timestamp"));
		Double longitude = jsonObj.getDouble("longitude");
		Double latitude = jsonObj.getDouble("latitude");

		JSONObject readingsObj = (JSONObject) jsonObj.get("readings");

		JSONObject rpmObj = (JSONObject) readingsObj.get("rpm");
		String rpmValue = rpmObj.getString("value");
		String rpmUnit = rpmObj.getString("unit");

		JSONObject speedObj = (JSONObject) readingsObj.get("speed");
		String speedValue = speedObj.getString("value");
		String speedUnit = speedObj.getString("unit");

		JSONObject temperObj = (JSONObject) readingsObj.get("temperature");
		String temperValue = temperObj.getString("value");
		String temperUnit = temperObj.getString("unit");

		System.out.println(obsvTime);
		System.out.println(longitude);
		System.out.println(latitude);

		System.out.println(rpmValue);
		System.out.println(speedValue);
		System.out.println(temperValue);

	}

	private boolean isJSONArray(String jsonStr) {
		try {
			JSONArray jarray = new JSONArray(jsonStr);
		} catch (JSONException ex) {
			return false;
		}
		return true;
	}

	private boolean isJSONObject(String jsonStr) {
		try {
			JSONObject jObject = new JSONObject(jsonStr);
		} catch (JSONException ex) {
			return false;
		}
		return true;
	}

	@Override
	public JSONArray findObservationsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc,
			Integer offset, Integer limit) {
		if (!SensorTypeEnum.SENSOR_TYPE_OBD_CAR_SPEED_SENSOR.getSensorType()
				.equals(sensor.getSensorType())) {
			return null;
		}

		ObsvOBDDetectionDao obsvOBDDetectionDao = (ObsvOBDDetectionDao) sensorObsvManager
				.getObservationDao(SensorTypeEnum.SENSOR_TYPE_OBD_CAR_SPEED_SENSOR);
		List<ObsvOBDDetection> obsvList = obsvOBDDetectionDao
				.findOBDObservationsBySensor(sensor, startDate, endDate,
						orderByName, isAsc, offset, limit);

		List<ObsvOBDDetectionVO> obsvVOList = ObsvOBDDetectionVO
				.copyCollection(obsvList);

		return new JSONArray(obsvVOList);
	}

	@Override
	public List<Observations> queryUnclassifiedObservations(
			SensorObservationManager sensorObsvManager, Sensors sensor) {
		if (!SensorTypeEnum.SENSOR_TYPE_OBD_CAR_SPEED_SENSOR.getSensorType()
				.equals(sensor.getSensorType())) {
			return null;
		}

		ObsvOBDDetectionDao obsvOBDDetectionDao = (ObsvOBDDetectionDao) sensorObsvManager
				.getObservationDao(SensorTypeEnum.SENSOR_TYPE_OBD_CAR_SPEED_SENSOR);

		return obsvOBDDetectionDao.queryUnclassifiedOBDObservations(sensor);
	}

	@Override
	public JSONArray findObservationsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Integer offset, Integer limit) {
		if (!SensorTypeEnum.SENSOR_TYPE_OBD_CAR_SPEED_SENSOR.getSensorType()
				.equals(sensor.getSensorType())) {
			return null;
		}

		ObsvOBDDetectionDao obsvOBDDetectionDao = (ObsvOBDDetectionDao) sensorObsvManager
				.getObservationDao(SensorTypeEnum.SENSOR_TYPE_OBD_CAR_SPEED_SENSOR);
		List<ObsvOBDDetection> obsvList = obsvOBDDetectionDao
				.findOBDObservationsBySensor(sensor, offset, limit);

		List<ObsvOBDDetectionVO> obsvVOList = ObsvOBDDetectionVO
				.copyCollection(obsvList);
		return new JSONArray(obsvVOList);
	}

	@Override
	public Boolean migrateUnclassifiedObservations(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			List<Observations> obsvList) {
		if (!SensorTypeEnum.SENSOR_TYPE_OBD_CAR_SPEED_SENSOR.getSensorType()
				.equals(sensor.getSensorType())) {
			return null;
		}

		ObsvOBDDetectionDao obsvOBDDetectionDao = (ObsvOBDDetectionDao) sensorObsvManager
				.getObservationDao(SensorTypeEnum.SENSOR_TYPE_OBD_CAR_SPEED_SENSOR);

		for (Observations obsv : obsvList) {
			ObsvOBDDetection obsvOBDDetection = new ObsvOBDDetection(
					obsv.getLongitude(), obsv.getLatitude(), obsv.getObsvTime());
			try {
				decodeAndSetOBDJSONArrayParameters(obsvOBDDetection,
						obsv.getValue());
				obsvOBDDetection.setObdSensor(obsv);
				obsvOBDDetectionDao.save(obsvOBDDetection);
			} catch (JSONException jsonEx) {
				log.error(String
						.format("OBD sensor observation value syntax error. Expect JSON Array while the actual value is %s. The exception is %s ",
								obsv.getValue(), jsonEx.toString()));
			}
		}

		return true;
	}

	@Override
	public JSONArray aggregateObservationResultsBySensor(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate) {
		if (!SensorTypeEnum.SENSOR_TYPE_OBD_CAR_SPEED_SENSOR.getSensorType()
				.equals(sensor.getSensorType())) {
			return null;
		}

		return new JSONArray();
	}

	@Override
	public JSONArray countSensorObsvSubCategories(
			SensorObservationManager sensorObsvManager, Sensors sensor,
			Date startDate, Date endDate) {
		if (!SensorTypeEnum.SENSOR_TYPE_OBD_CAR_SPEED_SENSOR.getSensorType()
				.equals(sensor.getSensorType())) {
			return null;
		}
		return new JSONArray();
	}

	@Override
	public Boolean normaliseObservation(
			SensorObservationManager sensorObsvManager, Long obsvId,
			Map<String, String> normalisationValues) {
		return null;
	}
}
