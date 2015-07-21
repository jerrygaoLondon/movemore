package uk.ac.oak.movemore.webapp.dao;

import org.appfuse.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

import uk.ac.oak.movemore.webapp.model.DeviceMessages;
import uk.ac.oak.movemore.webapp.model.DeviceMessagesId;

@Repository("deviceMessagesDao")
public class DeviceMessagesDaoHibernate extends
		GenericDaoHibernate<DeviceMessages, DeviceMessagesId> implements DeviceMessagesDao {

	public DeviceMessagesDaoHibernate() {
		super(DeviceMessages.class);
	}

}
