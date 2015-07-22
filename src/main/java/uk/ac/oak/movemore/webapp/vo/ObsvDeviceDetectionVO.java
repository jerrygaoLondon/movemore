package uk.ac.oak.movemore.webapp.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import uk.ac.oak.movemore.webapp.model.ObsvDeviceDetection;
import uk.ac.oak.movemore.webapp.util.DateUtil;

public class ObsvDeviceDetectionVO  implements Serializable{
	
	private static final long serialVersionUID = 3741778621334435508L;
	
	private Long obsvId;
	private Long sensorId;
	private String macAddress;
	private Integer signalStrength;
	//The observation time sent from sensor
	private String obsvTime;
	private Date firstObsvTime;
	private Date lastObsvTime;
	
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
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public Integer getSignalStrength() {
		return signalStrength;
	}
	public void setSignalStrength(Integer signalStrength) {
		this.signalStrength = signalStrength;
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
	
	public Date getFirstObsvTime() {
		return firstObsvTime;
	}
	public void setFirstObsvTime(Date firstObsvTime) {
		this.firstObsvTime = firstObsvTime;
	}
	public Date getLastObsvTime() {
		return lastObsvTime;
	}
	public void setLastObsvTime(Date lastObsvTime) {
		this.lastObsvTime = lastObsvTime;
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
	
	public void clone(ObsvDeviceDetection obsvDeviceDetection) {
		setObsvId(obsvDeviceDetection.getObsvId());
		setSensorId(obsvDeviceDetection.getDevDetect().getSensor().getSensorId());				
		setObsvTime(DateUtil.convertUTCDateToLocalTime(obsvDeviceDetection.getObsvTime()));
		setMacAddress(obsvDeviceDetection.getMacAddress());
		setLatitude(obsvDeviceDetection.getLatitude());
		setLongitude(obsvDeviceDetection.getLongitude());
		//setCreated(obsvDeviceDetection.getCreated());
		setUpdated(obsvDeviceDetection.getUpdated());
		setFirstObsvTime(obsvDeviceDetection.getFirstObsvTime());
		setLastObsvTime(obsvDeviceDetection.getLastObsvTime());
		setVersion(obsvDeviceDetection.getVersion());
	}
	
	public static List<ObsvDeviceDetectionVO> copyCollection(List<ObsvDeviceDetection> obsvList) {
		List<ObsvDeviceDetectionVO> obsvVOList = new LinkedList<ObsvDeviceDetectionVO>();
		for (ObsvDeviceDetection obsvDeviceDetection : obsvList) {
			ObsvDeviceDetectionVO obsvDeviceDetectionVO = new ObsvDeviceDetectionVO();
			obsvDeviceDetectionVO.clone(obsvDeviceDetection);
			
			obsvVOList.add(obsvDeviceDetectionVO);
		}
		return obsvVOList;
	}
	

}
