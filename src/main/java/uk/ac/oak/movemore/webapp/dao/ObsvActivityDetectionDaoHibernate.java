package uk.ac.oak.movemore.webapp.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.appfuse.dao.hibernate.GenericDaoHibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.oak.movemore.webapp.model.ObsvActivityDetection;
import uk.ac.oak.movemore.webapp.model.Sensors;

@Repository("obsvActivityDetectionDao")
@Transactional
public class ObsvActivityDetectionDaoHibernate extends
		GenericDaoHibernate<ObsvActivityDetection, Long> implements
		ObsvActivityDetectionDao {
	public Map<String, String> columnMap = new HashMap<String, String>();
	public ObsvActivityDetectionDaoHibernate() {
		super (ObsvActivityDetection.class);
		
		columnMap.put("obsv_id", "d.obsvId");
		columnMap.put("obsv_time", "d.obsvTime");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ObsvActivityDetection> findActivityObservationsBySensor (Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc, Integer offset, Integer limit) {
		List<ObsvActivityDetection>  obsvActivityDetectionList = new ArrayList<ObsvActivityDetection>();
		
		if (sensor == null || sensor.getSensorId() == null) {
			return obsvActivityDetectionList;
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
	
	private String constructfindObservationsBySensorQuery (Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc, Integer offset, Integer limit) {
		StringBuffer query=new StringBuffer("SELECT distinct d FROM ObsvActivityDetection d JOIN d.activitySensor o ");
		query.append(" where o.sensor =:sensor and d.latitude != 0 and d.longitude != 0 ");
		if (startDate != null) {
			query.append(" and d.obsvTime >= :startDate");
		}
		if (endDate != null) {
			query.append(" and d.obsvTime <= :endDate");
		}
		
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
	public List<Object[]> countSensorObsvSubCategories(Sensors sensor, Date startDate,
			Date endDate) {
		
		StringBuffer query = new StringBuffer("SELECT d.activityType, count(d.activityType) FROM ObsvActivityDetection d JOIN d.activitySensor o ");
		query.append(" where o.sensor =:sensor and d.latitude != 0 and d.longitude != 0 ");
		
		if (startDate != null) {
			query.append(" and d.obsvTime >= :startDate");
		}
		if (endDate != null) {
			query.append(" and d.obsvTime <= :endDate");
		}
		
		query.append(" GROUP BY d.activityType");
		
		Session sess = getSession();
        Query hsqlQuery = sess.createQuery(query.toString());
        hsqlQuery.setParameter("sensor", sensor);
        if (startDate!=null){
        	hsqlQuery.setParameter("startDate", startDate);
        }
        if (endDate!=null) {
        	hsqlQuery.setParameter("endDate", endDate);      
        }  
        
        return hsqlQuery.list();
	}
}
