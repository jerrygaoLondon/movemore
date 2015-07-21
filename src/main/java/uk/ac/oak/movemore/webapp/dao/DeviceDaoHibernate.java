package uk.ac.oak.movemore.webapp.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.appfuse.dao.hibernate.GenericDaoHibernate;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.oak.movemore.webapp.model.Device;

@Repository("deviceDao")
@Transactional
public class DeviceDaoHibernate extends GenericDaoHibernate<Device, Long>
		implements DeviceDao {

	public DeviceDaoHibernate() {
		super(Device.class);
	}

	public boolean isDevicePhysicalIdExist(String devicePhysicalId) {
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("devicePhysicalId", devicePhysicalId);

		List<Device> deviceList = findByNamedQuery("findDeviceByPhysicalId",
				queryParams);

		if (deviceList.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public Device findDeviceByPhysicalId(String devicePhysicalId)
			throws DeviceNotFoundException {
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("devicePhysicalId", devicePhysicalId);

		List<Device> deviceList = findByNamedQuery("findDeviceByPhysicalId",
				queryParams);

		if (deviceList == null || deviceList.size() == 0) {
			throw new DeviceNotFoundException("The device physical id {"
					+ devicePhysicalId + "} has not been found.");
		}

		return deviceList.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<Device> getAllDevices() {
		Criteria criteria = getSession().createCriteria(Device.class);		
		criteria.addOrder(Order.desc("created"));
		return criteria.list();
	}
	
	public void refresh(Device device) {
		getSession().refresh(device);
	}
}
