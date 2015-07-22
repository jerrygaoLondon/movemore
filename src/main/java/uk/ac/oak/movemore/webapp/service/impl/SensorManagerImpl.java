package uk.ac.oak.movemore.webapp.service.impl;

import java.util.List;
import java.util.Set;

import javax.jws.WebService;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.appfuse.service.impl.GenericManagerImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.oak.movemore.webapp.dao.DeviceDao;
import uk.ac.oak.movemore.webapp.dao.DeviceNotFoundException;
import uk.ac.oak.movemore.webapp.dao.ObservationsDao;
import uk.ac.oak.movemore.webapp.dao.SensorNotFoundException;
import uk.ac.oak.movemore.webapp.dao.SensorsDao;
import uk.ac.oak.movemore.webapp.model.Device;
import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.model.Sensors;
import uk.ac.oak.movemore.webapp.service.SensorManager;
import uk.ac.oak.movemore.webapp.service.SensorService;
import uk.ac.oak.movemore.webapp.service.response.JSONResponse;
import uk.ac.oak.movemore.webapp.service.response.SensorServiceSuccess;
import uk.ac.oak.movemore.webapp.service.response.ServiceResponseFailure;
import uk.ac.oak.movemore.webapp.util.SensorTypeClassifer;
import uk.ac.oak.movemore.webapp.util.SensorTypeEnum;
import uk.ac.oak.movemore.webapp.vo.SensorVO;

@Service("sensorManager")
@WebService(serviceName = "SensorService", endpointInterface = "uk.ac.oak.movemore.webapp.service.SensorService")
public class SensorManagerImpl extends GenericManagerImpl<Sensors, Long>
		implements SensorManager, SensorService {

	@Autowired
	private SensorsDao sensorsDao;
	@Autowired
	private DeviceDao deviceDao;
	@Autowired
	private ObservationsDao observationsDao;

	@Override
	public JSONResponse addNewSensor(String devicePhysicalId,
			String sensorPhysicalId, String sensorName, Integer sensorType) {
		JSONResponse sensorResp;

		if (StringUtils.isEmpty(devicePhysicalId)) {
			sensorResp = new ServiceResponseFailure();
			sensorResp.setIsSuccess(-1);
			((ServiceResponseFailure) sensorResp)
					.setMessage("Please provide valid device id.");
			((ServiceResponseFailure) sensorResp)
					.setReason("'{deviceId}' IS EMPTY");
			log.debug("'{deviceId}' IS EMPTY");
			return sensorResp;
		}

		if (StringUtils.isEmpty(sensorPhysicalId)) {
			sensorResp = new ServiceResponseFailure();
			sensorResp.setIsSuccess(-1);
			((ServiceResponseFailure) sensorResp)
					.setMessage("Please provide valid sensor id.");
			((ServiceResponseFailure) sensorResp)
					.setReason("'{sensorId}' IS EMPTY");
			log.debug("'{sensorId}' IS EMPTY");
			return sensorResp;
		}

		if (StringUtils.isEmpty(sensorName)) {
			sensorResp = new ServiceResponseFailure();
			sensorResp.setIsSuccess(-1);
			((ServiceResponseFailure) sensorResp)
					.setMessage("Please provide valid sensor name.");
			((ServiceResponseFailure) sensorResp)
					.setReason("'{name}' IS EMPTY");
			log.debug("'{name}' IS EMPTY");
			return sensorResp;
		}

		try {
			// Device device = deviceDao.get(Long.valueOf(deviceId));
			Device device = deviceDao.findDeviceByPhysicalId(devicePhysicalId);
			// set sensor name as default description
			Sensors sensor = new Sensors(sensorPhysicalId, sensorName,
					sensorName);
			sensor.setDevice(device);

			if (sensorType!=null) {
				sensor.setSensorType(sensorType);
			} else {
				SensorTypeEnum predictedSensorType = SensorTypeClassifer
						.guessSensorType(sensorPhysicalId, sensorName);
				sensor.setSensorType(predictedSensorType.getSensorType());
			}
			
			sensor = sensorsDao.save(sensor);

			sensorResp = new SensorServiceSuccess();
			sensorResp.setIsSuccess(1);
			((SensorServiceSuccess) sensorResp).setSensorId(String
					.valueOf(sensor.getSensorId()));

			return sensorResp;
		} catch (DeviceNotFoundException objReEx) {
			sensorResp = new ServiceResponseFailure();
			sensorResp.setIsSuccess(-1);
			((ServiceResponseFailure) sensorResp)
					.setMessage("Device not found. Please register your device first.");
			((ServiceResponseFailure) sensorResp).setReason(objReEx
					.getMessage());
			log.warn(objReEx.getMessage());
		} catch (Exception ex) {
			sensorResp = generalSensorServiceFailureResp(ex);
		}
		return sensorResp;
	}

	private JSONResponse generalSensorServiceFailureResp(Exception ex) {
		JSONResponse sensorResp;
		sensorResp = new ServiceResponseFailure();
		sensorResp.setIsSuccess(-1);
		((ServiceResponseFailure) sensorResp).setReason(ex.getMessage());
		((ServiceResponseFailure) sensorResp)
				.setMessage("Unexpected Exception.");
		log.error(ex.getMessage());
		return sensorResp;
	}

	@Override
	public JSONResponse removeSensor(String sensorId) {
		JSONResponse sensorResp;

		try {
			if (sensorsDao.exists(Long.valueOf(sensorId))) {
				Sensors sensor = sensorsDao.get(Long.valueOf(sensorId));
				Set<Observations> obsvSet = sensor.getObservations();
				for (Observations obsv : obsvSet) {
					observationsDao.removeObservationAttachment(obsv);
				}
				sensorsDao.remove(sensor);
			}
		} catch (Exception ex) {
			sensorResp = generalSensorServiceFailureResp(ex);
		}

		sensorResp = new SensorServiceSuccess();
		sensorResp.setIsSuccess(1);
		return sensorResp;
	}

	@Override
	@Transactional
	public Response getSensor(String sensorId) {
		if (StringUtils.isEmpty(sensorId)) {
			return badRequest("'sensorId' (physical id) is Empty");
		}

		Sensors sensor = null;
		try {
			sensor = sensorsDao.findSensorByPhysicalId(sensorId);
		} catch (SensorNotFoundException objRetEx) {
			log.error(objRetEx.getMessage());
		}

		if (sensor == null) {
			return badRequest("Sensor not found");
		}

		SensorVO sensorVO = new SensorVO();
		sensorVO.clone(sensor);

		return Response.status(Status.OK.getStatusCode())
				.entity(new JSONObject(sensorVO).toString()).build();
	}

	@Override
	@Transactional
	public Response countSensorsByType() {
		List<String[]> results = sensorsDao.countSensorsByType();
		JSONArray jsonRes = new JSONArray();

		for (String[] res : results) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("sensorType", res[0]);
			jsonObj.put("count", res[1]);
			jsonObj.put("typeName", res[2]);
			jsonRes.put(jsonObj);
		}

		return Response.status(Status.OK.getStatusCode())
				.entity(jsonRes.toString()).build();
	}

	private Response badRequest(String msg) {
		return Response.status(Status.BAD_REQUEST.getStatusCode()).entity(msg)
				.build();
	}

	@Override
	@Transactional
	public Response getAllSensors() {
		List<Sensors> sensors = sensorsDao.getAllSensors();
		List<SensorVO> sensorVOList = SensorVO.copyCollection(sensors);

		JSONObject jsonObj = new JSONObject();
		jsonObj.put("sensors", new JSONArray(sensorVOList));

		return Response.status(Status.OK.getStatusCode())
				.entity(jsonObj.toString()).build();
	}

	@Override
	public Sensors addOrUpdateSensor(Device device, String sensorPhysicalId,
			String sensorName, String sensorDescription, String sensorType) {

		Sensors sensor;

		boolean isExist = sensorsDao.isSensorPhysicalIdExist(sensorPhysicalId);
		if (isExist) {
			try {
				sensor = sensorsDao.findSensorByPhysicalId(sensorPhysicalId);
				if (StringUtils.isNotEmpty(sensorName)) {
					sensor.setName(sensorName);
				}

				if (StringUtils.isNotEmpty(sensorDescription)) {
					sensor.setDescription(sensorDescription);					
				}
				
				if (StringUtils.isNotEmpty(sensorType)) {
					sensor.setSensorType(Integer.valueOf(sensorType));	
				}
//				else{
//					sensor.setSensorType(SensorTypeEnum.SENSOR_TYPE_ACTIVITY_SENSOR.getSensorType());
//				}
				return sensorsDao.save(sensor);
			} catch (SensorNotFoundException de) {
				log.error("Unexpected Exception:" + de.toString());
			}
		}		
		
		sensor = new Sensors(sensorPhysicalId, sensorName, sensorDescription);
		sensor.setDevice(device);

		if (StringUtils.isEmpty(sensorType)) {
			/*
			SensorTypeEnum predictedSensorType = SensorTypeClassifer
					.guessSensorType(sensorPhysicalId, sensorName);
			sensor.setSensorType(predictedSensorType.getSensorType());*/
		} else {
			sensor.setSensorType(Integer.valueOf(sensorType));	
		}

		sensor = sensorsDao.save(sensor);		
		
		return sensor;
	}

	@Override
	public Sensors updateSensorType(Long sensorId, Integer sensorType) {
		Sensors sensor;

		boolean isExist = sensorsDao.exists(sensorId);
		if (isExist) {
			sensor = sensorsDao.get(sensorId);
			sensor.setSensorType(sensorType);
			return sensorsDao.save(sensor);
		}
		return null;
	}

	public boolean isSensorPhysicalIdExist(String devicePhysicalId) {
		return sensorsDao.isSensorPhysicalIdExist(devicePhysicalId);
	}

	public Sensors findSensorByPhysicalId(String devicePhysicalId)
			throws SensorNotFoundException {
		return sensorsDao.findSensorByPhysicalId(devicePhysicalId);
	}

	@Override
	public List<Sensors> getAll() {
		return sensorsDao.getAll();
	}

	@Override
	public Sensors get(Long id) {
		return sensorsDao.get(id);
	}

	@Override
	public boolean exists(Long id) {
		return sensorsDao.exists(id);
	}

	@Override
	public Sensors save(Sensors object) {
		return sensorsDao.save(object);
	}

	@Override
	public void remove(Sensors object) {
		sensorsDao.remove(object);
	}

	@Override
	public void remove(Long id) {
		sensorsDao.remove(id);
	}

}
