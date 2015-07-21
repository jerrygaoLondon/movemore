package uk.ac.oak.movemore.webapp.dao;

import java.util.List;

import org.appfuse.dao.GenericDao;

import uk.ac.oak.movemore.webapp.model.Device;

public interface DeviceDao extends GenericDao<Device, Long> {

	boolean isDevicePhysicalIdExist(String devicePhysicalId);
	Device findDeviceByPhysicalId(String devicePhysicalId) throws DeviceNotFoundException;
	List<Device> getAllDevices();
}
