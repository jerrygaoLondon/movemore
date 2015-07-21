package uk.ac.oak.movemore.webapp.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.appfuse.dao.GenericDao;

import uk.ac.oak.movemore.webapp.model.ObsvVoiceDecibelDetection;
import uk.ac.oak.movemore.webapp.model.Sensors;

public interface ObsvVoiceDecibelDetectionDao extends GenericDao<ObsvVoiceDecibelDetection, Long> {

	List<ObsvVoiceDecibelDetection> findVoiceDecibelBySensor(Sensors sensor);
	
	Set<ObsvVoiceDecibelDetection> findDistinctVoiceDecibelBySensor(Sensors sensor);

	List<ObsvVoiceDecibelDetection> findPeopleCounterObservationsBySensor(
			Sensors sensor, Integer offset, Integer limit);

	List<ObsvVoiceDecibelDetection> findPeopleCounterObservationsBySensor(
			Sensors sensor, Date startDate, Date endDate, String orderByName, Boolean isAsc, Integer offset,
			Integer limit);
}
