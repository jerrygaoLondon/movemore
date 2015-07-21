package uk.ac.oak.movemore.webapp.service;

import java.util.List;

import org.appfuse.service.BaseManagerTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.oak.movemore.webapp.model.Device;
import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.model.Sensors;
import uk.ac.oak.movemore.webapp.service.response.SensorServiceSuccess;
import uk.ac.oak.movemore.webapp.service.response.JSONResponse;

public class SensorManagerTest extends BaseManagerTestCase {

	@Autowired
	private SensorManager sensorManager;
	
	@Autowired
	private SensorObservationManager sensorObservationManager;
	
	@Autowired
	private DeviceManager deviceManager;
	
	@Test
	public void testAddNewSensor() throws Exception {
		String devicePhysicalId = "00CC02EE038C";
		String sensorName = "New Test Sensor for device 00CC02EE038C";
		String sensorPhysicalId="b827eb2eb26e";
		
		JSONResponse resp = sensorManager.addNewSensor(devicePhysicalId, sensorPhysicalId, sensorName, 0);
		Assert.assertTrue(resp instanceof SensorServiceSuccess);
		
		Assert.assertNotNull(((SensorServiceSuccess)resp).getSensorId());
		
		String sensorId = ((SensorServiceSuccess)resp).getSensorId();
		Sensors newSensor = sensorManager.get(Long.valueOf(sensorId));
		Assert.assertNotNull(newSensor.getDevice());
		Assert.assertNotNull(newSensor.getDevice().getDeviceId());
		Assert.assertEquals(devicePhysicalId, newSensor.getDevice().getDevicePhysicalId());
		Assert.assertNotNull(newSensor.getDevice().getName());
		Assert.assertEquals(sensorName, newSensor.getName());
		Assert.assertEquals(sensorPhysicalId, newSensor.getSensorPhysicalId());
	}
	
	@Test
	public void testAddOrUpdateSensor () throws Exception {
		//test updating an existing sensor
		String devicePhysicalId = "00CC02EE038C";
		String existingSensorPhysicalId = "b827eb2eb26e";
		String sensorName = "Dummy water velocity sensor for testing";
	
		Device device = deviceManager.findDeviceByPhysicalId(devicePhysicalId);
		Assert.assertNotNull(device);
		Sensors sensorUpdated = sensorManager.addOrUpdateSensor(device, existingSensorPhysicalId, sensorName, sensorName, null);
		Assert.assertNotNull(sensorUpdated);
		
		Assert.assertEquals(sensorName, sensorUpdated.getName());
		
		//test adding a new sensor
		String newSensorPhysicalId = "b827EE2ebCCe";
		Sensors sensorAdded = sensorManager.addOrUpdateSensor(device, newSensorPhysicalId, sensorName, sensorName, null);
		Assert.assertNotNull(sensorAdded);
		Assert.assertEquals(newSensorPhysicalId, sensorAdded.getSensorPhysicalId());
		
		device = deviceManager.findDeviceByPhysicalId(devicePhysicalId);
		Assert.assertNotNull(device.getSensors());
		Assert.assertFalse(device.getSensors().isEmpty());
		Assert.assertEquals(1, device.getSensors().size());
	}
	
	@Test
	public void testRemoveSensor() throws Exception {
		String sensorId = "3000001";
		
		JSONResponse resp = sensorManager.removeSensor(sensorId);
		Assert.assertTrue(resp instanceof SensorServiceSuccess);
		Assert.assertNull(((SensorServiceSuccess)resp).getSensorId());
		
		Assert.assertFalse(sensorManager.exists(Long.valueOf(sensorId)));
		
		//test orphan removal
		List<Observations> obsvs = sensorObservationManager.getAll();
		Assert.assertTrue(obsvs.isEmpty());
	}
}
