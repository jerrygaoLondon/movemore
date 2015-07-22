package uk.ac.oak.movemore.webapp.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.appfuse.model.BaseObject;

@Entity
@Table(name = "obsv_people_counter")
@NamedQueries({ @NamedQuery(name = "findPeopleCounterObservationsBySensor", query = "SELECT distinct d FROM ObsvPeopleCount d JOIN d.pCount o where o.sensor =:sensor order by d.created desc") })
public class ObsvPeopleCount extends BaseObject implements Serializable {

	private static final long serialVersionUID = -3283649738123170263L;

	private Long obsvId;
	private Observations pCount;
	private Integer number;
	// The observation time sent from sensor
	private Date obsvTime;

	// observation location
	private Double longitude;
	private Double latitude;

	public ObsvPeopleCount() {
	}

	public ObsvPeopleCount(Observations obsv, Integer number, Date obsvTime) {
		setObsvId(obsv.getObsvId());
		setpCount(obsv);
		setNumber(number);
		setObsvTime(obsvTime);
		this.updated = new Date();
	}

	/**
	 * set Java Persistence->JPA->Errors/Warnings->Queries and Generators and
	 * set level of “Generator is not defined in the persistence unit” to
	 * warning if it complains compilation error
	 * 
	 * @return
	 */
	@Id
	@Column(unique = true, nullable = false)
	public Long getObsvId() {
		return this.obsvId;
	}

	public void setObsvId(Long obsvId) {
		this.obsvId = obsvId;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@MapsId
	public Observations getpCount() {
		return pCount;
	}

	public void setpCount(Observations pCount) {
		this.pCount = pCount;
	}

	@Column(name = "number", updatable=false)
	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "obsv_time", updatable=false)
	public Date getObsvTime() {
		return obsvTime;
	}

	public void setObsvTime(Date obsvTime) {
		this.obsvTime = obsvTime;
	}

	@Column(name = "longitude", precision = 13, scale = 10, updatable=false)
	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Column(name = "latitude", precision = 13, scale = 10, updatable=false)
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	@Override
	public String toString() {
		return this.getObsvId() + "," + this.getNumber() + ","
				+ this.getObsvTime();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ObsvPeopleCount) {
			final ObsvPeopleCount otherObsv = (ObsvPeopleCount) obj;
			return new EqualsBuilder()
					.append(obsvTime, otherObsv.getObsvTime())
					.append(number, otherObsv.getNumber())
					.append(latitude, otherObsv.getLatitude())
					.append(longitude, otherObsv.getLongitude()).isEquals();
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(obsvTime).append(number)
				.append(latitude).append(longitude).toHashCode();
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
}
