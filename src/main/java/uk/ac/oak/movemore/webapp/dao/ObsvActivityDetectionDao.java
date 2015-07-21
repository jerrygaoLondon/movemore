package uk.ac.oak.movemore.webapp.dao;

import java.util.Date;
import java.util.List;

import org.appfuse.dao.GenericDao;

import uk.ac.oak.movemore.webapp.model.ObsvActivityDetection;
import uk.ac.oak.movemore.webapp.model.Sensors;

public interface ObsvActivityDetectionDao extends GenericDao<ObsvActivityDetection, Long> {

	List<ObsvActivityDetection> findActivityObservationsBySensor(Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc, Integer offset, Integer limit);
	
	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @return Object[] : 0 - activityType; 1 - counts;
	 */
	List<Object[]> countSensorObsvSubCategories(Sensors sensor, Date startDate, Date endDate);
}
