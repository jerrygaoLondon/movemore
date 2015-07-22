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
import uk.ac.oak.movemore.webapp.model.ObsvCarRegPlateDetection;
import uk.ac.oak.movemore.webapp.model.Sensors;

public class ObsvCarRegPlateDetectionDaoTest extends BaseDaoTestCase {

	@Autowired
	private ObservationsDao observationsDao;

	@Autowired
	private SensorsDao sensorsDao;

	@Autowired
	private ObsvCarRegPlateDetectionDao obsvCarRegPlateDetectionDao;

	@Test
	public void testInsert() throws Exception {
		Long sensorId = 3000001l;
		String carRegPlateNum = "A555WOW";
		Date obsvTime = new Date();
		double latitude = 53.383;
		double longitude = -1.4659;

		insertCarRegPlateDetectionObsvs(sensorId, carRegPlateNum, obsvTime,
				latitude, longitude);
	}

	@Test
	public void testFindCarRegPlateObservationsBySensor() throws Exception {
		// prepare test data
		Long sensorId = 3000001l;

		// observation 1
		String carRegPlateNum1 = "A555WOW";
		Date obsvTime1 = new Date();
		double latitude1 = 53.383;
		double longitude1 = -1.4659;

		// observation 2
		String carRegPlateNum2 = "A555FOF";
		Date obsvTime2 = new Date();
		double latitude2 = 53.383;
		double longitude2 = -1.4659;

		// observation 3
		String carRegPlateNum3 = "c518914cdda33ad6218c9b1dd6860620";
		Date obsvTime3 = new Date();
		double latitude3 = 53.383;
		double longitude3 = -1.4659;

		insertCarRegPlateDetectionObsvs(sensorId, carRegPlateNum1, obsvTime1,
				latitude1, longitude1);
		insertCarRegPlateDetectionObsvs(sensorId, carRegPlateNum2, obsvTime2,
				latitude2, longitude2);
		insertCarRegPlateDetectionObsvs(sensorId, carRegPlateNum3, obsvTime3,
				latitude3, longitude3);

		Sensors sensor = sensorsDao.get(sensorId);
		Assert.assertNotNull(sensor);

		List<ObsvCarRegPlateDetection> carRegPlateObsvs = obsvCarRegPlateDetectionDao
				.findCarRegPlateObservationsBySensor(sensor);
		Assert.assertNotNull(carRegPlateObsvs);
		Assert.assertFalse(carRegPlateObsvs.isEmpty());

		// it's four records, because we have one sample data
		Assert.assertEquals(4, carRegPlateObsvs.size());

		for (ObsvCarRegPlateDetection ocrp : carRegPlateObsvs) {
			if (ocrp.getCarRegPlateNum().equals(carRegPlateNum3)) {
				Assert.assertTrue(Double.valueOf(latitude3)
						.compareTo(ocrp.getLatitude()) == 0 || Double.valueOf(latitude3)
								.compareTo(ocrp.getLatitude()) == 1);
				Assert.assertTrue(Double.valueOf(longitude3)
						.compareTo(ocrp.getLongitude()) == 0 || Double.valueOf(longitude3)
						.compareTo(ocrp.getLongitude()) == 1);
				// ignore the HexCarRegPlateNum value for now
				// Assert.assertEquals(ObsvCarRegPlateDetection.convertCarRegPlateNum(carRegPlateNum3),
				// ocrp.getHexCarRegPlateNum());
			}
		}
	}

	@Test
	public void testDeleteAllCarRegPlateObservations() throws Exception {
		// prepare test data
		Long sensorId = 3000001l;

		// observation 1
		String carRegPlateNum1 = "A555WOW";
		Date obsvTime1 = new Date();
		double latitude1 = 53.383;
		double longitude1 = -1.4659;

		// observation 2
		String carRegPlateNum2 = "A555FOF";
		Date obsvTime2 = new Date();
		double latitude2 = 53.383;
		double longitude2 = -1.4659;

		insertCarRegPlateDetectionObsvs(sensorId, carRegPlateNum1, obsvTime1,
				latitude1, longitude1);
		insertCarRegPlateDetectionObsvs(sensorId, carRegPlateNum2, obsvTime2,
				latitude2, longitude2);

		Sensors sensor = sensorsDao.get(sensorId);
		Assert.assertNotNull(sensor);

		List<ObsvCarRegPlateDetection> carRegPlateObsvs = obsvCarRegPlateDetectionDao
				.findCarRegPlateObservationsBySensor(sensor);
		Assert.assertNotNull(carRegPlateObsvs);
		Assert.assertFalse(carRegPlateObsvs.isEmpty());

		// it's three records, because we have one sample data
		Assert.assertEquals(3, carRegPlateObsvs.size());

		observationsDao.removeAllObsvsBySensor(sensor);

		List<ObsvCarRegPlateDetection> carRegPlateObsvs2 = obsvCarRegPlateDetectionDao
				.findCarRegPlateObservationsBySensor(sensor);
		Assert.assertNotNull(carRegPlateObsvs2);
		Assert.assertTrue(carRegPlateObsvs2.isEmpty());
	}

	@Test
	public void testDuplicates() throws Exception {
		// prepare test data
		Long sensorId = 3000001l;

		// observation 1
		String carRegPlateNum1 = "A555WOW";
		Date obsvTime1 = new Date();
		double latitude1 = 53.383;
		double longitude1 = -1.4659;

		// observation 2
		String carRegPlateNum2 = "A555WOW";
		Date obsvTime2 = new Date();
		double latitude2 = 53.383;
		double longitude2 = -1.4659;

		insertCarRegPlateDetectionObsvs(sensorId, carRegPlateNum1, obsvTime1,
				latitude1, longitude1);
		insertCarRegPlateDetectionObsvs(sensorId, carRegPlateNum2, obsvTime2,
				latitude2, longitude2);

		Sensors sensor = sensorsDao.get(sensorId);
		Assert.assertNotNull(sensor);

		List<ObsvCarRegPlateDetection> carRegPlateObsvs = obsvCarRegPlateDetectionDao
				.findCarRegPlateObservationsBySensor(sensor);
		Assert.assertNotNull(carRegPlateObsvs);
		Assert.assertFalse(carRegPlateObsvs.isEmpty());
		Assert.assertEquals(3, carRegPlateObsvs.size());

		Set<ObsvCarRegPlateDetection> deDuplicateCarRegPlateObsvs = obsvCarRegPlateDetectionDao
				.findDistinctCarRegPlateObservationsBySensor(sensor);
		Assert.assertFalse(deDuplicateCarRegPlateObsvs.isEmpty());
		Assert.assertEquals(2, deDuplicateCarRegPlateObsvs.size());
	}

	private void insertCarRegPlateDetectionObsvs(final Long sensorId,
			final String carRegPlateNum, final Date obsvTime,
			final double latitude, final double longitude) {

		Sensors dummySensor = sensorsDao.get(sensorId);

		Assert.assertNotNull(dummySensor);
		Observations obsv = new Observations(dummySensor);

		ObsvCarRegPlateDetection carPlateObsv = new ObsvCarRegPlateDetection(
				obsv, carRegPlateNum, obsvTime);
		carPlateObsv.setLatitude(latitude);
		carPlateObsv.setLongitude(longitude);

		obsv.setObsvCarRegPlateDetect(carPlateObsv);

		obsv = observationsDao.save(obsv);

		Assert.assertNotNull(carPlateObsv);
		Assert.assertNull(obsv.getLatitude());
		Assert.assertNull(obsv.getLongitude());
		Assert.assertNotNull(obsv.getObsvCarRegPlateDetect().getObsvId());
		Assert.assertNotNull(obsv.getObsvCarRegPlateDetect().getLatitude());
		Assert.assertNotNull(obsv.getObsvCarRegPlateDetect().getLongitude());

		Assert.assertEquals(
				0,
				Double.valueOf(latitude).compareTo(
						obsv.getObsvCarRegPlateDetect().getLatitude()));
		Assert.assertEquals(
				0,
				Double.valueOf(longitude).compareTo(
						obsv.getObsvCarRegPlateDetect().getLongitude()));

		// Ignore the car plate number hashing for now
		// Assert.assertEquals(carRegPlateNum,
		// obsv.getObsvCarRegPlateDetect().getCarRegPlateNum());

		/*
		 * BigInteger hexCarRegPlateNum =
		 * ObsvCarRegPlateDetection.convertCarRegPlateNum(carRegPlateNum);
		 * Assert.assertEquals(hexCarRegPlateNum,
		 * obsv.getObsvCarRegPlateDetect().getCarRegPlateNum());
		 */
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
		 
		 List<ObsvCarRegPlateDetection> obsvList = obsvCarRegPlateDetectionDao.findCarRegPlateObservationsBySensor(sensor, startDate, endDate, null, null, offset, limit);
		 Assert.assertNotNull(obsvList);
		 Assert.assertFalse(obsvList.isEmpty());
	}
}
