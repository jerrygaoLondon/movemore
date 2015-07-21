package uk.ac.oak.movemore.webapp.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.appfuse.dao.hibernate.GenericDaoHibernate;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import uk.ac.oak.movemore.webapp.model.Sensors;

@Repository("sensorsDao")
public class SensorsDaoHibernate extends GenericDaoHibernate<Sensors, Long>
		implements SensorsDao {

	public SensorsDaoHibernate() {
		super(Sensors.class);
	}

	@Override
	public boolean isSensorPhysicalIdExist(String sensorPhysicalId) {
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("sensorPhysicalId", sensorPhysicalId);

		List<Sensors> sensorList = findByNamedQuery("findSensorsByPhysicalId",
				queryParams);

		if (sensorList.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public Sensors findSensorByPhysicalId(String sensorPhysicalId)
			throws SensorNotFoundException {
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("sensorPhysicalId", sensorPhysicalId);

		List<Sensors> sensorList = findByNamedQuery("findSensorsByPhysicalId",
				queryParams);

		if (sensorList == null || sensorList.size() == 0) {
			throw new SensorNotFoundException("The sensor physical id {"
					+ sensorPhysicalId + "} has not been found.");
		}

		return sensorList.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Sensors> getAllSensors() {
		Criteria criteria = getSession().createCriteria(Sensors.class);
		criteria.addOrder(Order.desc("created"));
		return criteria.list();
	}

	@Override
	public List<String[]> countSensorsByType() {
		String countSensorsSql = "select sensor_type,count(sensor_type) as count,sensor_type.name "
				+ "from sensors,sensor_type "
				+ "where sensor_type.id=sensors.sensor_type group by sensor_type";
		Session sess = getSession();
		Query sqlQuery = sess.createSQLQuery(countSensorsSql);
		@SuppressWarnings("unchecked")
		List<Object[]> results = sqlQuery.list();
		
		List<String[]> resultList = new ArrayList<String[]>(results.size());
		
		for(Object[] objArr : results) {
			String[] resultArr = new String[objArr.length];

			for (int i = 0; i < objArr.length; i++) {
				resultArr[i] = objArr[i] == null? "": objArr[i].toString();
			}
			resultList.add(resultArr);
		}
		
		return resultList;
	}
}
