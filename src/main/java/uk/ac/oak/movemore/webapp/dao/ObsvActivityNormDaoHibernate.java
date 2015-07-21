package uk.ac.oak.movemore.webapp.dao;

import org.appfuse.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.oak.movemore.webapp.model.ObsvActivityNorm;

@Repository("obsvActivityNormDao")
@Transactional
public class ObsvActivityNormDaoHibernate extends
		GenericDaoHibernate<ObsvActivityNorm, Long> implements
		ObsvActivityNormDao {

	public ObsvActivityNormDaoHibernate() {
		super(ObsvActivityNorm.class);
	}

}
