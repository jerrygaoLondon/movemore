package uk.ac.oak.movemore.webapp.service.impl;

import java.util.Date;
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
import uk.ac.oak.movemore.webapp.model.Device;
import uk.ac.oak.movemore.webapp.model.Sensors;
import uk.ac.oak.movemore.webapp.service.DeviceManager;
import uk.ac.oak.movemore.webapp.service.DeviceService;
import uk.ac.oak.movemore.webapp.service.SensorManager;
import uk.ac.oak.movemore.webapp.service.response.JSONResponse;
import uk.ac.oak.movemore.webapp.service.response.ServiceResponseFailure;
import uk.ac.oak.movemore.webapp.vo.DeviceVO;
import uk.ac.oak.movemore.webapp.vo.SensorVO;

@Service("deviceManager")
@WebService(serviceName = "DeviceService", endpointInterface = "uk.ac.oak.movemore.webapp.service.DeviceService")
public class DeviceManagerImpl extends GenericManagerImpl<Device, Long>
		implements DeviceManager, DeviceService {

	@Autowired
	private DeviceDao deviceDao;

	@Autowired
	private SensorManager sensorManager;
	
	@Override
	public Response updateDeviceLocation(String deviceId, Double latitude,
			Double longitude) {
		//JSONResponse deviceResp;
		
		if (StringUtils.isEmpty(deviceId)) {
			return badRequest("Please provide valid device. {Device id} IS EMPTY.");			
		}
		
		if(latitude!=null && latitude.floatValue() == 0.0f) {
			return badRequest("Please provide valid latitude. {latitude} IS INVALID.");
		}
		
		if(longitude!=null && longitude.floatValue() == 0.0f) {
			return badRequest("Please provide valid longtitude. {longtitude} IS INVALID.");
		}
		JSONObject resp = new JSONObject();
		try {
			if(!deviceDao.exists(Long.valueOf(deviceId))) {
				log.warn("Please provide valid device system id. Current deviceId {"+deviceId+"} has not been registered in system.");
				return badRequest("Device not found. Please provide valid device system id or register device first.");
			}
			
			Device device = deviceDao.get(Long.valueOf(deviceId));
			device.setLatitude(latitude);
			device.setLongitude(longitude);
			device.setUpdated(new Date());
			deviceDao.save(device);
			
			resp.put("deviceId", String.valueOf(device.getDeviceId()));
		} catch (Exception ex) {
			log.error(ex);
			return Response.status(Status.EXPECTATION_FAILED.getStatusCode()).entity(ex.toString()).build();
		}
		
		
		return Response.status(Status.OK.getStatusCode()).entity(resp.toString()).build();
	}

	@Override
	public Response registerNewDevice(String devicePhysicalId,
			Double latitude, Double longitude, String deviceName) {
		log.info("register a new device");
		log.info("devicePhysicalId:"+devicePhysicalId);
		log.info("latitude:"+devicePhysicalId);
		log.info("longtitude:"+devicePhysicalId);
		log.info("deviceName:"+deviceName);
		
		if (StringUtils.isEmpty(devicePhysicalId)) {
			return badRequest("Please provide valid device physical id. current {devicePhysicalId} is empty.");
		}
		
		if (StringUtils.isEmpty(deviceName)) {
			return badRequest("Please provide valid device name.. current {deviceName} is empty.");
		}
		
		
		if(latitude!=null && latitude.floatValue() == 0.0f) {
			return badRequest("Please provide valid latitude. current {latitude} is empty.");
		}
		
		if(longitude!=null && longitude.floatValue() == 0.0f) {
			return badRequest("Please provide valid longitude. current {longitude} is empty.");
		}
		
		boolean isExist = deviceDao.isDevicePhysicalIdExist(devicePhysicalId);
		if(isExist) {
			String msg="The device '"+devicePhysicalId+"' is conflicted with existing one registered already.";
			log.warn(msg);
			
			return Response.status(Status.CONFLICT.getStatusCode()).entity(msg).build();
		}
		JSONObject resp = new JSONObject();
		try{
			Device newDevice = new Device(devicePhysicalId, deviceName, deviceName);
			newDevice.setLatitude(latitude);
			newDevice.setLongitude(longitude);
			
			newDevice = deviceDao.save(newDevice);
			
			resp.put("deviceId", String.valueOf(newDevice.getDeviceId()));
		} catch (Exception ex) {
			log.error(ex);
			return Response.status(Status.EXPECTATION_FAILED.getStatusCode()).entity(ex.toString()).build();
		}
		return Response.status(Status.OK.getStatusCode()).entity(resp.toString()).build();
	}
	
	@Override
	public Device addOrUpdateDevice(String devicePhysicalId, String deviceName, Double latitude, Double longitude, Float batteryLevel) {
		Device device;
		
		boolean isExist = deviceDao.isDevicePhysicalIdExist(devicePhysicalId);
		if(isExist) {
			try {
				device = deviceDao.findDeviceByPhysicalId(devicePhysicalId);
				if (StringUtils.isNotEmpty(deviceName)) {
					device.setName(deviceName);
				}
				if (latitude != null) {
					device.setLatitude(latitude);
				}
				if (longitude != null) {
					device.setLongitude(longitude);
				}
				
				if (batteryLevel != null) {
					device.setBatteryLevel(batteryLevel);
				}
				
				device.setUpdated(new Date());
				return device;	
			} catch (DeviceNotFoundException de) {
				log.error("Unexpected Exception:"+ de.toString());
			}			
		}
		
		device = new Device(devicePhysicalId, deviceName, deviceName);
		if (latitude != null) {
			device.setLatitude(latitude);
		}
		if (longitude != null) {
			device.setLongitude(longitude);
		}
		if (batteryLevel != null) {
			device.setBatteryLevel(batteryLevel);
		}
		device = deviceDao.save(device);
		
		return device;		
	}

	private JSONResponse generalServiceFailureResp(Exception ex) {
		JSONResponse deviceResp;
		deviceResp = new ServiceResponseFailure();
		deviceResp.setIsSuccess(-1);
		((ServiceResponseFailure)deviceResp).setReason(ex.getMessage());
		((ServiceResponseFailure)deviceResp).setMessage("Unexpected Exception.");
		log.error(ex.getMessage());
		return deviceResp;
	}
	
	@Override
	public List<Device> getAll() {
		return deviceDao.getAll();
	}

	@Override
	public Device get(Long id) {
		return deviceDao.get(id);
	}

	@Override
	public boolean exists(Long id) {
		return deviceDao.exists(id);
	}

	@Override
	public Device save(Device object) {
		return deviceDao.save(object);
	}

	@Override
	public void remove(Device object) {
		deviceDao.remove(object);
	}

	@Override
	public void remove(Long id) {
		deviceDao.remove(id);
	}

	@Override
	public void reindex() {
		deviceDao.reindex();
	}

	@Override
	public void reindexAll(boolean async) {
		deviceDao.reindexAll(async);
	}

	@Override
	public Device findDeviceByPhysicalId(String devicePhysicalId)
			throws DeviceNotFoundException {		
		return deviceDao.findDeviceByPhysicalId(devicePhysicalId);
	}
	
	@Override
	public Response registerOrUpdateDeviceInfo(String devicePhysicalId,
			String deviceName, String sensorPhysicalId, String sensorName,
			Double latitude, Double longitude, Float batteryLevel, String sensorDescription, String sensorType) {
		log.info(String
				.format("register or update device information. Processing data received from sensor: device id is %s, device name is %s, sensor id is %s, sensor name is %s, lat %s, lon %s, battery leve is %s, sensor description is %s",
						devicePhysicalId,
						deviceName,
						sensorPhysicalId,
						sensorName,
						latitude,
						longitude,
						batteryLevel,
						sensorDescription,
						sensorType));

		if (StringUtils.isEmpty(devicePhysicalId)) {
			return badRequest("Please provide valid device. {Device physical id} IS EMPTY.");
		}

		if (StringUtils.isEmpty(sensorPhysicalId)) {
			return badRequest("Please provide valid sensor. {Sensor physical id} IS EMPTY.");
		}
		JSONObject sensorResult = new JSONObject();
		try {
			Device device = addOrUpdateDevice(devicePhysicalId, deviceName, latitude, longitude, batteryLevel);
	
			Sensors sensor=sensorManager.addOrUpdateSensor(device, sensorPhysicalId, sensorName, sensorDescription, sensorType);
			
			sensorResult.put("sensorId", sensor.getSensorId());
			
		} catch (Exception ex) {
			log.error(ex);
			return Response.status(Status.EXPECTATION_FAILED.getStatusCode()).entity(ex.toString()).build();
		}
		return Response.status(Status.OK.getStatusCode()).entity(sensorResult.toString()).build();
	}

	
	
	@Override
	public Response getAllSensors(String deviceId) {
		if (StringUtils.isEmpty(deviceId)) {						
			return badRequest("'deviceId' (physical id) is Empty");
		}
		
		Device device = null;
		
		try {
			device = deviceDao.findDeviceByPhysicalId(deviceId);
		} catch (DeviceNotFoundException objRetEx) {
			log.error(objRetEx.getMessage());		
		}		
		
		if(device == null) {
			return badRequest("Device not found");
		}
		
		Set<Sensors> sensors = device.getSensors();
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.put("sensors", new JSONArray(SensorVO.copyCollection(sensors)));
		
		return Response.status(Status.OK.getStatusCode()).entity(jsonObj.toString()).build();
	}
	
	
	
	private Response badRequest(String msg) {
		return Response.status(Status.BAD_REQUEST.getStatusCode()).entity(msg).build();
	}

	@Override
	@Transactional
	public Response getAllDevices() {
		List<Device> deviceList = deviceDao.getAllDevices();
		
		List<DeviceVO> deviceVOList = DeviceVO.copyCollection(deviceList);
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("devices", new JSONArray(deviceVOList));
		
		return Response.status(Status.OK.getStatusCode()).entity(jsonObj.toString()).build();
	}
	
}
