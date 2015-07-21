package uk.ac.oak.movemore.webapp.dao;

import java.util.List;

import org.appfuse.dao.BaseDaoTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class SensorDaoTest extends BaseDaoTestCase {
	@Autowired
	private SensorsDao sensorDao;
	
	@Test
	public void testCountSensorsByType() {
		List<String[]> resList = sensorDao.countSensorsByType();
		Assert.notNull(resList);
	}
}
