package uk.ac.oak.movemore.webapp.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "obsv_activity_detection")
@NamedQueries({ @NamedQuery(name = "findActivityObsvsBySensor", query = "SELECT distinct d FROM ObsvActivityDetection d JOIN d.activitySensor o where o.sensor =:sensor order by d.created desc") })
public class ObsvActivityDetection extends BaseObject implements Serializable {

	private static final long serialVersionUID = -8876675334489052141L;
	private Long obsvId;
	private Observations activitySensor;
	private Integer activityType;
	private Double confidence;
	private Integer floorClimbed;

	private Double speed;
	// The observation time sent from sensor
	private Date obsvTime;

	// observation location
	private Double longitude;
	private Double latitude;
	private Double locAccuracy;
	
	private Integer stepsDoneToday;
	private Long duration;
	
	private ObsvActivityNorm obsvActivityNorm;

	public ObsvActivityDetection() {
	}

	public ObsvActivityDetection(Double longitude, Double latitude, Date obsvTime) {
		setLongitude(longitude);
		setLatitude(latitude);
		setObsvTime(obsvTime);
		setUpdated(new Date());
	}

	@Id
	@Column(unique = true, nullable = false)
	public Long getObsvId() {
		return obsvId;
	}

	public void setObsvId(Long obsvId) {
		this.obsvId = obsvId;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	public Observations getActivitySensor() {
		return activitySensor;
	}

	public void setActivitySensor(Observations activitySensor) {
		this.activitySensor = activitySensor;
	}
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy="activityObsv", cascade=CascadeType.ALL)  
	public ObsvActivityNorm getObsvActivityNorm() {
		return obsvActivityNorm;
	}

	public void setObsvActivityNorm(ObsvActivityNorm obsvActivityNorm) {
		this.obsvActivityNorm = obsvActivityNorm;
	}

	@Column
	public Integer getActivityType() {
		return activityType;
	}
	/**
	 * Activity type corresponds to Google DetectedActivity API for Android
	 * @see https://developers.google.com/android/reference/com/google/android/gms/location/DetectedActivity.html
	 * @param activityType
	 */
	public void setActivityType(Integer activityType) {
		this.activityType = activityType;
	}

	@Column
	public Double getConfidence() {
		return confidence;
	}

	@Column
	public Double getSpeed() {
		return speed;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}
	
	@Column
	public Integer getFloorClimbed() {
		return floorClimbed;
	}

	public void setFloorClimbed(Integer floorClimbed) {
		this.floorClimbed = floorClimbed;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "obsv_time", updatable = true)
	public Date getObsvTime() {
		return obsvTime;
	}

	public void setObsvTime(Date obsvTime) {
		this.obsvTime = obsvTime;
	}

	@Column(name = "longitude", updatable = false)
	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Column(name = "latitude", updatable = false)
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	@Column(name = "locAccuracy")
	public Double getLocAccuracy() {
		return locAccuracy;
	}

	public void setLocAccuracy(Double locAccuracy) {
		this.locAccuracy = locAccuracy;
	}
	
	@Column
	public Integer getStepsDoneToday() {
		return stepsDoneToday;
	}

	public void setStepsDoneToday(Integer stepsDoneToday) {
		this.stepsDoneToday = stepsDoneToday;
	}
	
	@Column
	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		return this.activityType + "," + this.confidence + "," + this.latitude
				+ "," + this.longitude + "," + this.obsvTime;
//				+ " normalised activity type [" + this.finalActivityType
//				+ "], normalised latitude [" + this.finalLatitude + "]"
//				+ ", normalised longitude [" + this.finalLongitude + "]"
//				+ ", final confidence ["+ this.finalConfidence+"]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ObsvActivityDetection) {
			final ObsvActivityDetection otherObsv = (ObsvActivityDetection) obj;
			return new EqualsBuilder()
					.append(activityType, otherObsv.getActivityType())
					.append(confidence, otherObsv.getActivityType())
					.append(obsvTime, otherObsv.getObsvTime())
					.append(latitude, otherObsv.getLatitude())
					.append(longitude, otherObsv.getLongitude()).isEquals();
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(obsvTime).append(activityType)
				.append(confidence).append(latitude).append(longitude)
				.toHashCode();
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
