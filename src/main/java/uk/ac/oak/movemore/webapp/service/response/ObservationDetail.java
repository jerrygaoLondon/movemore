package uk.ac.oak.movemore.webapp.service.response;

import java.io.Serializable;
import java.util.Date;

import uk.ac.oak.movemore.webapp.model.Observations;

public class ObservationDetail extends SensorObservationSuccess implements
		Serializable {

	private static final long serialVersionUID = 4316111216829795098L;

	// The observation time sent from sensor
	private Date obsvTime;
	// The system time received from sensor
	private Date recordTime;
	// sensor observed value
	private String value;

	private Long sensorId;

	private String sensorName;

	private Double longitude;
	private Double latitude;

	public Date getObsvTime() {
		return obsvTime;
	}

	public void setObsvTime(Date obsvTime) {
		this.obsvTime = obsvTime;
	}

	public Date getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(Date recordTime) {
		this.recordTime = recordTime;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getSensorId() {
		return sensorId;
	}

	public void setSensorId(Long sensorId) {
		this.sensorId = sensorId;
	}

	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * Copy property values from the Observations model to current POJO
	 * 
	 * @param obsv
	 */
	public void copyProperties(Observations obsv) {
		setObsvId(String.valueOf(obsv.getObsvId()));
		setObsvTime(obsv.getObsvTime());
		setValue(obsv.getValue());
		setRecordTime(obsv.getRecordTime());
		setSensorId(obsv.getSensor() != null ? obsv.getSensor().getSensorId()
				: null);
		setSensorName(obsv.getSensor() != null ? obsv.getSensor().getName()
				: null);
		setLatitude((obsv.getSensor() != null && obsv.getSensor().getDevice() != null) ? obsv
				.getSensor().getDevice().getLatitude()
				: null);
		setLongitude((obsv.getSensor() != null && obsv.getSensor().getDevice() != null) ? obsv
				.getSensor().getDevice().getLongitude()
				: null);
	}

}
