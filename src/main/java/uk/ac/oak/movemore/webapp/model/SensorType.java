package uk.ac.oak.movemore.webapp.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.appfuse.model.BaseObject;

@Entity
@Table(name = "sensor_type")
public class SensorType extends BaseObject implements Serializable  {

	private static final long serialVersionUID = -1259534772008772196L;
	
	private Long id;
	private String name;
	
	@Id
	@Column(name="id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="name")
	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.getId() +","+this.getName();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SensorType) {
			final SensorType otherSensorType = (SensorType)obj;
			
			return new EqualsBuilder().append(id, otherSensorType.getId())
					.append(name, otherSensorType.getName())
					.isEquals();
		} else {
			return false;
		}		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(name).append(id).toHashCode();
	}

	
}
