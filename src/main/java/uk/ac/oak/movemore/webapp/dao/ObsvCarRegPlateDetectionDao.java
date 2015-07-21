package uk.ac.oak.movemore.webapp.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.appfuse.dao.GenericDao;

import uk.ac.oak.movemore.webapp.model.ObsvCarRegPlateDetection;
import uk.ac.oak.movemore.webapp.model.Sensors;

public interface ObsvCarRegPlateDetectionDao extends
		GenericDao<ObsvCarRegPlateDetection, Long> {

	List<ObsvCarRegPlateDetection> findCarRegPlateObservationsBySensor(Sensors sensor);
	
	Set<ObsvCarRegPlateDetection> findDistinctCarRegPlateObservationsBySensor(Sensors sensor);

	List<ObsvCarRegPlateDetection> findCarRegPlateObservationsBySensor(
			Sensors sensor, Integer offset, Integer limit);

	List<ObsvCarRegPlateDetection> findCarRegPlateObservationsBySensor(
			Sensors sensor, Date startDate, Date endDate, String orderByName, Boolean isAsc, Integer offset,
			Integer limit);

	/**
	 * Aggregate observation results (i.e.,detected encrypted car registration plate no) by observation
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
