package uk.ac.oak.movemore.webapp.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import uk.ac.oak.movemore.webapp.model.ObsvActivityDetection;

public class ObsvActivityDetectionVO implements Serializable {

	private static final long serialVersionUID = 6456191798286373113L;

	private Long obsvId;
	private Long sensorId;
	private String activityType;
	private Double confidence;
	// observation location
	private Float longitude;
	private Float latitude;
	private Double speed;
	private Double locAccuracy;
	// The observation time sent from sensor
	private Date obsvTime;
	// private Date created;
	private Date updated;
	private Boolean normalised = false;
	private Integer version;

	public Long getObsvId() {
		return obsvId;
	}

	public void setObsvId(Long obsvId) {
		this.obsvId = obsvId;
	}

	public Long getSensorId() {
		return sensorId;
	}

	public void setSensorId(Long sensorId) {
		this.sensorId = sensorId;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public Double getConfidence() {
		return confidence;
	}

	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Date getObsvTime() {
		return obsvTime;
	}

	public void setObsvTime(Date obsvTime) {
		this.obsvTime = obsvTime;
	}

	public Double getLocAccuracy() {
		return locAccuracy;
	}

	public void setLocAccuracy(Double locAccuracy) {
		this.locAccuracy = locAccuracy;
	}

	public Double getSpeed() {
		return speed;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public Boolean getNormalised() {
		return normalised;
	}

	public void setNormalised(Boolean normalised) {
		this.normalised = normalised;
	}

	public void clone(ObsvActivityDetection obsvActivityDetect) {
		setObsvId(obsvActivityDetect.getObsvId());

		if (obsvActivityDetect.getObsvActivityNorm() != null
				&& StringUtils.isNotEmpty(obsvActivityDetect
						.getObsvActivityNorm().getFinalActivityType())) {
			setActivityType(obsvActivityDetect.getObsvActivityNorm()
					.getFinalActivityType());
			setNormalised(true);
		} else {
			setActivityType(obsvActivityDetect.getActivityType());
		}

		if (obsvActivityDetect.getObsvActivityNorm() != null
				&& obsvActivityDetect.getObsvActivityNorm()
						.getFinalConfidence() != null
				&& obsvActivityDetect.getObsvActivityNorm()
						.getFinalConfidence().floatValue() != 0.0f) {
			setConfidence(obsvActivityDetect.getObsvActivityNorm()
					.getFinalConfidence());
			setNormalised(true);
		} else {
			setConfidence(obsvActivityDetect.getConfidence());
		}

		setSpeed(obsvActivityDetect.getSpeed());

		if (obsvActivityDetect.getObsvActivityNorm() != null
				&& obsvActivityDetect.getObsvActivityNorm().getFinalLatitude() != null
				&& obsvActivityDetect.getObsvActivityNorm().getFinalLatitude()
						.floatValue() != 0.0f) {
			setLatitude(obsvActivityDetect.getObsvActivityNorm()
					.getFinalLatitude().floatValue());
			setNormalised(true);
		} else {
			setLatitude(obsvActivityDetect.getLatitude());
		}

		if (obsvActivityDetect.getObsvActivityNorm() != null
				&& obsvActivityDetect.getObsvActivityNorm().getFinalLongitude() != null
				&& obsvActivityDetect.getObsvActivityNorm().getFinalLongitude()
						.floatValue() != 0.0f) {
			setLongitude(obsvActivityDetect.getObsvActivityNorm()
					.getFinalLongitude().floatValue());
			setNormalised(true);
		} else {
			setLongitude(obsvActivityDetect.getLongitude());
		}

		setLocAccuracy(obsvActivityDetect.getLocAccuracy());
		setSensorId(obsvActivityDetect.getActivitySensor().getSensor()
				.getSensorId());
		// setObsvTime(DateUtil.convertUTCDateToLocalTime(obsvActivityDetect.getObsvTime()));
		setObsvTime(obsvActivityDetect.getObsvTime());
		setUpdated(obsvActivityDetect.getUpdated());
		setVersion(obsvActivityDetect.getVersion());
	}

	public static List<ObsvActivityDetectionVO> copyCollection(
			List<ObsvActivityDetection> obsvActivityDetectionList) {
		List<ObsvActivityDetectionVO> obsvActivityDetectionVOList = new LinkedList<ObsvActivityDetectionVO>();

		for (ObsvActivityDetection obsvActivityDetection : obsvActivityDetectionList) {
			ObsvActivityDetectionVO obsvActivityDetectionVO = new ObsvActivityDetectionVO();
			obsvActivityDetectionVO.clone(obsvActivityDetection);

			obsvActivityDetectionVOList.add(obsvActivityDetectionVO);
		}

		return obsvActivityDetectionVOList;
	}
}
