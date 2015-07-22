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
@Table(name = "obsv_voice_sensor")
@NamedQueries({ @NamedQuery(name = "findVoiceSensorObservationsBySensor", query = "SELECT distinct d FROM ObsvVoiceDecibelDetection d JOIN d.voiceSensor o where o.sensor =:sensor order by d.created desc") })
public class ObsvVoiceDecibelDetection extends BaseObject implements
		Serializable {

	private static final long serialVersionUID = 4391136827533901992L;

	private Long obsvId;

	private Observations voiceSensor;

	private Double decibel;

	// The observation time sent from sensor
	private Date obsvTime;

	// observation location
	private Double longitude;
	private Double latitude;
	
	private String attachment;
	
	public ObsvVoiceDecibelDetection() {
		
	}
	
	public ObsvVoiceDecibelDetection(Double decibel, Double longitude, Double latitude, Date obsvTime) {
		setDecibel(decibel);
		setLongitude(longitude);
		setLatitude(latitude);
		setObsvTime(obsvTime);
		setUpdated(new Date());
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

	@OneToOne(cascade = CascadeType.ALL)
	@MapsId
	public Observations getVoiceSensor() {
		return voiceSensor;
	}

	public void setVoiceSensor(Observations voiceSensor) {
		this.voiceSensor = voiceSensor;
	}

	@Column
	public Double getDecibel() {
		return decibel;
	}

	public void setDecibel(Double decibel) {
		this.decibel = decibel;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "obsv_time", updatable = false)
	public Date getObsvTime() {
		return obsvTime;
	}

	public void setObsvTime(Date obsvTime) {
		this.obsvTime = obsvTime;
	}
	
	@Column(name = "longitude", precision = 13, scale = 10, updatable = false)
	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Column(name = "latitude", precision = 13, scale = 10, updatable = false)
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setObsvId(Long obsvId) {
		this.obsvId = obsvId;
	}
	
	@Column (length = 500)
	public String getAttachment() {
		return attachment;
	}
	
	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	@Override
	public String toString() {
		return this.getObsvId() + "," + this.decibel + "dB" + ","
				+ this.getObsvTime();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ObsvVoiceDecibelDetection) {
			final ObsvVoiceDecibelDetection otherObsv = (ObsvVoiceDecibelDetection) obj;
			return new EqualsBuilder()
					.append(obsvTime, otherObsv.getObsvTime())
					.append(decibel, otherObsv.getDecibel())
					.append(latitude, otherObsv.getLatitude())
					.append(longitude, otherObsv.getLongitude()).isEquals();
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(obsvTime).append(decibel)
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
