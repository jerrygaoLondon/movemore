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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.oak.movemore.webapp.model.ObsvPeopleCount;
import uk.ac.oak.movemore.webapp.model.Sensors;

@Repository("obsvPeopleCountDao")
@Transactional
public class ObsvPeopleCountDaoHibernate extends
		GenericDaoHibernate<ObsvPeopleCount, Long> implements
		ObsvPeopleCountDao {

	public Map<String, String> columnMap = new HashMap<String, String>();
	
	@Autowired
	private ObservationsDao observationsDao;
	
	public ObsvPeopleCountDaoHibernate() {
		super(ObsvPeopleCount.class);
		
		columnMap.put("obsv_id", "d.obsvId");
		columnMap.put("obsv_time", "d.obsvTime");
	}

	@Override
	public List<ObsvPeopleCount> findPeopleCounterObservationsBySensor(
			Sensors sensor) {
		List<ObsvPeopleCount> peopleCountObsvs = new ArrayList<ObsvPeopleCount>();
		
		if (sensor == null || sensor.getSensorId() == null) {
			return peopleCountObsvs;
		}
		
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("sensor", sensor);
		
		peopleCountObsvs.addAll(findByNamedQuery("findPeopleCounterObservationsBySensor", queryParams)) ;
		
		return peopleCountObsvs;
	}
	
	public Set<ObsvPeopleCount> findDistinctPeopleCounterObservationsBySensor(Sensors sensor) {
		Set<ObsvPeopleCount> peopleCountObsvs = new HashSet<ObsvPeopleCount>();
		
		if (sensor == null || sensor.getSensorId() == null) {
			return peopleCountObsvs;
		}
		
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("sensor", sensor);
		
		peopleCountObsvs.addAll(findByNamedQuery("findPeopleCounterObservationsBySensor", queryParams)) ;
		
		return peopleCountObsvs;
	}
	
	public boolean removeAllPeopleCounterObservations (List<ObsvPeopleCount> obsvPcList) {
		if (obsvPcList == null || obsvPcList.isEmpty()) {
			return true;
		}
		
		for (ObsvPeopleCount pc : obsvPcList) {
			remove(pc);
		}
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ObsvPeopleCount> findPeopleCounterObservationsBySensor (Sensors sensor,
				Date startDate, Date endDate,String orderByName, Boolean isAsc, Integer offset, Integer limit) {
		List<ObsvPeopleCount> peopleCountObsvs = new ArrayList<ObsvPeopleCount>();
		
		if (sensor == null || sensor.getSensorId() == null) {
			return peopleCountObsvs;
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
	public List<ObsvPeopleCount> findPeopleCounterObservationsBySensor (Sensors sensor, Integer offset, Integer limit) {
		List<ObsvPeopleCount> peopleCountObsvs = new ArrayList<ObsvPeopleCount>();
		
		if (sensor == null || sensor.getSensorId() == null) {
			return peopleCountObsvs;
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
		StringBuffer query=new StringBuffer("SELECT distinct d FROM ObsvPeopleCount d JOIN d.pCount o ");
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
