package uk.ac.oak.movemore.webapp.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.CascadeType;
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
@Table(name = "observations")
@NamedQueries({ @NamedQuery(name = "findObservationsBySensor", query = "select obvs from Observations obvs where obvs.sensor = :sensor order by obvs.created desc")})
public class Observations extends BaseObject implements Serializable, Cloneable {

	private static final long serialVersionUID = -7309695092529772481L;
	
	private Long obsvId;
	//The observation time sent from sensor
	private Date obsvTime;
	//The system time received from sensor
	private Date recordTime;
	//sensor observed value
	private String value;
	
	// observation location
	private Float longitude;
	private Float latitude;
	
	private String attachment;
	
	private Sensors sensor;
	
	private ObsvPeopleCount obsvPeopleCount;
	private ObsvDeviceDetection obsvDeviceDetect;
	private ObsvCarRegPlateDetection obsvCarRegPlateDetect;
	private ObsvOBDDetection obsvOBDDetect;
	private ObsvVoiceDecibelDetection obsvVoiceDetect;
	private ObsvActivityDetection obsvActivityDetect;
	
	public Observations () {
		if(this.created == null) {
			this.created = new Date(); 
		}
	}
	
	public Observations (Sensors sensor) {
		this.sensor = sensor;
		if(this.created == null) {
			this.created = new Date(); 
		}
		this.updated = new Date();
	}
	
	public Observations (Sensors sensor, String value, Date obsvTime) {
		this.sensor = sensor;
		this.value = value;
		this.obsvTime = obsvTime;
		this.recordTime = new Timestamp(new Date().getTime());
		if(this.created == null) {
			this.created = new Date(); 
		}
		this.updated = new Date();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="obsv_id", unique = true, nullable = false)
	public Long getObsvId() {
		return obsvId;
	}

	public void setObsvId(Long obsvId) {
		this.obsvId = obsvId;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "obsv_time", updatable=false)
	public Date getObsvTime() {
		return obsvTime;
	}

	public void setObsvTime(Date obsvTime) {
		this.obsvTime = obsvTime;
	}
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy="pCount", cascade=CascadeType.ALL)  
	public ObsvPeopleCount getObsvPeopleCount() {
		return obsvPeopleCount;
	}

	public void setObsvPeopleCount(ObsvPeopleCount obsvPeopleCount) {
		this.obsvPeopleCount = obsvPeopleCount;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy="devDetect", cascade=CascadeType.ALL)  
	public ObsvDeviceDetection getObsvDeviceDetect() {
		return obsvDeviceDetect;
	}

	public void setObsvDeviceDetect(ObsvDeviceDetection obsvDeviceDetect) {
		this.obsvDeviceDetect = obsvDeviceDetect;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy="carRegPlate", cascade=CascadeType.ALL)  
	public ObsvCarRegPlateDetection getObsvCarRegPlateDetect() {
		return obsvCarRegPlateDetect;
	}

	public void setObsvCarRegPlateDetect(
			ObsvCarRegPlateDetection obsvCarRegPlateDetect) {
		this.obsvCarRegPlateDetect = obsvCarRegPlateDetect;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy="obdSensor", cascade=CascadeType.ALL)  
	public ObsvOBDDetection getObsvOBDDetect() {
		return obsvOBDDetect;
	}

	public void setObsvOBDDetect(ObsvOBDDetection obsvOBDDetect) {
		this.obsvOBDDetect = obsvOBDDetect;
	}
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy="voiceSensor", cascade=CascadeType.ALL)  
	public ObsvVoiceDecibelDetection getObsvVoiceDetect() {
		return obsvVoiceDetect;
	}

	public void setObsvVoiceDetect(ObsvVoiceDecibelDetection obsvVoiceDetect) {
		this.obsvVoiceDetect = obsvVoiceDetect;
	}
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy="activitySensor", cascade=CascadeType.ALL)  
	public ObsvActivityDetection getObsvActivityDetect() {
		return obsvActivityDetect;
	}

	public void setObsvActivityDetect(ObsvActivityDetection obsvActivityDetect) {
		this.obsvActivityDetect = obsvActivityDetect;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "record_time", updatable=false)
	public Date getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(Date recordTime) {
		this.recordTime = recordTime;
	}
	
	@Column(name="value", columnDefinition="TEXT", updatable=false)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Column(name = "longitude", precision = 13, scale = 10, updatable=false)
	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	@Column(name = "latitude", precision = 13, scale = 10, updatable=false)
	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sensor_id", updatable=false)
	public Sensors getSensor() {
		return this.sensor;
	}

	public void setSensor(Sensors sensor) {
		this.sensor = sensor;
	}
	
	@Column (length=500)
	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	@Override
	public String toString() {		
		return this.getObsvId()+","+this.getObsvTime();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Observations) {
			final Observations otherObsv = (Observations) obj;
			EqualsBuilder eb = new EqualsBuilder().append(value, otherObsv.value)
					.append(obsvTime, otherObsv.getObsvTime())
					.append(latitude, otherObsv.latitude)
					.append(longitude, otherObsv.longitude);
			if (getSensor()!= null && getSensor().getSensorId()!=null && otherObsv.getSensor()!=null && otherObsv.getSensor().getSensorId() != null) {
				eb.append(getSensor().getSensorId(), otherObsv.getSensor().getSensorId());
			}
			return eb.isEquals();
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hb = new HashCodeBuilder().append(value).append(obsvTime).append(latitude).append(longitude);
		if (getSensor()!= null && getSensor().getSensorId()!=null) {
			hb.append(getSensor().getSensorId());
		}
		return hb.toHashCode();
	}

	private Date created;
	private Date updated;
	private Integer version;
	
	@Temporal(TemporalType.TIMESTAMP)
	@PrePersist
    @Column(name = "created", nullable = false, updatable = false )
	public Date getCreated() {
		return created;
	}
	
	public void setCreated(Date created) {
		this.created = created;
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
	//the JPA callbacks won't work if you're using the Session API.
	@PrePersist
	protected void onCreate() {
	  created = new Date();
	}
	
	@PrePersist
	@PreUpdate
	protected void onUpdate() {
	  updated = new Date();
	} 
	
	//avoid conflicting updates
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
}
