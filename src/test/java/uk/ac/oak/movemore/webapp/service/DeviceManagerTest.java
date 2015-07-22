package uk.ac.oak.movemore.webapp.service;

import java.util.Set;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.appfuse.service.BaseManagerTestCase;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.oak.movemore.webapp.model.Device;
import uk.ac.oak.movemore.webapp.model.Sensors;

public class DeviceManagerTest extends BaseManagerTestCase {

	@Autowired
	private DeviceManager deviceManager;

	@Autowired
	private SensorManager sensorManager;

	@Test
	public void testUpdateDeviceLocation() throws Exception {

		Long deviceId = 1000001L;
		Double latitude = 38.891300d;
		Double longtitude = -77.025900d;

		Response jsonResp = deviceManager.updateDeviceLocation(
				deviceId.toString(), latitude, longtitude);

		//Assert.assertTrue(jsonResp instanceof DeviceServiceSuccess);
		Assert.assertNotNull(jsonResp);		
		Assert.assertEquals(Status.OK.getStatusCode(), jsonResp.getStatus());
	}

	@Test
	public void testRegisterNewDevice() throws Exception {
		String devicePhysicalId = "00CC02EE038C33BC";
		Double latitude = 38.891300d;
		Double longtitude = -77.025900d;
		String deviceName = "Device Test Name";

		Response jsonResp = deviceManager.registerNewDevice(
				devicePhysicalId, latitude, longtitude, deviceName);
		Assert.assertNotNull(jsonResp);
		
		Assert.assertEquals(Status.OK.getStatusCode(), jsonResp.getStatus());
		
//		Assert.assertTrue(jsonResp instanceof DeviceServiceSuccess);

//		Assert.assertEquals(new Integer(1), jsonResp.getIsSuccess());

//		Assert.assertNotNull(((DeviceServiceSuccess) jsonResp).getDeviceId());
		
		JSONObject jsonObj = new JSONObject(String.valueOf(jsonResp.getEntity()));
		Assert.assertNotNull(jsonObj);
//		String deviceId = ((DeviceServiceSuccess) jsonResp).getDeviceId();
		String deviceId = jsonObj.getString("deviceId");

		Assert.assertTrue(deviceManager.exists(Long.valueOf(deviceId)));
		Device newDevice = deviceManager.get(Long.valueOf(deviceId));

		Assert.assertEquals(devicePhysicalId, newDevice.getDevicePhysicalId());
		Assert.assertEquals(latitude, newDevice.getLatitude());
		Assert.assertEquals(longtitude, newDevice.getLongitude());
		Assert.assertEquals(deviceName, newDevice.getName());
		Assert.assertEquals(deviceName, newDevice.getDescription());
	}

	@Test
	public void testRegisterDuplicatedDevice() throws Exception {
		String devicePhysicalId = "00CC02EE038C";
		Double latitude = 38.891300d;
		Double longtitude = -77.025900d;
		String deviceName = "Device Test Name";

		Response jsonResp = deviceManager.registerNewDevice(
				devicePhysicalId, latitude, longtitude, deviceName);
//		Assert.assertTrue(jsonResp instanceof ServiceResponseFailure);

//		Assert.assertEquals(new Integer(-2), jsonResp.getIsSuccess());
		Assert.assertNotNull(jsonResp);
		
		Assert.assertEquals(Status.CONFLICT.getStatusCode(), jsonResp.getStatus());
		
//		String reason=(String)jsonResp.getEntity();
//		Assert.assertNotNull(((ServiceResponseFailure) jsonResp).getReason());

//		Assert.assertEquals("'{devicePhysicalId}' is registered.",
//				((ServiceResponseFailure) jsonResp).getReason());
//		Assert.assertNotNull(((ServiceResponseFailure) jsonResp).getMessage());
	}

	//
	// registerOrUpdateDeviceInfo(String devicePhysicalId,
	// String deviceName, String sensorPhysicalId, String sensorName,
	// double latitude, double longtitude) {
	@Test
	public void testUpdatingExistingDeviceInfo() throws Exception {
		// Test update existing device
		String devicePhysicalId = "00CC02EE038C";
		double latitude = 38.891300;
		double longitude = -77.025900;
		String deviceName = "dummy device 1 for test update";
		String sensorPhysicalId = "b827eb2eb26e";
		String sensorName = "Dummy water velocity sensor for testing";

		Response deviceResp = deviceManager.registerOrUpdateDeviceInfo(
				devicePhysicalId, deviceName, sensorPhysicalId, sensorName,
				latitude, longitude, null, sensorName, null);
//		Assert.assertTrue(deviceResp instanceof DeviceServiceSuccess);
		Assert.assertNotNull(deviceResp);		
		Assert.assertEquals(Status.OK.getStatusCode(), deviceResp.getStatus());

//		Assert.assertEquals(new Integer(1),
//				((DeviceServiceSuccess) deviceResp).getIsSuccess());

		Device device = deviceManager.findDeviceByPhysicalId(devicePhysicalId);
		Assert.assertNotNull(device);

		Assert.assertEquals(deviceName, device.getName());
		Assert.assertEquals(Double.valueOf(latitude),
				device.getLatitude());
		Assert.assertEquals(Double.valueOf(longitude),
				device.getLongitude());

		Set<Sensors> sensorSet = device.getSensors();
		Assert.assertFalse(sensorSet.isEmpty());
		Assert.assertEquals(1, sensorSet.size());

		Sensors sensor = sensorSet.iterator().next();
		Assert.assertEquals(sensorPhysicalId, sensor.getSensorPhysicalId());
		Assert.assertEquals(sensorName, sensor.getName());
	}

	@Test
	public void testUpdatingExistingDeviceWithNewSensor() throws Exception {
		String devicePhysicalId = "00CC02EE038C";
		double latitude = 38.891300;
		double longitude = -77.025900;
		String deviceName = "dummy device 1 for test update";

		String newSensorPhysicalId = "b827nn2XX26e";
		String newSensorName = "Dummy water velocity sensor for testing";

		Device existingDevice = deviceManager
				.findDeviceByPhysicalId(devicePhysicalId);
		Assert.assertNotNull(existingDevice);
		Assert.assertNotNull(existingDevice.getSensors());
		Assert.assertEquals(1, existingDevice.getSensors().size());

		Response deviceResp = deviceManager.registerOrUpdateDeviceInfo(
				devicePhysicalId, deviceName, newSensorPhysicalId,
				newSensorName, latitude, longitude, null, newSensorName, null);
//		Assert.assertTrue(deviceResp instanceof DeviceServiceSuccess);
//
//		Assert.assertEquals(new Integer(1),
//				((DeviceServiceSuccess) deviceResp).getIsSuccess());
		
		Assert.assertNotNull(deviceResp);		
		Assert.assertEquals(Status.OK.getStatusCode(), deviceResp.getStatus());

		Device device = deviceManager.findDeviceByPhysicalId(devicePhysicalId);
		Assert.assertNotNull(device);

		Assert.assertEquals(deviceName, device.getName());
		Assert.assertEquals(Double.valueOf(latitude),
				device.getLatitude());
		Assert.assertEquals(Double.valueOf(longitude),
				device.getLongitude());

		Set<Sensors> sensorSet = device.getSensors();
		Assert.assertFalse(sensorSet.isEmpty());
		// Assert.assertEquals(2, sensorSet.size());

		boolean isExist = sensorManager
				.isSensorPhysicalIdExist(newSensorPhysicalId);
		Assert.assertTrue(isExist);

		boolean isOldExist = sensorManager
				.isSensorPhysicalIdExist("b827eb2eb26e");
		Assert.assertTrue(isOldExist);

		Sensors newSensor = sensorManager
				.findSensorByPhysicalId(newSensorPhysicalId);
		Assert.assertEquals(newSensorName, newSensor.getName());
	}

	@Test
	public void testRegisterNewDeviceWithNewSensor() throws Exception {
		String newDevicePhysicalId = "CC22EERRD001EELL33";
		double latitude = 51.148500;
		double longitude = -2.714000;
		String newDeviceName = "dummy device for Glastonbury";

		String newSensorPhysicalId = "b827nn2FF26ett";
		String newSensorName = "Dummy sensor for Glastonbury";

		Response deviceResp = deviceManager.registerOrUpdateDeviceInfo(
				newDevicePhysicalId, newDeviceName, newSensorPhysicalId,
				newSensorName, latitude, longitude, null, newSensorName, null);
//		Assert.assertTrue(deviceResp instanceof DeviceServiceSuccess);
		Assert.assertNotNull(deviceResp);		
		Assert.assertEquals(Status.OK.getStatusCode(), deviceResp.getStatus());

		Device device = deviceManager
				.findDeviceByPhysicalId(newDevicePhysicalId);
		Assert.assertNotNull(device);

		Assert.assertEquals(newDeviceName, device.getName());
		Assert.assertEquals(Double.valueOf(latitude),
				device.getLatitude());
		Assert.assertEquals(Double.valueOf(longitude),
				device.getLongitude());

		boolean isExist = sensorManager
				.isSensorPhysicalIdExist(newSensorPhysicalId);
		Assert.assertTrue(isExist);

		// Set<Sensors> sensorSet = device.getSensors();
		// Assert.assertFalse(sensorSet.isEmpty());
		// Assert.assertEquals(1, sensorSet.size());

		Sensors newSensor = sensorManager
				.findSensorByPhysicalId(newSensorPhysicalId);
		Assert.assertEquals(newSensorName, newSensor.getName());
	}
}
