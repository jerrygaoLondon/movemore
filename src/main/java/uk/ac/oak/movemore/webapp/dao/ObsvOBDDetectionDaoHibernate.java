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
import uk.ac.oak.movemore.webapp.model.ObsvOBDDetection;
import uk.ac.oak.movemore.webapp.model.Sensors;

@Repository("obsvOBDDetectionDao")
@Transactional
public class ObsvOBDDetectionDaoHibernate extends
		GenericDaoHibernate<ObsvOBDDetection, Long> implements
		ObsvOBDDetectionDao {

	public Map<String, String> columnMap = new HashMap<String, String>();

	public ObsvOBDDetectionDaoHibernate() {
		super(ObsvOBDDetection.class);

		columnMap.put("obsv_id", "d.obsvId");
		columnMap.put("obsv_time", "d.obsvTime");
	}

	@Override
	public List<ObsvOBDDetection> findOBDObservationsBySensor(Sensors sensor) {
		List<ObsvOBDDetection> obdObsvs = new ArrayList<ObsvOBDDetection>();

		if (sensor == null || sensor.getSensorId() == null) {
			return obdObsvs;
		}

		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("sensor", sensor);

		obdObsvs.addAll(findByNamedQuery("findOBDObservationsBySensor",
				queryParams));

		return obdObsvs;
	}

	@Override
	public Set<ObsvOBDDetection> findDistinctOBDObservationsBySensor(
			Sensors sensor) {
		Set<ObsvOBDDetection> obdObsvs = new HashSet<ObsvOBDDetection>();

		if (sensor == null || sensor.getSensorId() == null) {
			return obdObsvs;
		}

		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("sensor", sensor);

		obdObsvs.addAll(findByNamedQuery("findOBDObservationsBySensor",
				queryParams));

		return obdObsvs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ObsvOBDDetection> findOBDObservationsBySensor(Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc,
			Integer offset, Integer limit) {
		List<ObsvOBDDetection> obdObsvs = new ArrayList<ObsvOBDDetection>();

		if (sensor == null || sensor.getSensorId() == null) {
			return obdObsvs;
		}
		String queryStr = constructfindObservationsBySensorQuery(sensor,
				startDate, endDate, orderByName, isAsc, offset, limit);

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
	public List<ObsvOBDDetection> findOBDObservationsBySensor(Sensors sensor,
			Integer offset, Integer limit) {
		List<ObsvOBDDetection> obdObsvs = new ArrayList<ObsvOBDDetection>();

		if (sensor == null || sensor.getSensorId() == null) {
			return obdObsvs;
		}

		String queryStr = constructfindObservationsBySensorQuery(sensor, null,
				null, null, null, offset, limit);

		Session sess = getSession();
		Query hsqlQuery = sess.createQuery(queryStr);
		hsqlQuery.setParameter("sensor", sensor);
		hsqlQuery.setMaxResults(limit == null ? 1000 : limit);
		hsqlQuery.setFirstResult(offset == null ? 0 : offset);

		return hsqlQuery.list();
	}

	private String constructfindObservationsBySensorQuery(Sensors sensor,
			Date startDate, Date endDate, String orderByName, Boolean isAsc,
			Integer offset, Integer limit) {
		StringBuffer query = new StringBuffer(
				"SELECT distinct d FROM ObsvOBDDetection d JOIN d.obdSensor o ");
		query.append(" where o.sensor =:sensor and d.latitude != 0 and d.longitude != 0 ");
		if (startDate != null) {
			query.append(" and d.obsvTime >= :startDate");
		}
		if (endDate != null) {
			query.append(" and d.obsvTime <= :endDate");
		}

		// query.append(" order by d.obsvTime desc ");

		if (StringUtils.isNotEmpty(orderByName)
				&& columnMap.containsKey(orderByName.toLowerCase())) {
			query.append(" order by " + columnMap.get("orderByName"));
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

	@Override
	public List<Observations> queryUnclassifiedOBDObservations(Sensors sensor) {
		StringBuffer query = new StringBuffer(
				"SELECT distinct obsv FROM Observations obsv WHERE obsv.sensor =:sensor ");
		query.append(" and obsv.obsvId NOT IN (SELECT distinct obd.obsvId FROM ObsvOBDDetection obd ")
			.append("   JOIN obd.obdSensor obdS WHERE obdS.sensor =:sensor) ");
		
		
		Session sess = getSession();
		Query hsqlQuery = sess.createQuery(query.toString());
		hsqlQuery.setParameter("sensor", sensor);
		
		return hsqlQuery.list();
	}
}
