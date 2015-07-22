package uk.ac.oak.movemore.webapp.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.appfuse.dao.BaseDaoTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.model.ObsvOBDDetection;
import uk.ac.oak.movemore.webapp.model.Sensors;

public class ObsvOBDSensorDaoTest extends BaseDaoTestCase {

	@Autowired
	private ObsvOBDDetectionDao obsvOBDDetectDao;
	
	@Autowired
	private ObservationsDao observationsDao;

	@Autowired
	private SensorsDao sensorsDao;
	
	@Test
	public void testInsert() throws Exception {
		Long sensorId = 3000001l;
		
		Double fuelEconomy = -1.0;
		Double vehicleSpeed = 4.0;
		Double ambientAirTemperature=25.0;
		Double engineRPM = 899.0;
		Double fuelLevel= 0.0;
		Double latitude = 53.383d;
		Double longitude = -1.4659d;
		Date obsvTime = new Date();
		
		Sensors dummySensor = sensorsDao.get(sensorId);

		Set<ObsvOBDDetection> obsvDeviceDetectionInitValues = obsvOBDDetectDao.findDistinctOBDObservationsBySensor(dummySensor);
		Assert.assertNotNull(obsvDeviceDetectionInitValues);
		Assert.assertEquals(0, obsvDeviceDetectionInitValues.size());
		
		Assert.assertNotNull(dummySensor);
		Observations obsv = new Observations(dummySensor);
		
		ObsvOBDDetection obsvOBDDetection = new ObsvOBDDetection(longitude, latitude, obsvTime);
		obsvOBDDetection.setFuelEconomy(fuelEconomy);
		obsvOBDDetection.setVehicleSpeed(vehicleSpeed);
		obsvOBDDetection.setAmbientAirTemperature(ambientAirTemperature);
		obsvOBDDetection.setEngineRPM(engineRPM);
		obsvOBDDetection.setFuelLevel(fuelLevel);
		
		obsvOBDDetection.setObdSensor(obsv);
		obsv.setObsvOBDDetect(obsvOBDDetection);
		
		obsv = observationsDao.save(obsv);
		
		Set<ObsvOBDDetection> obsvDeviceDetections = obsvOBDDetectDao.findDistinctOBDObservationsBySensor(dummySensor);
		Assert.assertNotNull(obsvDeviceDetections);
		Assert.assertEquals(1, obsvDeviceDetections.size());
	}
	
	@Test
	public void testfindObservationsBySensor1() throws Exception {
		 
		 //load sample sensor data
		 Long dummySensorId = new Long(3000001);
		 Sensors sensor = sensorsDao.get(dummySensorId);
		 
		 Assert.assertNotNull("Sensor does not exist", sensor);
		 Assert.assertNotNull(sensor.getSensorId());
		 //
		 ISO8601DateFormat df = new ISO8601DateFormat();
		 Date startDate = df.parse("2014-06-05T15:00:00+00:00");
		 Date endDate = df.parse("2014-07-10T17:00:00+00:00"); 
		 Integer offset = 0; 
		 Integer limit = 10;
		 
		 List<ObsvOBDDetection> obsvList = obsvOBDDetectDao.findOBDObservationsBySensor(sensor, startDate, endDate, null, null, offset, limit);
		 Assert.assertNotNull(obsvList);
		 Assert.assertTrue(obsvList.isEmpty());
		 Assert.assertEquals(0, obsvList.size());
	}
	
	@Test
	public void testqueryUnclassifiedOBDObservations() throws Exception {
		
//		Long sensorId = new Long(3000015);
//		
//		Sensors sensor = sensorsDao.get(sensorId);
//		 
//		List<Observations> obsvList = obsvOBDDetectDao.queryUnclassifiedOBDObservations(sensor);
//		
//		Assert.assertNull(obsvList);
	}
}
