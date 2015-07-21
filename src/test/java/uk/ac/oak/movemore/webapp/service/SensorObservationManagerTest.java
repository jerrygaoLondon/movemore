package uk.ac.oak.movemore.webapp.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.appfuse.service.BaseManagerTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.oak.movemore.webapp.dao.ObsvActivityDetectionDao;
import uk.ac.oak.movemore.webapp.dao.ObsvActivityNormDao;
import uk.ac.oak.movemore.webapp.model.ObsvActivityDetection;
import uk.ac.oak.movemore.webapp.model.ObsvCarRegPlateDetection;
import uk.ac.oak.movemore.webapp.model.ObsvDeviceDetection;
import uk.ac.oak.movemore.webapp.model.ObsvPeopleCount;
import uk.ac.oak.movemore.webapp.model.Sensors;
import uk.ac.oak.movemore.webapp.service.response.JSONResponse;
import uk.ac.oak.movemore.webapp.service.response.ObservationCollection;
import uk.ac.oak.movemore.webapp.service.response.SensorObservationSuccess;
import uk.ac.oak.movemore.webapp.util.SensorTypeEnum;

public class SensorObservationManagerTest extends BaseManagerTestCase {
	
	@Autowired
	private SensorObservationManager sensorObservationManager;
	
	@Autowired
	private SensorManager sensorManager;
	
	@Autowired	
	private ObsvActivityNormDao obsvActivityNormDao;
	
	@Autowired
	private ObsvActivityDetectionDao obsvActivityDetectionDao;

	//@Test
	public void testGetSensorObservationBySensorId() throws Exception {
		String sensorId = "3000001";

		JSONResponse resp = sensorObservationManager.getSensorObservationBySensorId(sensorId);
		
		Assert.assertTrue(resp instanceof ObservationCollection);
		
		Assert.assertNotNull(((ObservationCollection)resp).getObservationDetails());
		//7 sample data
		Assert.assertEquals(7, ((ObservationCollection)resp).getObservationDetails().size());
	}
	
	//@Test
	public void testSaveObservationForUnclassifiedSensorsObsvs() throws Exception {
		String sensorId = "3000001";
		String sensorPhysicalId = "b827eb2eb26e";
		String time=new Timestamp(new Date().getTime()).toString();
		List<String> values = new ArrayList<String>();
		values.add("70:56:81:B2:38:2D,-1");
		values.add("A4:67:06:9B:F4:2D,-88");
		values.add("0C:84:DC:CD:4C:E5,-127");
		
		//JSONResponse saveObsvResp = sensorObservationManager.saveObservation(sensorPhysicalId, time, values);
		Response successResp = sensorObservationManager.saveObservation(sensorPhysicalId, time, values);
		
		//Assert.assertTrue(saveObsvResp instanceof SensorObservationSuccess);
		Assert.assertEquals(Status.OK, successResp.getStatus());
		
		JSONResponse getObsvsResp = sensorObservationManager.getSensorObservationBySensorId(sensorId);
		
		Assert.assertTrue(getObsvsResp instanceof ObservationCollection);
		
		Assert.assertNotNull(((ObservationCollection)getObsvsResp).getObservationDetails());
		//7 sample date plus 3 records
		Assert.assertEquals(10, ((ObservationCollection)getObsvsResp).getObservationDetails().size());
	}
	
	//@Test
	public void testSaveObservationForBluetoothSensorsObsvs() throws Exception {
		Long sensorId = 3000001l;
		
		List<String> values = new ArrayList<String>();
		values.add("70:56:81:B2:38:2D");
		values.add("A4:67:06:9B:F4:2D");
		values.add("0C:84:DC:CD:4C:E5");
		String time=new Timestamp(new Date().getTime()).toString();
		
		Sensors sensor = sensorManager.updateSensorType(sensorId, SensorTypeEnum.SENSOR_TYPE_BLUETOOTH_SENSOR.getSensorType());
		
		//JSONResponse saveObsvResp = sensorObservationManager.saveObservation(sensor.getSensorPhysicalId(), time, values);
		Response successResp = sensorObservationManager.saveObservation(sensor.getSensorPhysicalId(), time, values);
		 
		//Assert.assertTrue(saveObsvResp instanceof SensorObservationSuccess);
		Assert.assertEquals(Status.OK, successResp.getStatus());
		
//		String obsvId = ((SensorObservationSuccess)saveObsvResp).getObsvId();
//		
//		Assert.assertNull(obsvId);

		Set<ObsvDeviceDetection> bluetoothObsvs = sensorObservationManager.findDetectedDeviceObsvsBySensor(sensor);
		Assert.assertNotNull(bluetoothObsvs);
		Assert.assertFalse(bluetoothObsvs.isEmpty());
		Assert.assertEquals(4, bluetoothObsvs.size());
		
		for (ObsvDeviceDetection obsvDevice : bluetoothObsvs) {
			Assert.assertNotNull(obsvDevice.getMacAddress());
			Assert.assertNotNull(obsvDevice.getLatitude());
			Assert.assertNotNull(obsvDevice.getLongitude());
			//test by excluding the sample data
			if (values.contains(obsvDevice.getMacAddress())) {
				Assert.assertNull(obsvDevice.getSignalStrength());
			}
			Assert.assertNotNull(obsvDevice.getDevDetect());
			Assert.assertNotNull(obsvDevice.getObsvTime());
		}
		
		Set<ObsvPeopleCount> obsvPeopleCounts = sensorObservationManager.findPeopleCounterObservationsBySensor(sensor);
		Assert.assertNotNull(obsvPeopleCounts);
		Assert.assertFalse(obsvPeopleCounts.isEmpty());
		//should only one sample data there
		Assert.assertEquals(1, obsvPeopleCounts.size());
		
		Set<ObsvCarRegPlateDetection> obsvRegPlateObsvs = sensorObservationManager.findCarRegPlateObservationsBySensor(sensor);
		Assert.assertNotNull(obsvRegPlateObsvs);
		Assert.assertFalse(obsvRegPlateObsvs.isEmpty());
		//should only one sample data there
		Assert.assertEquals(1, obsvRegPlateObsvs.size());
	}

	@Test
	public void testBulkUploadingTrainingActivityData() throws Exception {
		File trainingActivityTestFile = new File(Paths.get("").toAbsolutePath().toString()+"/src/test/resources/422147750.tcx");

		String dummySensorPhysicalId = "b827eb2eb26e";
		
		InputStream activityDatafileStream = new FileInputStream(trainingActivityTestFile);

		Assert.assertNotNull(activityDatafileStream);
		
		Response response = sensorObservationManager.sensorDataBulkUpLoad(dummySensorPhysicalId, activityDatafileStream, trainingActivityTestFile.getName());
		
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	//@Test
	public void testSaveObservationForWifiSensorsObsvs() throws Exception {
		Long sensorId = 3000001l;
		
		String macAddress1 = "70:56:81:B2:38:2D";
		String macAddress2 = "A4:67:06:9B:F4:2D";
		String macAddress3 = "0C:84:DC:CD:4C:E5";
		Integer signalStrength1 = -1;
		Integer signalStrength2 = -88;
		Integer signalStrength3 = -127;
		
		List<String> values = new ArrayList<String>();
		values.add("70:56:81:B2:38:2D,-1");
		values.add("A4:67:06:9B:F4:2D,-88");
		values.add("0C:84:DC:CD:4C:E5,-127");
		
		String time=new Timestamp(new Date().getTime()).toString();
		
		Sensors sensor = sensorManager.updateSensorType(sensorId, SensorTypeEnum.SENSOR_TYPE_WIFI_SENSOR.getSensorType());
		//JSONResponse saveObsvResp = sensorObservationManager.saveObservation(sensor.getSensorPhysicalId(), time, values);
		Response successResp = sensorObservationManager.saveObservation(sensor.getSensorPhysicalId(), time, values);
				
//		Assert.assertTrue(saveObsvResp instanceof SensorObservationSuccess);
		Assert.assertEquals(Status.OK, successResp.getStatus());
		
//		String obsvId = ((SensorObservationSuccess)saveObsvResp).getObsvId();
//		
//		Assert.assertNull(obsvId);

		Set<ObsvDeviceDetection> wifiDeviceObsvs = sensorObservationManager.findDetectedDeviceObsvsBySensor(sensor);
		Assert.assertNotNull(wifiDeviceObsvs);
		Assert.assertFalse(wifiDeviceObsvs.isEmpty());
		Assert.assertEquals(4, wifiDeviceObsvs.size());
		
		for (ObsvDeviceDetection obsvDevice : wifiDeviceObsvs) {
			//Assert.assertNotNull(obsvDevice.getHexMacAddress());
			Assert.assertNotNull(obsvDevice.getMacAddress());
			Assert.assertNotNull(obsvDevice.getLatitude());
			Assert.assertNotNull(obsvDevice.getLongitude());
			Assert.assertNotNull(obsvDevice.getDevDetect());
			Assert.assertNotNull(obsvDevice.getSignalStrength());
			Assert.assertNotNull(obsvDevice.getObsvTime());
			
			if (macAddress1.equals(obsvDevice.getMacAddress())) {
				Assert.assertTrue(signalStrength1.equals(obsvDevice.getSignalStrength()));
			}
			if(macAddress2.equals(obsvDevice.getMacAddress())) {
				Assert.assertTrue(signalStrength2.equals(obsvDevice.getSignalStrength()));
			}
			if(macAddress3.equals(obsvDevice.getMacAddress())) {
				Assert.assertTrue(signalStrength3.equals(obsvDevice.getSignalStrength()));
			}
		}		
		
		Set<ObsvPeopleCount> obsvPeopleCounts = sensorObservationManager.findPeopleCounterObservationsBySensor(sensor);
		Assert.assertNotNull(obsvPeopleCounts);
		Assert.assertFalse(obsvPeopleCounts.isEmpty());
		//should only one sample data there
		Assert.assertEquals(1, obsvPeopleCounts.size());
		
		Set<ObsvCarRegPlateDetection> obsvRegPlateObsvs = sensorObservationManager.findCarRegPlateObservationsBySensor(sensor);
		Assert.assertNotNull(obsvRegPlateObsvs);
		Assert.assertFalse(obsvRegPlateObsvs.isEmpty());
		//should only one sample data there
		Assert.assertEquals(1, obsvRegPlateObsvs.size());
	}
	
	//@Test
	public void testSaveObservationForPeopleNumCountingSensorsObsvs() throws Exception {
		Long sensorId = 3000001l;
		
		Integer numOfFace = 30;
		List<String> values = new ArrayList<String>();
		values.add("30");
		String time=new Timestamp(new Date().getTime()).toString();
		
		Sensors sensor = sensorManager.updateSensorType(sensorId, SensorTypeEnum.SENSOR_TYPE_PEOPLE_COUNTER.getSensorType());
//		JSONResponse saveObsvResp = sensorObservationManager.saveObservation(sensor.getSensorPhysicalId(), time, values);
		Response successResp = sensorObservationManager.saveObservation(sensor.getSensorPhysicalId(), time, values);
		
//		Assert.assertTrue(saveObsvResp instanceof SensorObservationSuccess);
		Assert.assertEquals(Status.OK, successResp.getStatus());
		
//		String obsvId = ((SensorObservationSuccess)saveObsvResp).getObsvId();
//		
//		Assert.assertNull(obsvId);
		
		Set<ObsvPeopleCount> obsvPplCounts = sensorObservationManager.findPeopleCounterObservationsBySensor(sensor);
		Assert.assertNotNull(obsvPplCounts);
		Assert.assertFalse(obsvPplCounts.isEmpty());
		Assert.assertEquals("Except 2 results with 1 sample data", 2, obsvPplCounts.size());
	
		for (ObsvPeopleCount pcount : obsvPplCounts) {
			Assert.assertNotNull(pcount.getObsvId());
			Assert.assertNotNull(pcount.getObsvTime());
			Assert.assertNotNull(pcount.getpCount());
			Assert.assertNotNull(pcount.getNumber());
			if (Timestamp.valueOf(time).equals(pcount.getObsvTime())) {
				Assert.assertEquals(numOfFace, pcount.getNumber());
			}
			
			Assert.assertNotNull(pcount.getLatitude());
			Assert.assertNotNull(pcount.getLongitude());			
		}
		
		Set<ObsvCarRegPlateDetection> obsvRegPlateObsvs = sensorObservationManager.findCarRegPlateObservationsBySensor(sensor);
		Assert.assertNotNull(obsvRegPlateObsvs);
		Assert.assertFalse(obsvRegPlateObsvs.isEmpty());
		
		Assert.assertEquals("Except only one sample data there", 1, obsvRegPlateObsvs.size());
		
		Set<ObsvDeviceDetection> deviceObsvs = sensorObservationManager.findDetectedDeviceObsvsBySensor(sensor);
		Assert.assertNotNull(deviceObsvs);
		Assert.assertFalse(deviceObsvs.isEmpty());
		Assert.assertEquals("Except only one sample data there", 1, deviceObsvs.size());
	}
	
	//@Test
	public void testSaveObservationForCarPlateNumSensorObsvs() throws Exception {
		Long sensorId = 3000001l;
		
		String carRegPlateNum1 = "A555WOW";
		String carRegPlateNum2 = "c518914cdda33ad6218c9b1dd6860620";
		
		List<String> values = new ArrayList<String>();
		values.add(carRegPlateNum1);
		values.add(carRegPlateNum2);
		
		String time=new Timestamp(new Date().getTime()).toString();
		
		Sensors sensor = sensorManager.updateSensorType(sensorId, SensorTypeEnum.SENSOR_TYPE_CAR_REG_PLATE_SENSOR.getSensorType());
//		JSONResponse saveObsvResp = sensorObservationManager.saveObservation(sensor.getSensorPhysicalId(), time, values);
		Response successResp = sensorObservationManager.saveObservation(sensor.getSensorPhysicalId(), time, values);
		
//		Assert.assertTrue(saveObsvResp instanceof SensorObservationSuccess);
		Assert.assertEquals(Status.OK, successResp.getStatus());
//		String obsvId = ((SensorObservationSuccess)saveObsvResp).getObsvId();
//		
//		Assert.assertNull(obsvId);
		
		Set<ObsvCarRegPlateDetection> carRegPlateObsvs = sensorObservationManager.findCarRegPlateObservationsBySensor(sensor);
		Assert.assertNotNull(carRegPlateObsvs);
		Assert.assertFalse(carRegPlateObsvs.isEmpty());
		Assert.assertEquals("Except 3 results with 1 sample data", 3, carRegPlateObsvs.size());
		
		for (ObsvCarRegPlateDetection carRegPlateObsv : carRegPlateObsvs) {
			Assert.assertNotNull(carRegPlateObsv.getObsvId());
			Assert.assertNotNull(carRegPlateObsv.getObsvTime());
			Assert.assertNotNull(carRegPlateObsv.getCarRegPlate());
/*			if (carRegPlateNum2.equals(carRegPlateObsv.getCarRegPlateNum())) {
				Assert.assertNotNull(carRegPlateObsv.getHexCarRegPlateNum());
			}*/
			Assert.assertNotNull(carRegPlateObsv.getCarRegPlateNum());
			Assert.assertNotNull(carRegPlateObsv.getLatitude());
			Assert.assertNotNull(carRegPlateObsv.getLongitude());
			Assert.assertNotNull(carRegPlateObsv.getCreated());
		}
		
		Set<ObsvDeviceDetection> deviceObsvs = sensorObservationManager.findDetectedDeviceObsvsBySensor(sensor);
		Assert.assertNotNull(deviceObsvs);
		Assert.assertFalse(deviceObsvs.isEmpty());
		Assert.assertEquals("Except only one sample data there", 1, deviceObsvs.size());
		
		Set<ObsvPeopleCount> obsvPplCounts = sensorObservationManager.findPeopleCounterObservationsBySensor(sensor);
		Assert.assertNotNull(obsvPplCounts);
		Assert.assertFalse(obsvPplCounts.isEmpty());
		Assert.assertEquals("Except 1 sample data", 1, obsvPplCounts.size());

	}
	
	//JSONResponse pushSensorObservation(String sensorId, String value,String obsvTime);
	public void testpushSensorObservationForBluetoothSensorObsvs() throws Exception {
		
	}
	
	public void testpushSensorObservationForWifiDeviceSensorObsvs() throws Exception {
		
	}
	
	//@Test
	public void testpushSensorObservationForPeopleNumCountingSensorObsvs() throws Exception {
		Long sensorId = 3000001l;
		
		Integer numOfFace = 30;
		String time=new Timestamp(new Date().getTime()).toString();
		
		Sensors sensor = sensorManager.updateSensorType(sensorId, SensorTypeEnum.SENSOR_TYPE_PEOPLE_COUNTER.getSensorType());
		JSONResponse saveObsvResp = sensorObservationManager.pushSensorObservation(sensorId.toString(), numOfFace.toString(), time);
		
		Assert.assertTrue(saveObsvResp instanceof SensorObservationSuccess);
		String obsvId = ((SensorObservationSuccess)saveObsvResp).getObsvId();
		
		Assert.assertNotNull(obsvId);
		
		Set<ObsvPeopleCount> obsvPplCounts = sensorObservationManager.findPeopleCounterObservationsBySensor(sensor);
		Assert.assertNotNull(obsvPplCounts);
		Assert.assertFalse(obsvPplCounts.isEmpty());
		Assert.assertEquals("Except 2 results with 1 sample data", 2, obsvPplCounts.size());
	
		for (ObsvPeopleCount pcount : obsvPplCounts) {
			Assert.assertNotNull(pcount.getObsvId());
			Assert.assertNotNull(pcount.getObsvTime());
			Assert.assertNotNull(pcount.getpCount());
			Assert.assertNotNull(pcount.getNumber());
			if (Timestamp.valueOf(time).equals(pcount.getObsvTime())) {
				Assert.assertEquals(numOfFace, pcount.getNumber());
			}
			
			Assert.assertNotNull(pcount.getLatitude());
			Assert.assertNotNull(pcount.getLongitude());			
		}
		
		Set<ObsvCarRegPlateDetection> obsvRegPlateObsvs = sensorObservationManager.findCarRegPlateObservationsBySensor(sensor);
		Assert.assertNotNull(obsvRegPlateObsvs);
		Assert.assertFalse(obsvRegPlateObsvs.isEmpty());
		
		Assert.assertEquals("Except only one sample data there", 1, obsvRegPlateObsvs.size());
		
		Set<ObsvDeviceDetection> deviceObsvs = sensorObservationManager.findDetectedDeviceObsvsBySensor(sensor);
		Assert.assertNotNull(deviceObsvs);
		Assert.assertFalse(deviceObsvs.isEmpty());
		Assert.assertEquals("Except only one sample data there", 1, deviceObsvs.size());

	}
	
	public void testpushSensorObservationForCarPlateNumSensorObsvs() throws Exception {
		
	}
	
//	@Test
	public void testnormaliseObservation() throws Exception {
		Long obsvId = 4837200l;
		String finalActivityType = "Running";
		String finalLatitude = "53.3712";
		String finalLongitude = "-1.4935";
		String finalConfidence = "98";
		
		Response resp = sensorObservationManager.normaliseObservation(obsvId, finalActivityType, finalLatitude, finalLongitude, finalConfidence);
		
		Assert.assertTrue(obsvActivityNormDao.exists(obsvId));
		
		ObsvActivityDetection obsvActivity = obsvActivityDetectionDao.get(obsvId);
		Assert.assertNotNull(obsvActivity);
		Assert.assertNotNull(obsvActivity.getObsvActivityNorm());
	}
	
//	@Test
	public void testgetSensorObservationBySensorId() throws Exception {
		String sensorId = "356194054489613-Activity";
		String startDate = "2014-07-13T00:00:00+00:00";
		String endDate = "2014-07-14T23:00:00+00:00";
		Integer offset = 0;
		Integer limit = 1000;
		
		Response resp = sensorObservationManager.getSensorObservationBySensorId(sensorId, startDate, endDate, null, null, offset, limit);
		System.out.println(resp.getEntity().toString());
	}
}
