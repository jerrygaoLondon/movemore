package uk.ac.oak.movemore.webapp.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.appfuse.dao.BaseDaoTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.model.ObsvPeopleCount;
import uk.ac.oak.movemore.webapp.model.Sensors;

public class ObsvPeopleCountDaoTest extends BaseDaoTestCase {

	@Autowired
	private ObsvPeopleCountDao obsvPeopleCountDao;

	@Autowired
	private ObservationsDao observationsDao;

	@Autowired
	private SensorsDao sensorsDao;

	@Test
	public void testInsert() throws Exception {
		Long sensorId = 3000001l;
		Integer peopleNumber = 30;
		Date obsvTime = new Date();
		double latitude = 53.383;
		double longitude = -1.4659;

		insertNewPeopleCountingObsvs(sensorId, peopleNumber, obsvTime,
				latitude, longitude);
	}

	@Test
	public void testFindPeopleCounterObservationsBySensor() throws Exception {
		// prepare test data
		Long sensorId = 3000001l;

		// observation 1
		Integer peopleNumber1 = 33;
		Date obsvTime1 = parseTimestamp("2014-06-05 12:00:01");
		double latitude1 = 53.383;
		double longitude1 = -1.4659;

		// observation 2
		Integer peopleNumber2 = 30;
		Date obsvTime2 = parseTimestamp("2014-06-05 12:00:01");
		double latitude2 = 54.383;
		double longitude2 = -1.4653;

		// observation 3
		Integer peopleNumber3 = 36;
		Date obsvTime3 = parseTimestamp("2014-06-05 12:00:01");
		double latitude3 = 54.333;
		double longitude3 = -1.4663;

		insertNewPeopleCountingObsvs(sensorId, peopleNumber1, obsvTime1,
				latitude1, longitude1);
		insertNewPeopleCountingObsvs(sensorId, peopleNumber2, obsvTime2,
				latitude2, longitude2);
		insertNewPeopleCountingObsvs(sensorId, peopleNumber3, obsvTime3,
				latitude3, longitude3);

		Sensors sensor = sensorsDao.get(sensorId);
		Assert.assertNotNull(sensor);

		List<ObsvPeopleCount> obsvPcList = obsvPeopleCountDao
				.findPeopleCounterObservationsBySensor(sensor);
		Assert.assertNotNull(obsvPcList);
		Assert.assertFalse(obsvPcList.isEmpty());
		// it's four records, because we have one sample data
		Assert.assertEquals(4, obsvPcList.size());

		for (ObsvPeopleCount opc : obsvPcList) {
			if (opc.getNumber().equals(peopleNumber3)) {
				Assert.assertEquals(0, Double.valueOf(latitude3)
						.compareTo(opc.getLatitude()));
				Assert.assertEquals(0, Double.valueOf(longitude3)
						.compareTo(opc.getLongitude()));
			}
		}

	}

	@Test
	public void testDeleteAllObservations() throws Exception {
		// prepare test data
		Long sensorId = 3000001l;

		// observation 1
		Integer peopleNumber1 = 33;
		Date obsvTime1 = parseTimestamp("2014-06-05 12:00:01");
		double latitude1 = 53.383;
		double longitude1 = -1.4659;

		// observation 2
		Integer peopleNumber2 = 30;
		Date obsvTime2 = parseTimestamp("2014-06-05 12:00:01");
		double latitude2 = 54.383;
		double longitude2 = -1.4653;
		insertNewPeopleCountingObsvs(sensorId, peopleNumber1, obsvTime1,
				latitude1, longitude1);
		insertNewPeopleCountingObsvs(sensorId, peopleNumber2, obsvTime2,
				latitude2, longitude2);

		Sensors sensor = sensorsDao.get(sensorId);
		Assert.assertNotNull(sensor);
		Assert.assertNotNull(sensor.getObservations());
		Assert.assertFalse(sensor.getObservations().isEmpty());
		//TODO: Assert.assertEquals(3, sensor.getObservations().size());
		List<Observations> obsvList = observationsDao.findObservationsBySensor(sensor);
		//7 sample data plus 2 new records
		Assert.assertEquals(9, obsvList.size());
		
		List<ObsvPeopleCount> obsvPcList = obsvPeopleCountDao
				.findPeopleCounterObservationsBySensor(sensor);
		Assert.assertNotNull(obsvPcList);
		Assert.assertFalse(obsvPcList.isEmpty());
		// it's three records, because we have one sample data
		Assert.assertEquals(3, obsvPcList.size());

		obsvPeopleCountDao.removeAllPeopleCounterObservations(obsvPcList);

		List<ObsvPeopleCount> obsvPcList2 = obsvPeopleCountDao
				.findPeopleCounterObservationsBySensor(sensor);

		Assert.assertNotNull(obsvPcList2);
		Assert.assertTrue(obsvPcList2.isEmpty());

		List<Observations> obsvs = observationsDao
				.findObservationsBySensor(sensor);
		Assert.assertNotNull(obsvs);
		//removed all the people counts observation records and leave all the other 6 sample data (default 7)
		Assert.assertEquals(6, obsvs.size());
	}
	
	@Test
	public void testDuplicates() throws Exception{
		// prepare test data
		Long sensorId = 3000001l;

		// observation 1
		Integer peopleNumber1 = 33;
		//Date obsvTime1 = parseTimestamp("2014-06-05 12:00:01");
		Date obsvTime1 = new Date();
		double latitude1 = 53.383;
		double longitude1 = -1.4659;

		insertNewPeopleCountingObsvs(sensorId, peopleNumber1, obsvTime1,
				latitude1, longitude1);
		insertNewPeopleCountingObsvs(sensorId, peopleNumber1, obsvTime1,
				latitude1, longitude1);

		Sensors sensor = sensorsDao.get(sensorId);
		Assert.assertNotNull(sensor);
		Assert.assertNotNull(sensor.getObservations());
		Assert.assertFalse(sensor.getObservations().isEmpty());
		List<Observations> obsvList = observationsDao.findObservationsBySensor(sensor);
		//7 sample data plus 2 new records
		Assert.assertEquals(9, obsvList.size());
		
		List<ObsvPeopleCount> obsvPcList = obsvPeopleCountDao.findPeopleCounterObservationsBySensor(sensor);
		Assert.assertNotNull(obsvPcList);
		Assert.assertEquals(3, obsvPcList.size());
		
		Set<ObsvPeopleCount> obsvPcSet = obsvPeopleCountDao.findDistinctPeopleCounterObservationsBySensor(sensor);

		Assert.assertNotNull(obsvPcSet);
		Assert.assertFalse(obsvPcSet.isEmpty());
		Assert.assertEquals(2, obsvPcSet.size());
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
		 Date startDate = df.parse("2014-06-05T09:00:00+00:00");
		 Date endDate = df.parse("2014-07-10T12:00:00+00:00"); 
		 Integer offset = 0; 
		 Integer limit = 10;
		 
		 List<ObsvPeopleCount> obsvList = obsvPeopleCountDao.findPeopleCounterObservationsBySensor(sensor, startDate, endDate, null, null, offset, limit);
		 Assert.assertNotNull(obsvList);
		 Assert.assertFalse(obsvList.isEmpty());
		 
		 startDate = df.parse("2014-06-05T11:00:00+00:00");
		 endDate = df.parse("2014-07-10T12:00:00+00:00"); 
		 obsvList = obsvPeopleCountDao.findPeopleCounterObservationsBySensor(sensor, startDate, endDate, null, null, offset, limit);
		 Assert.assertNotNull(obsvList);
		 Assert.assertTrue(obsvList.isEmpty());		
	}
	
	@Test
	public void testfindObservationsBySensor2() throws Exception {				
		Long dummySensorId = new Long(3000001);
		Sensors sensor = sensorsDao.get(dummySensorId);
		 //insert two new records
		// observation 1
		Integer peopleNumber1 = 33;
		Date obsvTime1 = parseTimestamp("2014-06-05 12:00:01");
		double latitude1 = 53.383;
		double longitude1 = -1.4659;
		
		Integer peopleNumber2 = 33;
		Date obsvTime2 = parseTimestamp("2014-06-05 13:00:01");
		double latitude2 = 53.383;
		double longitude2 = -1.4659;

		insertNewPeopleCountingObsvs(dummySensorId, peopleNumber1, obsvTime1,
				latitude1, longitude1);
		insertNewPeopleCountingObsvs(dummySensorId, peopleNumber2, obsvTime2,
				latitude2, longitude2);
		
		ISO8601DateFormat df = new ISO8601DateFormat();
		Date startDate = df.parse("2014-06-05T09:00:00+00:00");
		Date endDate = df.parse("2014-07-10T14:00:00+00:00"); 
		Integer offset = 0; 
		Integer limit = 10;
		 
		List<ObsvPeopleCount> obsvList = obsvPeopleCountDao.findPeopleCounterObservationsBySensor(sensor, startDate, endDate, null, null, offset, limit);
		Assert.assertNotNull(obsvList);
		Assert.assertFalse(obsvList.isEmpty());
		Assert.assertEquals(3, obsvList.size());
		
		offset = 0;
		limit = 1;
		
		obsvList = obsvPeopleCountDao.findPeopleCounterObservationsBySensor(sensor, startDate, endDate, null, null, offset, limit);
		Assert.assertNotNull(obsvList);
		Assert.assertFalse(obsvList.isEmpty());
		Assert.assertEquals(1, obsvList.size());		
	}

	private void insertNewPeopleCountingObsvs(final Long sensorId,
			final Integer peopleNumber, final Date obsvTime,
			final double latitude, final double longitude) {

		Sensors dummySensor = sensorsDao.get(sensorId);

		Assert.assertNotNull(dummySensor);
		Observations obsv = new Observations(dummySensor);

		ObsvPeopleCount peopleCounterObsv = new ObsvPeopleCount(obsv,
				peopleNumber, obsvTime);
		peopleCounterObsv.setLatitude(latitude);
		peopleCounterObsv.setLongitude(longitude);

		obsv.setObsvPeopleCount(peopleCounterObsv);
		peopleCounterObsv.setpCount(obsv);

		obsv = observationsDao.save(obsv);

		Assert.assertNotNull(peopleCounterObsv);
		Assert.assertNotNull(obsv.getObsvPeopleCount().getObsvId());
		Assert.assertNotNull(obsv.getObsvPeopleCount().getLatitude());
		Assert.assertNotNull(obsv.getObsvPeopleCount().getLongitude());
	}

	private Date parseTimestamp(String time) {
		SimpleDateFormat format = new java.text.SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		try {
			return format.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}
