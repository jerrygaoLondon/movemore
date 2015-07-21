package uk.ac.oak.movemore.webapp.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.appfuse.dao.GenericDao;

import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.model.Sensors;

public interface ObservationsDao extends GenericDao<Observations, Long> {

	List<Observations> findObservationsBySensor(Sensors sensor);
	Set<Observations> findDistinctObservationsBySensor(Sensors sensor);
	
	boolean removeAllObsvsBySensor(Sensors sensor);
	
	List<Observations> findObservationsBySensor(Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc, Integer offset, Integer limit);
	
	List<Observations> findObservationsBySensor(Sensors sensor,
			Integer offset, Integer limit);
	
	boolean removeObservationAttachment(Long id);
	
	boolean removeObservationAttachment(Observations object);
	
	List<Observations> queryRealtimeDataStream(Integer limit);
}
