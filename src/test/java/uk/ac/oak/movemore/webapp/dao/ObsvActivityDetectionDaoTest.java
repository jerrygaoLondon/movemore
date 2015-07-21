package uk.ac.oak.movemore.webapp.dao;

import java.util.List;

import org.appfuse.dao.BaseDaoTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.oak.movemore.webapp.model.ObsvActivityDetection;
import uk.ac.oak.movemore.webapp.model.Sensors;

public class ObsvActivityDetectionDaoTest extends BaseDaoTestCase {

	@Autowired
	private ObsvActivityDetectionDao obsvActivityDetectionDao;
	
	@Autowired
	private ObservationsDao observationsDao;

	@Autowired
	private SensorsDao sensorsDao;
	
	@Test
	public void testFindActivityObservationsBySensor () {
		// prepare test data
		Long sensorId = 3000001l;
				
		Sensors sensor = sensorsDao.get(sensorId);
		Assert.assertNotNull(sensor);
		//find sample value
		List<ObsvActivityDetection> obsvActivityDetectionList = obsvActivityDetectionDao.findActivityObservationsBySensor(sensor, null, null, null, null, 0, 1000);
		
		Assert.assertNotNull(obsvActivityDetectionList);
		Assert.assertFalse(obsvActivityDetectionList.isEmpty());
		Assert.assertEquals(1, obsvActivityDetectionList.size());
	}
}
