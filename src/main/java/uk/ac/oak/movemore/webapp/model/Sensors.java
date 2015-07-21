package uk.ac.oak.movemore.webapp.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.appfuse.model.BaseObject;

/**
 * This class is used to represent available sensors available in devices (e.g.,Raspberry Pi
 * box) in the database.
 * 
 */
@Entity
@Table(name = "sensors")
@NamedQueries({ @NamedQuery(name = "findSensorsByName", query = "select s from Sensors s where s.name = :name "),
				@NamedQuery(name = "findSensorsByDevice", query = "select s from Sensors s where s.device = :device "),
				@NamedQuery(name = "findSensorsByPhysicalId", query = "select s from Sensors s where s.sensorPhysicalId = :sensorPhysicalId "),}
)
public class Sensors extends BaseObject implements Serializable {

	private static final long serialVersionUID = -3922306370225223248L;

	private Long sensorId;
	private String sensorPhysicalId;
	private Device device;
	
	private String name;
	private String description;

	private Integer sensorType;
	
	private Set<Observations> observations = new HashSet<Observations>(0);
	
	public Sensors() {
	}
	
	public Sensors(String sensorPhysicalId, String name) {
		this.sensorPhysicalId = sensorPhysicalId;
		this.name = name;
		setUpdated(new Date());
	}
	
	public Sensors(String sensorPhysicalId, String name, String desc) {
		this.sensorPhysicalId = sensorPhysicalId;
		this.name = name;
		this.description = desc;
		setUpdated(new Date());
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="sensor_id", unique = true, nullable = false)
	public Long getSensorId() {
		return sensorId;
	}

	public void setSensorId(Long id) {
		this.sensorId = id;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "device_id", nullable = false)
	public Device getDevice() {
		return this.device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}
	@Column(length = 100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		this.updated = new Date();
	}
	
	@Column(length = 500)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		this.updated = new Date();
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy="sensor", orphanRemoval= true)
	public Set<Observations> getObservations() {
		return this.observations;
	}

	public void setObservations(Set<Observations> observations) {
		this.observations = observations;
	}

	@Override
	public String toString() {
		return getName() + "," + getDevice().getName() + "," + getDescription();
	}
	
	@Column(name="sensor_type", columnDefinition = "int default 0", length = 2)
	public Integer getSensorType() {
		return sensorType;
	}

	public void setSensorType(Integer sensorType) {
		this.sensorType = sensorType;
		this.updated = new Date();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Sensors) {
			final Sensors otherSensor = (Sensors) obj;
			return new EqualsBuilder().append(name, otherSensor.name)
					.append(description, otherSensor.description)
					.append(sensorPhysicalId, otherSensor.sensorPhysicalId)
					.isEquals();
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(name).append(description)
				.append(sensorPhysicalId).toHashCode();
	}
	

	private Date created;
	private Date updated;
	private Integer version;

	@Temporal(TemporalType.TIMESTAMP)
	@PrePersist
	@Column(name = "created", nullable = false, updatable = false)
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = new Date();
	}

	@Temporal(TemporalType.TIMESTAMP)
	@PreUpdate
	@Column(name = "updated", nullable = true)
	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	@PrePersist
	protected void onCreate() {
		created = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		updated = new Date();
	}

	// avoid conflicting updates
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name = "sensor_physical_id", length=100)
	public String getSensorPhysicalId() {
		return sensorPhysicalId;
	}

	public void setSensorPhysicalId(String sensorPhysicalId) {
		this.sensorPhysicalId = sensorPhysicalId;
	}

	public Sensors clone() {
		Sensors clonedSensor = new Sensors();
		
		clonedSensor.setSensorId(this.sensorId);
		clonedSensor.setSensorPhysicalId(this.getSensorPhysicalId());
		clonedSensor.setName(this.name);
		clonedSensor.setDescription(this.description);
		clonedSensor.setDevice(this.getDevice());
		clonedSensor.setCreated(this.created);
		clonedSensor.setUpdated(this.updated);
		clonedSensor.setVersion(this.getVersion());
		
		return clonedSensor;
	}
}
