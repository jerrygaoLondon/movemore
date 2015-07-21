package uk.ac.oak.movemore.webapp.dao;

import org.appfuse.dao.BaseDaoTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.oak.movemore.webapp.model.Device;

public class DeviceDaoTest extends BaseDaoTestCase {
	@Autowired
	private DeviceDao deviceDao;
	
	@Test
	public void testIsDevicePhysicalIdExist() throws Exception {
		String devicePhysicalId="00CC02EE038C";
		boolean exists = deviceDao.isDevicePhysicalIdExist(devicePhysicalId);
		Assert.assertTrue(exists);
		
		String virtualdevicePhysicalId="testtest111";
		boolean notExist = deviceDao.isDevicePhysicalIdExist(virtualdevicePhysicalId);
		Assert.assertFalse(notExist);
	}
	
	@Test
	public void testFindDeviceByPhysicalId() throws Exception {
		String devicePhysicalId="00CC02EE038C";
		Device device = deviceDao.findDeviceByPhysicalId(devicePhysicalId);
		Assert.assertNotNull(device);
		Assert.assertEquals(Long.valueOf(1000001l), device.getDeviceId());
	}
	
	@Test(expected=DeviceNotFoundException.class)
	public void testNotFindDeviceByPhysicalId() throws Exception{
		String devicePhysicalId="testtest111";
		deviceDao.findDeviceByPhysicalId(devicePhysicalId);
	}
}
