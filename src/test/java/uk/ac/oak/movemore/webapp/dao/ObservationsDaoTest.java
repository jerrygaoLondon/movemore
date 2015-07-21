package uk.ac.oak.movemore.webapp.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.appfuse.dao.BaseDaoTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.model.Sensors;

public class ObservationsDaoTest extends BaseDaoTestCase {
	 @Autowired
	 private ObservationsDao observationsDao = null;
	 @Autowired
	 private SensorsDao sensorsDao = null;
	 
	 @Test
	 public void testInsert() throws Exception {
		 Observations obsv1 ;
		 
		 //load sample sensor data
		 Long dummySensorId = new Long(3000001);
		 Sensors sensor = sensorsDao.get(dummySensorId);
		
		 Assert.assertNotNull("Sensor does not exist", sensor);
		 Assert.assertNotNull(sensor.getSensorId());
		 obsv1 = new Observations(sensor, "1000m", Timestamp.valueOf("2014-05-02 18:48:05.123456"));
		 obsv1.setLatitude(sensor.getDevice().getLatitude());
		 obsv1.setLongitude(sensor.getDevice().getLongitude());
		 Observations testObsv1 = observationsDao.save(obsv1);
		
		 Assert.assertNotNull(testObsv1);
		 Assert.assertNotNull(testObsv1.getObsvId());
		 
		 Assert.assertEquals(new Integer(0), testObsv1.getVersion()); 
		 Assert.assertNotNull(testObsv1.getLatitude());
		 Assert.assertNotNull(testObsv1.getLongitude());
		 Assert.assertEquals(0, Float.valueOf("-1.4659").compareTo(testObsv1.getLongitude())); 
		 Assert.assertEquals(0, Float.valueOf("53.383").compareTo(testObsv1.getLatitude())); 
		 Assert.assertNotNull(testObsv1.getCreated()); 
		 Assert.assertNotNull(testObsv1.getUpdated()); 
		 Assert.assertNotNull(testObsv1.getRecordTime()); 
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
		 
		 List<Observations> obsvList = observationsDao.findObservationsBySensor(sensor, startDate, endDate, null, null, offset, limit);
		 Assert.assertNotNull(obsvList);
		 Assert.assertFalse(obsvList.isEmpty());
	 }
	 
	 @Test
	 public void testfindObservationsBySensor2() throws Exception {		 
		 //load sample sensor data
		 Long dummySensorId = new Long(3000001);
		 Sensors sensor = sensorsDao.get(dummySensorId);
		 
		 Assert.assertNotNull("Sensor does not exist", sensor);
		 Assert.assertNotNull(sensor.getSensorId());
		 
		 Integer offset = 0; 
		 Integer limit = 1000;
		 
		 List<Observations> obsvList = observationsDao.findObservationsBySensor(sensor, offset, limit);
		 Assert.assertNotNull(obsvList);
		 Assert.assertFalse(obsvList.isEmpty());
		 
	 }
}
