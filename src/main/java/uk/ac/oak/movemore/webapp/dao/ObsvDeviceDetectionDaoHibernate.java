package uk.ac.oak.movemore.webapp.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.appfuse.dao.hibernate.GenericDaoHibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.oak.movemore.webapp.model.ObsvDeviceDetection;
import uk.ac.oak.movemore.webapp.model.Sensors;

@Repository("obsvDeviceDetectionDao")
@Transactional
public class ObsvDeviceDetectionDaoHibernate extends
		GenericDaoHibernate<ObsvDeviceDetection, Long> implements
		ObsvDeviceDetectionDao {
	
	public Map<String, String> columnMap = new HashMap<String, String>();
	
	public ObsvDeviceDetectionDaoHibernate () {
		super(ObsvDeviceDetection.class);
		
		columnMap.put("obsv_id", "d.obsvId");
		columnMap.put("obsv_time", "d.obsvTime");
	}

	@Override
	public List<ObsvDeviceDetection> findDeviceObservationsBySensor(
			Sensors sensor) {
		List<ObsvDeviceDetection> deviceDetectionObsvs = new ArrayList<ObsvDeviceDetection>();
		
		if (sensor == null || sensor.getSensorId() == null) {
			return deviceDetectionObsvs;
		}
		
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("sensor", sensor);
		
		deviceDetectionObsvs.addAll(findByNamedQuery("findDeviceObservationsBySensor", queryParams)) ;
		
		return deviceDetectionObsvs;
	}
	
	public Set<ObsvDeviceDetection> findDistinctDeviceObservationsBySensor (Sensors sensor) {
		Set<ObsvDeviceDetection> obsvDeviceObsvs = new HashSet<ObsvDeviceDetection>();
		
		if (sensor == null || sensor.getSensorId() == null) {
			return obsvDeviceObsvs;
		}
		
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("sensor", sensor);
		
		obsvDeviceObsvs.addAll(findByNamedQuery("findDeviceObservationsBySensor", queryParams)) ;
		
		return obsvDeviceObsvs;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ObsvDeviceDetection> findDeviceObservationsBySensor (Sensors sensor,
				Date startDate, Date endDate, String orderByName, Boolean isAsc, Integer offset, Integer limit) {
		List<ObsvDeviceDetection> obsvDeviceObsvs = new ArrayList<ObsvDeviceDetection>();
		
		if (sensor == null || sensor.getSensorId() == null) {
			return obsvDeviceObsvs;
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
	public List<Object[]> aggregateDeviceObservationBySensor(Sensors sensor,
			Date startDate, Date endDate) {
		List<Object[]> obsvResCounts = new LinkedList<Object[]>();
		
		if (sensor == null || sensor.getSensorId() == null) {
			return obsvResCounts;
		}
		
		StringBuffer countsQuery=new StringBuffer("SELECT distinct d.obsvTime, count(distinct d.macAddress) FROM ObsvDeviceDetection d JOIN d.devDetect o ");
		countsQuery.append(" where o.sensor =:sensor and d.latitude != 0 and d.longitude != 0 ");
		if (startDate != null) {
			countsQuery.append(" and d.obsvTime >= :startDate");
		}
		if (endDate != null) {
			countsQuery.append(" and d.obsvTime <= :endDate");
		}
		countsQuery.append(" group by d.obsvTime ").append(" order by d.obsvId asc");
		
		Session sess = getSession();
		
        Query hsqlQuery = sess.createQuery(countsQuery.toString());
        hsqlQuery.setParameter("sensor", sensor);
        if (startDate!=null){
        	hsqlQuery.setParameter("startDate", startDate);
        }
        if (endDate!=null) {
        	hsqlQuery.setParameter("endDate", endDate);      
        }
        
		return (List<Object[]>)hsqlQuery.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ObsvDeviceDetection> findDeviceObservationsBySensor (Sensors sensor, Integer offset, Integer limit) {
		List<ObsvDeviceDetection> obsvDeviceObsvs = new ArrayList<ObsvDeviceDetection>();
		
		if (sensor == null || sensor.getSensorId() == null) {
			return obsvDeviceObsvs;
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
		StringBuffer query=new StringBuffer("SELECT distinct d FROM ObsvDeviceDetection d JOIN d.devDetect o ");
		query.append(" where o.sensor =:sensor and d.latitude != 0 and d.longitude != 0 ");
		if (startDate != null) {
			query.append(" and d.obsvTime >= :startDate");
		}
		if (endDate != null) {
			query.append(" and d.obsvTime <= :endDate");
		}
		
		//query.append(" order by d.obsvTime desc ");
		
		if (StringUtils.isNotEmpty(orderByName) && columnMap.containsKey(orderByName.toLowerCase())) {
			query.append(" order by "+ columnMap.get("orderByName"));
		} else {
			query.append(" order by d.obsvTime ");
		}
		
		if (isAsc != null && isAsc) {
			query.append(" asc ");
		} else {
			query.append(" desc ");
		}
		
		return query.toString();
	}
}
