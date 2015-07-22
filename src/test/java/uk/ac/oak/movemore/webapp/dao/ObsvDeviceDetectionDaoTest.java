package uk.ac.oak.movemore.webapp.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.appfuse.dao.BaseDaoTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.model.ObsvDeviceDetection;
import uk.ac.oak.movemore.webapp.model.Sensors;

public class ObsvDeviceDetectionDaoTest extends BaseDaoTestCase {
	
	@Autowired
	private ObsvDeviceDetectionDao obsvDeviceDetectionDao;
	
	@Autowired
	private ObservationsDao observationsDao;

	@Autowired
	private SensorsDao sensorsDao;
		
	//@Test
	public void testInsert() throws Exception {
		Long sensorId = 3000001l;
		String macAddress = "01:68:5C:94:78:24";
		Date obsvTime = new Date();
		double latitude = 53.383;
		double longitude = -1.4659;
		Integer signalStrength = new Integer(-35);
		
		insertNewDeviceDetectionObsvs(sensorId, macAddress, obsvTime, latitude, longitude, signalStrength);
	}
	
	@Test
	public void testFindDeviceObservationsBySensor() throws Exception {
		// prepare test data
		Long sensorId = 3000001l;
		
		// observation 1
		String macAddress1 = "01:68:5C:EE:88:24";
		Date obsvTime1 = new Date();
		double latitude1 = 53.383;
		double longitude1 = -1.4659;
		Integer signalStrength1 = new Integer(-60);
		
		// observation 2
		String macAddress2 = "01:68:66:EE:88:24";
		Date obsvTime2 = new Date();
		double latitude2 = 53.383;
		double longitude2 = -1.4659;
		Integer signalStrength2 = new Integer(-63);
		
		// observation 3
		String macAddress3 = "01:68:66:EE:88:CC";
		Date obsvTime3 = new Date();
		double latitude3 = 53.383;
		double longitude3 = -1.4659;
		Integer signalStrength3 = new Integer(-10);
		
		insertNewDeviceDetectionObsvs(sensorId, macAddress1, obsvTime1, latitude1, longitude1, signalStrength1);
		insertNewDeviceDetectionObsvs(sensorId, macAddress2, obsvTime2, latitude2, longitude2, signalStrength2);
		insertNewDeviceDetectionObsvs(sensorId, macAddress3, obsvTime3, latitude3, longitude3, signalStrength3);
		
		Sensors sensor = sensorsDao.get(sensorId);
		Assert.assertNotNull(sensor);
		
		List<ObsvDeviceDetection> obsvDeviceDetections = obsvDeviceDetectionDao.findDeviceObservationsBySensor(sensor);
		Assert.assertNotNull(obsvDeviceDetections);
		Assert.assertFalse(obsvDeviceDetections.isEmpty());
		// it's four records, because we have one sample data
		Assert.assertEquals(4, obsvDeviceDetections.size());
		
		for (ObsvDeviceDetection odd : obsvDeviceDetections) {
			if (odd.getMacAddress().equals(macAddress2)) {
				Assert.assertEquals(0, Double.valueOf(latitude3)
						.compareTo(odd.getLatitude()));
				Assert.assertEquals(0, Double.valueOf(longitude3)
						.compareTo(odd.getLongitude()));
				Assert.assertEquals(signalStrength2, odd.getSignalStrength());
			}
		}
	}
	
	@Test
	public void testDeleteAllDeviceObservations() throws Exception {
		// prepare test data
		Long sensorId = 3000001l;
		// observation 1
		String macAddress1 = "01:68:5C:EE:88:24";
		Date obsvTime1 = new Date();
		double latitude1 = 53.383;
		double longitude1 = -1.4659;
		Integer signalStrength1 = new Integer(-60);
		
		// observation 2
		String macAddress2 = "01:68:66:EE:88:24";
		Date obsvTime2 = new Date();
		double latitude2 = 53.383;
		double longitude2 = -1.4659;
		Integer signalStrength2 = new Integer(-63);
		
		insertNewDeviceDetectionObsvs(sensorId, macAddress1, obsvTime1, latitude1, longitude1, signalStrength1);
		insertNewDeviceDetectionObsvs(sensorId, macAddress2, obsvTime2, latitude2, longitude2, signalStrength2);
		
		Sensors sensor = sensorsDao.get(sensorId);
		Assert.assertNotNull(sensor);
		
		List<ObsvDeviceDetection> obsvDeviceDetections = obsvDeviceDetectionDao.findDeviceObservationsBySensor(sensor);
		Assert.assertNotNull(obsvDeviceDetections);
		Assert.assertFalse(obsvDeviceDetections.isEmpty());
		// it's three records, because we have one sample data
		Assert.assertEquals(3, obsvDeviceDetections.size());
		
		observationsDao.removeAllObsvsBySensor(sensor);
		
		List<ObsvDeviceDetection> obsvDeviceDetectionsRemoved = obsvDeviceDetectionDao.findDeviceObservationsBySensor(sensor);
		Assert.assertNotNull(obsvDeviceDetectionsRemoved);
		Assert.assertTrue(obsvDeviceDetectionsRemoved.isEmpty());
	}
	
	@Test
	public void testDuplicates() throws Exception{
		// prepare test data
		Long sensorId = 3000001l;
		// observation 1
		String macAddress1 = "01:68:5C:EE:88:24";
		Date obsvTime1 = new Date();
		double latitude1 = 53.383;
		double longitude1 = -1.4659;
		Integer signalStrength1 = new Integer(-60);
		
		// observation 2
		// 9bksI2bUpbVYRJmIR7H+qVddUfcTpL8NCKkoQ59j9Oc=
		String macAddress2 = "01:68:5C:EE:88:24";
		Date obsvTime2 = new Date();
		double latitude2 = 53.383;
		double longitude2 = -1.4659;
		Integer signalStrength2 = new Integer(-60);
		
		insertNewDeviceDetectionObsvs(sensorId, macAddress1, obsvTime1, latitude1, longitude1, signalStrength1);
		insertNewDeviceDetectionObsvs(sensorId, macAddress2, obsvTime2, latitude2, longitude2, signalStrength2);
		
		Sensors sensor = sensorsDao.get(sensorId);
		Assert.assertNotNull(sensor);
		
		List<ObsvDeviceDetection> obsvDeviceDetections = obsvDeviceDetectionDao.findDeviceObservationsBySensor(sensor);
		Assert.assertNotNull(obsvDeviceDetections);
		Assert.assertFalse(obsvDeviceDetections.isEmpty());
		// it's three records, because we have one sample data
		Assert.assertEquals(3, obsvDeviceDetections.size());
		
		Set<ObsvDeviceDetection> deDuplicateObsvDeviceDetections = obsvDeviceDetectionDao.findDistinctDeviceObservationsBySensor(sensor);
		Assert.assertFalse(deDuplicateObsvDeviceDetections.isEmpty());
		Assert.assertEquals(2, deDuplicateObsvDeviceDetections.size());
	}
	
	private void insertNewDeviceDetectionObsvs(final Long sensorId,
			final String macAddress, final Date obsvTime,
			final double latitude, final double longitude,
			Integer signalStrength) {

		Sensors dummySensor = sensorsDao.get(sensorId);

		Assert.assertNotNull(dummySensor);
		Observations obsv = new Observations(dummySensor);

		ObsvDeviceDetection deviceDetectionObsv = new ObsvDeviceDetection(obsv,
				macAddress, obsvTime);
		deviceDetectionObsv.setSignalStrength(signalStrength);
		deviceDetectionObsv.setLatitude(latitude);
		deviceDetectionObsv.setLongitude(longitude);

		obsv.setObsvDeviceDetect(deviceDetectionObsv);
		deviceDetectionObsv.setDevDetect(obsv);

		obsv = observationsDao.save(obsv);

		Assert.assertNotNull(deviceDetectionObsv);
		Assert.assertNull(obsv.getLatitude());
		Assert.assertNull(obsv.getLongitude());
		Assert.assertNotNull(obsv.getObsvDeviceDetect().getObsvId());
		Assert.assertNotNull(obsv.getObsvDeviceDetect().getLatitude());
		Assert.assertNotNull(obsv.getObsvDeviceDetect().getLongitude());
		
		Assert.assertEquals(0, Double.valueOf(latitude)
				.compareTo(obsv.getObsvDeviceDetect().getLatitude()));
		Assert.assertEquals(0, Double.valueOf(longitude)
				.compareTo(obsv.getObsvDeviceDetect().getLongitude()));
		
		Assert.assertEquals(signalStrength, obsv.getObsvDeviceDetect().getSignalStrength());
		
		//Long hexMacAddress = MACAddress.valueOf(macAddress).toLong();
		
		//Assert.assertEquals(hexMacAddress, obsv.getObsvDeviceDetect().getHexMacAddress());
	}
	
	@Test
	public void testfindObservationsBySensor1() throws Exception {		 
		 //load sample sensor data
		 Long dummySensorId = new Long(3000001);
		 Sensors sensor = sensorsDao.get(dummySensorId);
		 
		 Assert.assertNotNull("Sensor does not exist", sensor);
		 Assert.assertNotNull(sensor.getSensorId());

		 Date startDate = null;
		 Date endDate = null;
		 Integer offset = null;
		 Integer limit = null;
		 
		 List<ObsvDeviceDetection> obsvList = obsvDeviceDetectionDao.findDeviceObservationsBySensor(sensor, startDate, endDate, null, null, offset, limit);
		 Assert.assertNotNull(obsvList);
		 Assert.assertFalse(obsvList.isEmpty());
		 Assert.assertEquals(1, obsvList.size());
	}
	
	@Test
	public void testaggregateDeviceObservationBySensor() throws Exception {
		 Long dummySensorId = new Long(3000001);
		 Sensors sensor = sensorsDao.get(dummySensorId);
		 
		 List<Object[]> results = obsvDeviceDetectionDao.aggregateDeviceObservationBySensor(sensor, null, null);
		 
		 Assert.assertNotNull(results);
		 Assert.assertEquals(1, results.size());

//		 for (Object[] objArr : results) {
//			 Date time = (Date)objArr[0];
//			 String utcTime = DateUtil.convertUTCDateToLocalTime(time);
//			 System.out.println(utcTime +","+objArr[1]);
//		 }
	}

}
