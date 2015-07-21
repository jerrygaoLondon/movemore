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
import uk.ac.oak.movemore.webapp.model.ObsvVoiceDecibelDetection;
import uk.ac.oak.movemore.webapp.model.Sensors;

public class ObsvVoiceDecibelDetectionDaoTest extends BaseDaoTestCase {

	@Autowired
	private ObsvVoiceDecibelDetectionDao obsvVoiceDecibelDetectionDao;
	
	@Autowired
	private ObservationsDao observationsDao;

	@Autowired
	private SensorsDao sensorsDao;
	
	@Test
	public void testInsert() throws Exception {
		Long sensorId = 3000001l;
		Double decibel = 53.0;
		Float latitude = 53.383f;
		Float longitude = -1.4659f;
		Date obsvTime = new Date();
		
		Sensors dummySensor = sensorsDao.get(sensorId);
		Set<ObsvVoiceDecibelDetection> voiceDetectionInitValues = obsvVoiceDecibelDetectionDao.findDistinctVoiceDecibelBySensor(dummySensor);
		Assert.assertNotNull(voiceDetectionInitValues);
		Assert.assertEquals(1, voiceDetectionInitValues.size());
		
		Assert.assertNotNull(dummySensor);
		Observations obsv = new Observations(dummySensor);
		
		ObsvVoiceDecibelDetection voiceDetection = new ObsvVoiceDecibelDetection (decibel, longitude, latitude, obsvTime);
		
		voiceDetection.setVoiceSensor(obsv);
		obsv.setObsvVoiceDetect(voiceDetection);
		
		obsv = observationsDao.save(obsv);
		
		Assert.assertNotNull(obsv);
		Assert.assertNotNull(obsv.getObsvId());
		
		Set<ObsvVoiceDecibelDetection> voiceDetections = obsvVoiceDecibelDetectionDao.findDistinctVoiceDecibelBySensor(dummySensor);
		Assert.assertNotNull(voiceDetections);
		Assert.assertEquals(2, voiceDetections.size());
	}
	
	@Test
	public void testfindObservationsBySensor1() throws Exception {		 
		 //load sample sensor data
		 Long dummySensorId = new Long(3000001);
		 Sensors sensor = sensorsDao.get(dummySensorId);
		 
		 Assert.assertNotNull("Sensor does not exist", sensor);
		 Assert.assertNotNull(sensor.getSensorId());
		 
		 ISO8601DateFormat df = new ISO8601DateFormat();
		 Date startDate = df.parse("2014-06-05T15:00:00+00:00");
		 Date endDate = df.parse("2014-07-10T17:00:00+00:00"); 
		 Integer offset = 0; 
		 Integer limit = 10;
		 
		 List<ObsvVoiceDecibelDetection> obsvList = obsvVoiceDecibelDetectionDao.findPeopleCounterObservationsBySensor(sensor, startDate, endDate, null, null, offset, limit);
		 Assert.assertNotNull(obsvList);
		 Assert.assertFalse(obsvList.isEmpty());
		 Assert.assertEquals(1, obsvList.size());
	}
}
