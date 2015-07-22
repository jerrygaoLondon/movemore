package uk.ac.oak.movemore.webapp.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.appfuse.model.BaseObject;

@Entity
@Table(name = "obsv_activity_norm")
public class ObsvActivityNorm extends BaseObject implements Serializable {

	private static final long serialVersionUID = 1146226128685846953L;

	private Long obsvId;
	private ObsvActivityDetection activityObsv;

	private Double finalLongitude;
	private Double finalLatitude;
	private Integer finalActivityType;
	private Double finalConfidence;

	private Date updated;

	public ObsvActivityNorm() {

	}

	public ObsvActivityNorm(Double finalLatitude, Double finalLongitude,
			Integer finalActivityType, Double finalConfidence) {
		this.finalLongitude = finalLongitude;
		this.finalLatitude = finalLatitude;
		this.finalActivityType = finalActivityType;
		this.finalConfidence = finalConfidence;
		this.updated = new Date();
	}

	@OneToOne(fetch = FetchType.EAGER)
	@MapsId
	public ObsvActivityDetection getActivityObsv() {
		return activityObsv;
	}

	public void setActivityObsv(ObsvActivityDetection activityObsv) {
		this.activityObsv = activityObsv;
	}

	@Column(name = "final_longitude", precision = 13, scale = 10, updatable = true)
	public Double getFinalLongitude() {
		return finalLongitude;
	}

	public void setFinalLongitude(Double finalLongitude) {
		this.finalLongitude = finalLongitude;
	}

	@Column(name = "final_latitude", precision = 13, scale = 10, updatable = true)
	public Double getFinalLatitude() {
		return finalLatitude;
	}

	public void setFinalLatitude(Double finalLatitude) {
		this.finalLatitude = finalLatitude;
		this.updated = new Date();
	}

	@Column(name = "final_activityType")
	public Integer getFinalActivityType() {
		return finalActivityType;
	}

	public void setFinalActivityType(Integer finalActivityType) {
		this.finalActivityType = finalActivityType;
		this.updated = new Date();
	}

	public Double getFinalConfidence() {
		return finalConfidence;
	}

	public void setFinalConfidence(Double finalConfidence) {
		this.finalConfidence = finalConfidence;
		this.updated = new Date();
	}

	@Id
	@Column(unique = true, nullable = false)
	public Long getObsvId() {
		return obsvId;
	}

	public void setObsvId(Long obsvId) {
		this.obsvId = obsvId;
	}

	@Column(name = "updated", nullable = true)
	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	@Override
	public String toString() {
		return "finalActivityType: [" + this.getFinalActivityType()
				+ "], final lat: [" + this.getFinalLatitude()
				+ "], final long: [" + this.finalLongitude
				+ "], final confidence: [" + this.finalConfidence + "]";
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

}
