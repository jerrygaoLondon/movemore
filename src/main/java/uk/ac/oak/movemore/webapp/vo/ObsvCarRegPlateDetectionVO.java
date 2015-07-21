package uk.ac.oak.movemore.webapp.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import uk.ac.oak.movemore.webapp.model.ObsvCarRegPlateDetection;
import uk.ac.oak.movemore.webapp.util.DateUtil;

public class ObsvCarRegPlateDetectionVO  implements Serializable {
	
	private static final long serialVersionUID = -8376916752468775853L;
	private Long obsvId;
	private String carRegPlateNum;
	
	//The observation time sent from sensor
	private String obsvTime;

	// observation location
	private Float longitude;
	private Float latitude;
	private Long sensorId;
//	private Date created;
	private Date updated;
	private Integer version;
	
	public Long getObsvId() {
		return obsvId;
	}
	public void setObsvId(Long obsvId) {
		this.obsvId = obsvId;
	}
	public String getCarRegPlateNum() {
		return carRegPlateNum;
	}
	public void setCarRegPlateNum(String carRegPlateNum) {
		this.carRegPlateNum = carRegPlateNum;
	}
	public String getObsvTime() {
		return obsvTime;
	}
	public void setObsvTime(String obsvTime) {
		this.obsvTime = obsvTime;
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
	
	public Long getSensorId() {
		return sensorId;
	}
	
	public void setSensorId(Long sensorId) {
		this.sensorId = sensorId;
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
	public void clone(ObsvCarRegPlateDetection obsvCarRegPlateDetection) {
		setObsvId(obsvCarRegPlateDetection.getObsvId());
		setCarRegPlateNum(obsvCarRegPlateDetection.getCarRegPlateNum());
		setLatitude(obsvCarRegPlateDetection.getLatitude());
		setLongitude(obsvCarRegPlateDetection.getLongitude());
		setObsvTime(DateUtil.convertUTCDateToLocalTime(obsvCarRegPlateDetection.getObsvTime()));
		setSensorId(obsvCarRegPlateDetection.getCarRegPlate().getSensor().getSensorId());
//		setCreated(obsvCarRegPlateDetection.getCreated());
		setUpdated(obsvCarRegPlateDetection.getUpdated());
		setVersion(obsvCarRegPlateDetection.getVersion());
	}
	
	public static List<ObsvCarRegPlateDetectionVO> copyCollection(List<ObsvCarRegPlateDetection> obsvCarRegPlateList) {
		List<ObsvCarRegPlateDetectionVO> obsvCarRegPlateVOList = new LinkedList<ObsvCarRegPlateDetectionVO>();
		for (ObsvCarRegPlateDetection obsvCarRegPlate : obsvCarRegPlateList) {
			ObsvCarRegPlateDetectionVO obsvCarRegPlateVO = new ObsvCarRegPlateDetectionVO();
			obsvCarRegPlateVO.clone(obsvCarRegPlate);
			obsvCarRegPlateVOList.add(obsvCarRegPlateVO);
		}
		return obsvCarRegPlateVOList;
	}
}
