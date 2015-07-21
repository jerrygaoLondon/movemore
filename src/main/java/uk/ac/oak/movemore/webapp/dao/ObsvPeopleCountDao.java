package uk.ac.oak.movemore.webapp.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.appfuse.dao.GenericDao;

import uk.ac.oak.movemore.webapp.model.ObsvPeopleCount;
import uk.ac.oak.movemore.webapp.model.Sensors;

public interface ObsvPeopleCountDao extends
		GenericDao<ObsvPeopleCount, Long> {

	List<ObsvPeopleCount> findPeopleCounterObservationsBySensor(Sensors sensor);
	
	Set<ObsvPeopleCount> findDistinctPeopleCounterObservationsBySensor(Sensors sensor);
	
	boolean removeAllPeopleCounterObservations (List<ObsvPeopleCount> obsvPcList);

	List<ObsvPeopleCount> findPeopleCounterObservationsBySensor(Sensors sensor,
			Integer offset, Integer limit);

	List<ObsvPeopleCount> findPeopleCounterObservationsBySensor(Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc, Integer offset, Integer limit);
}
