package uk.ac.oak.movemore.webapp.vo;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.oak.movemore.webapp.model.ObsvVoiceDecibelDetection;
import uk.ac.oak.movemore.webapp.util.DateUtil;
import uk.ac.oak.movemore.webapp.util.SensorMediaFileManager;

public class ObsvVoiceDecibelDetectionVO implements Serializable{
	
	private static final long serialVersionUID = -6904096061130898593L;
	
	protected final Log log = LogFactory.getLog(ObsvVoiceDecibelDetectionVO.class);
	
	private Long obsvId;
	private Long sensorId;
	private Double decibel;

	// The observation time sent from sensor
	private String obsvTime;

	// observation location
	private Float longitude;
	private Float latitude;
	
//	private Date created;
	private Date updated;
	private Integer version;
	
	private String attachmentAbsolutePath;
	
	private SensorMediaFileManager sensorFileManager = new SensorMediaFileManager();
	
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
	public Double getDecibel() {
		return decibel;
	}
	public void setDecibel(Double decibel) {
		this.decibel = decibel;
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
	
	public String getAttachmentAbsolutePath() {
		return attachmentAbsolutePath;
	}
	public void setAttachmentAbsolutePath(String attachmentAbsolutePath) {
		this.attachmentAbsolutePath = attachmentAbsolutePath;
	}
	public void clone(ObsvVoiceDecibelDetection obsvVoiceDecibelDetection) {
		setObsvId(obsvVoiceDecibelDetection.getObsvId());
		setSensorId(obsvVoiceDecibelDetection.getVoiceSensor().getSensor().getSensorId());
		setDecibel(obsvVoiceDecibelDetection.getDecibel());
		setObsvTime(DateUtil.convertUTCDateToLocalTime(obsvVoiceDecibelDetection.getObsvTime()));
		setLatitude(obsvVoiceDecibelDetection.getLatitude());
		setLongitude(obsvVoiceDecibelDetection.getLongitude());
//		setCreated(obsvVoiceDecibelDetection.getCreated());
		setUpdated(obsvVoiceDecibelDetection.getUpdated());
		setVersion(obsvVoiceDecibelDetection.getVersion());
		try {
			if (obsvVoiceDecibelDetection.getAttachment()!=null) {
				String absPath = sensorFileManager.getAbsolutePath(obsvVoiceDecibelDetection.getAttachment());
				setAttachmentAbsolutePath(absPath);
			}
		} catch (IOException ioe) {
			log.error("Failure to retrieve file absolute path");
			log.error(ioe.toString());
		}
	}
	
	public static List<ObsvVoiceDecibelDetectionVO> copyCollection(List<ObsvVoiceDecibelDetection> obsvVoiceList) {
		List<ObsvVoiceDecibelDetectionVO> obsvVoiceVOList = new LinkedList<ObsvVoiceDecibelDetectionVO>();
		
		for (ObsvVoiceDecibelDetection obsvVoice : obsvVoiceList) {
			ObsvVoiceDecibelDetectionVO obsvVoiceVO = new ObsvVoiceDecibelDetectionVO();
			obsvVoiceVO.clone(obsvVoice);
			obsvVoiceVOList.add(obsvVoiceVO);
		}
		
		return obsvVoiceVOList;
	}
}
