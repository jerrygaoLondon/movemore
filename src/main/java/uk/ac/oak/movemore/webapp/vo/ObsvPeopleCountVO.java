package uk.ac.oak.movemore.webapp.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import uk.ac.oak.movemore.webapp.model.ObsvPeopleCount;
import uk.ac.oak.movemore.webapp.util.DateUtil;


public class ObsvPeopleCountVO  implements Serializable{
	
	private static final long serialVersionUID = -576125720451392322L;
	private Long obsvId;
	private Long sensorId;
	private Integer number;
	// The observation time sent from sensor
	private String obsvTime;

	// observation location
	private Double longitude;
	private Double latitude;
//	private Date created;
	private Date updated;
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
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public String getObsvTime() {
		return obsvTime;
	}
	public void setObsvTime(String obsvTime) {
		this.obsvTime = obsvTime;
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
//	public Date getCreated() {
//		return created;
//	}
//	public void setCreated(Date created) {
//		this.created = created;
//	}
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
	
	public void clone(ObsvPeopleCount obsvPeopleCount) {
		setObsvId(obsvPeopleCount.getObsvId());
		setSensorId(obsvPeopleCount.getpCount().getSensor().getSensorId());
		setNumber(obsvPeopleCount.getNumber());
		setObsvTime(DateUtil.convertUTCDateToLocalTime(obsvPeopleCount.getObsvTime()));
		setLatitude(obsvPeopleCount.getLatitude());
		setLongitude(obsvPeopleCount.getLongitude());
//		setCreated(obsvPeopleCount.getCreated());
		setUpdated(obsvPeopleCount.getUpdated());
		setVersion(obsvPeopleCount.getVersion());
	}
	
	public static List<ObsvPeopleCountVO> copyCollection(List<ObsvPeopleCount> obsvPeopleCountList) {
		List<ObsvPeopleCountVO> obsvPeopleCountVOList = new LinkedList<ObsvPeopleCountVO>();
		
		for (ObsvPeopleCount obsvPeopleCount : obsvPeopleCountList) {
			ObsvPeopleCountVO obsvPeopleCountVO = new ObsvPeopleCountVO();
			obsvPeopleCountVO.clone(obsvPeopleCount);
			obsvPeopleCountVOList.add(obsvPeopleCountVO);
		}
		
		return obsvPeopleCountVOList;
	}
}
