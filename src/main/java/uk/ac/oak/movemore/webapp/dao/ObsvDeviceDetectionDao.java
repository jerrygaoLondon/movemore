package uk.ac.oak.movemore.webapp.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.appfuse.dao.GenericDao;

import uk.ac.oak.movemore.webapp.model.ObsvDeviceDetection;
import uk.ac.oak.movemore.webapp.model.Sensors;

public interface ObsvDeviceDetectionDao extends
		GenericDao<ObsvDeviceDetection, Long> {

	List<ObsvDeviceDetection> findDeviceObservationsBySensor(Sensors sensor);

	Set<ObsvDeviceDetection> findDistinctDeviceObservationsBySensor(
			Sensors sensor);

	List<ObsvDeviceDetection> findDeviceObservationsBySensor(Sensors sensor,
			Integer offset, Integer limit);

	List<ObsvDeviceDetection> findDeviceObservationsBySensor(Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc,
			Integer offset, Integer limit);

	/**
	 * Aggregate observation results (i.e.,detected macAddress) by observation
	 * time within a time range
	 * 
	 * @param sensor
	 * @param startDate
	 *            can be null
	 * @param endDate
	 *            can be null
	 * @return A list of result rows with 2 columns (the first is obsv time and
	 *         the second is counts) or a empty list with 0 results if no
	 *         results found
	 */
	List<Object[]> aggregateDeviceObservationBySensor(Sensors sensor,
			Date startDate, Date endDate);
}
