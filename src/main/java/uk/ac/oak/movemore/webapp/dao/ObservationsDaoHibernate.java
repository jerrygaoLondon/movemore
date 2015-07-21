package uk.ac.oak.movemore.webapp.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.appfuse.dao.hibernate.GenericDaoHibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.model.Sensors;
import uk.ac.oak.movemore.webapp.util.SensorMediaFileManager;

@Repository("observationsDao")
@Transactional
public class ObservationsDaoHibernate extends
		GenericDaoHibernate<Observations, Long> implements ObservationsDao {

	public Map<String, String> columnMap = new HashMap<String, String>();
	private SensorMediaFileManager sensorFileManager = new SensorMediaFileManager();
	
	public ObservationsDaoHibernate() {
		super(Observations.class);
		
		columnMap.put("obsv_id", "obsv.obsvId");
		columnMap.put("obsv_time", "obsv.obsvTime");
	}

	public boolean removeAllObsvsBySensor(Sensors sensor) {

		if (sensor == null || sensor.getSensorId() == null) {
			return true;
		}

		if (sensor.getObservations() == null
				|| sensor.getObservations().isEmpty()) {
			log.warn(String
					.format("attempting to remove all the sensor [%s] observations, but no observations attached",
							sensor.getSensorId()));
			return true;
		}
		List<Observations> obsvList = findObservationsBySensor(sensor);
		
		for (Observations obsv : obsvList) {

			remove(obsv);			
			removeObservationAttachment(obsv);
		}

		return true;
	}
	
	@Override
	public boolean removeObservationAttachment(Long id) {
		if (exists(id)) {
			return removeObservationAttachment(get(id));
		}
		return false;
	}

	@Override
	public boolean removeObservationAttachment(Observations object) {
		if (object!=null && StringUtils.isNotEmpty(object.getAttachment())) {
			return sensorFileManager.delete(object.getAttachment());
		}
		return false;
	}
	
	@Override
	public List<Observations> queryRealtimeDataStream(Integer limit) {		
		String hqlQuery = "select obvs from Observations obvs order by obvs.created desc ";
		
		Session sess = getSession();
        Query hsqlQuery = sess.createQuery(hqlQuery);
        
        if (limit != null) {
        	hsqlQuery.setMaxResults(limit);
		} else {
			hsqlQuery.setMaxResults(20);
		}        
        
		return hsqlQuery.list();
	}

	@Override
	public List<Observations> findObservationsBySensor(Sensors sensor) {
		List<Observations> obsvs = new ArrayList<Observations>();

		if (sensor == null || sensor.getSensorId() == null) {
			return obsvs;
		}

		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("sensor", sensor);

		obsvs.addAll(findByNamedQuery("findObservationsBySensor", queryParams));
		return obsvs;
	}

	@Override
	public Set<Observations> findDistinctObservationsBySensor(Sensors sensor) {
		Set<Observations> obsvs = new HashSet<Observations>();

		if (sensor == null || sensor.getSensorId() == null) {
			return obsvs;
		}

		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("sensor", sensor);

		obsvs.addAll(findByNamedQuery("findObservationsBySensor", queryParams));
		return obsvs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Observations> findObservationsBySensor(Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc, Integer offset, Integer limit) {
		List<Observations> obsvs = new ArrayList<Observations>();

		if (sensor == null || sensor.getSensorId() == null) {
			return obsvs;
		}
		
		String queryStr = constructfindObservationsBySensorQuery(sensor, startDate, endDate, orderByName, isAsc, offset, limit);

		Session sess = getSession();
        Query hsqlQuery = sess.createQuery(queryStr);
        hsqlQuery.setParameter("sensor", sensor);
        if (startDate != null) {
        	hsqlQuery.setParameter("startDate", startDate);
        }
        if (endDate != null) {
        	hsqlQuery.setParameter("endDate", endDate);     
        }   
        hsqlQuery.setMaxResults(limit == null ? 1000 : limit);
        hsqlQuery.setFirstResult(offset == null ? 0 : offset);
        
		return hsqlQuery.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Observations> findObservationsBySensor(Sensors sensor,
			Integer offset, Integer limit) {
		List<Observations> obsvs = new ArrayList<Observations>();

		if (sensor == null || sensor.getSensorId() == null) {
			return obsvs;
		}
		String queryStr = constructfindObservationsBySensorQuery(sensor, null, null, null, null, offset, limit);
		
		Session sess = getSession();
        Query hsqlQuery = sess.createQuery(queryStr);
        hsqlQuery.setParameter("sensor", sensor);
        hsqlQuery.setMaxResults(limit == null ? 1000 : limit);
        hsqlQuery.setFirstResult(offset == null ? 0 : offset);
        
		return hsqlQuery.list();
	}
	
	private String constructfindObservationsBySensorQuery (Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc, Integer offset, Integer limit) {
		StringBuffer query=new StringBuffer("select obsv from Observations obsv");
		query.append(" where obsv.sensor = :sensor ");
		if (startDate != null) {
			query.append(" and obsv.obsvTime >= :startDate");
		}
		if (endDate != null) {
			query.append(" and obsv.obsvTime <= :endDate");
		}
		
		//query.append(" order by obsv.obsvTime desc ");
		if (StringUtils.isNotEmpty(orderByName) && columnMap.containsKey(orderByName.toLowerCase())) {
			query.append(" order by "+ columnMap.get("orderByName"));
		} else {
			query.append(" order by obsv.obsvTime ");
		}
		
		if (isAsc != null && isAsc) {
			query.append(" asc ");
		} else {
			query.append(" desc ");
		}
		
		return query.toString();
	}
}
