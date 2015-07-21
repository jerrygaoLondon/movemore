package uk.ac.oak.movemore.webapp.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.appfuse.dao.GenericDao;

import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.model.ObsvOBDDetection;
import uk.ac.oak.movemore.webapp.model.Sensors;

public interface ObsvOBDDetectionDao extends GenericDao<ObsvOBDDetection, Long> {

	List<ObsvOBDDetection> findOBDObservationsBySensor(Sensors sensor);
	
	Set<ObsvOBDDetection> findDistinctOBDObservationsBySensor(Sensors sensor);

	List<ObsvOBDDetection> findOBDObservationsBySensor(Sensors sensor,
			Integer offset, Integer limit);

	List<ObsvOBDDetection> findOBDObservationsBySensor(Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc, Integer offset, Integer limit);
	
	List<Observations> queryUnclassifiedOBDObservations(Sensors sensor);
}
