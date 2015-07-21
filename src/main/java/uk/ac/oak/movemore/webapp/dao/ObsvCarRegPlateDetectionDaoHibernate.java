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

import uk.ac.oak.movemore.webapp.model.ObsvCarRegPlateDetection;
import uk.ac.oak.movemore.webapp.model.Sensors;

@Repository("obsvCarRegPlateDetectionDao")
@Transactional
public class ObsvCarRegPlateDetectionDaoHibernate extends
		GenericDaoHibernate<ObsvCarRegPlateDetection, Long> implements
		ObsvCarRegPlateDetectionDao {
	public Map<String, String> columnMap = new HashMap<String, String>();
	
	public ObsvCarRegPlateDetectionDaoHibernate() {
		super(ObsvCarRegPlateDetection.class);
		
		columnMap.put("obsv_id", "d.obsvId");
		columnMap.put("obsv_time", "d.obsvTime");
	}	

	public List<ObsvCarRegPlateDetection> findCarRegPlateObservationsBySensor(Sensors sensor) {
		List<ObsvCarRegPlateDetection> carRegPlateObsvs = new ArrayList<ObsvCarRegPlateDetection>();
				
		if (sensor == null || sensor.getSensorId() == null) {
			return carRegPlateObsvs;
		}
		
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("sensor", sensor);
		
		carRegPlateObsvs.addAll(findByNamedQuery("findCarRegPlateObsvsBySensor", queryParams)) ;
				
		return carRegPlateObsvs;
	}

	public Set<ObsvCarRegPlateDetection> findDistinctCarRegPlateObservationsBySensor(Sensors sensor) {
		Set<ObsvCarRegPlateDetection> carRegPlateObsvs = new HashSet<ObsvCarRegPlateDetection>();
				
		if (sensor == null || sensor.getSensorId() == null) {
			return carRegPlateObsvs;
		}
		
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("sensor", sensor);
		
		carRegPlateObsvs.addAll(findByNamedQuery("findCarRegPlateObsvsBySensor", queryParams)) ;
				
		return carRegPlateObsvs;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ObsvCarRegPlateDetection> findCarRegPlateObservationsBySensor (Sensors sensor,
				Date startDate, Date endDate, String orderByName, Boolean isAsc, Integer offset, Integer limit) {
		List<ObsvCarRegPlateDetection> carRegPlateObsvs = new ArrayList<ObsvCarRegPlateDetection>();
		
		if (sensor == null || sensor.getSensorId() == null) {
			return carRegPlateObsvs;
		}
		String queryStr = constructfindObservationsBySensorQuery(sensor, startDate, endDate, orderByName, isAsc, offset, limit);

		Session sess = getSession();
        Query hsqlQuery = sess.createQuery(queryStr);
        hsqlQuery.setParameter("sensor", sensor);
        if (startDate!=null){
        	hsqlQuery.setParameter("startDate", startDate);
        }
        if (endDate!=null) {
        	hsqlQuery.setParameter("endDate", endDate);      
        }  
        hsqlQuery.setMaxResults(limit == null ? 1000 : limit);
        hsqlQuery.setFirstResult(offset == null ? 0 : offset);
        
		return hsqlQuery.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ObsvCarRegPlateDetection> findCarRegPlateObservationsBySensor (Sensors sensor, Integer offset, Integer limit) {
		List<ObsvCarRegPlateDetection> carRegPlateObsvs = new ArrayList<ObsvCarRegPlateDetection>();
		
		if (sensor == null || sensor.getSensorId() == null) {
			return carRegPlateObsvs;
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
		StringBuffer query=new StringBuffer("SELECT distinct d FROM ObsvCarRegPlateDetection d JOIN d.carRegPlate o ");
		query.append(" where o.sensor =:sensor ");
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

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> aggregateDeviceObservationBySensor(Sensors sensor,
			Date startDate, Date endDate) {
		List<Object[]> obsvResCounts = new LinkedList<Object[]>();
		
		if (sensor == null || sensor.getSensorId() == null) {
			return obsvResCounts;
		}
		
		StringBuffer countsQuery=new StringBuffer("SELECT distinct d.obsvTime, count(distinct d.carRegPlateNum) FROM ObsvCarRegPlateDetection d JOIN d.carRegPlate o ");
		countsQuery.append(" where o.sensor =:sensor ");
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
}
