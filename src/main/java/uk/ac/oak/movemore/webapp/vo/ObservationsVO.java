package uk.ac.oak.movemore.webapp.vo;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.util.DateUtil;
import uk.ac.oak.movemore.webapp.util.SensorMediaFileManager;

public class ObservationsVO implements Serializable{
	
	private static final long serialVersionUID = -4565927411994054365L;
	protected final Log log = LogFactory.getLog(ObservationsVO.class);
			
	private Long obsvId;
	//The observation time sent from sensor
	private String obsvTime;
	//sensor observed value
	private String value;
	
	// observation location
	private Double longitude;
	private Double latitude;
	private Long sensorId;
//	private Date created;
	private Date updated;
	private String attachmentAbsolutePath;
	private Integer version;
	
	private SensorMediaFileManager sensorFileManager = new SensorMediaFileManager();
	
	public Long getObsvId() {
		return obsvId;
	}

	public void setObsvId(Long obsvId) {
		this.obsvId = obsvId;
	}

	public String getObsvTime() {
		return obsvTime;
	}

	public void setObsvTime(String obsvTime) {
		this.obsvTime = obsvTime;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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

	public Long getSensorId() {
		return sensorId;
	}

	public void setSensorId(Long sensorId) {
		this.sensorId = sensorId;
	}
	
//	public Date getCreated() {
//		return created;
//	}
//
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

	public void clone(Observations obsv) {
		setObsvId(obsv.getObsvId());
		setObsvTime(DateUtil.convertUTCDateToLocalTime(obsv.getObsvTime()));
		setLatitude(obsv.getLatitude());
		setLongitude(obsv.getLongitude());
		setValue(obsv.getValue());
		setSensorId(obsv.getSensor().getSensorId());
//		setCreated(obsv.getCreated());
		setUpdated(obsv.getUpdated());
		setVersion(obsv.getVersion());
		try {
			if (obsv.getAttachment() != null) {
				setAttachmentAbsolutePath(sensorFileManager.getAbsolutePath(obsv.getAttachment()));
			}
		} catch (IOException ioe) {
			log.error("Failure to retrieve file absolute path");
			log.error(ioe.toString());
		}
	}
	
	public static List<ObservationsVO> copyCollection (List<Observations> obsvList) {
		List<ObservationsVO> obsvVOList = new LinkedList<ObservationsVO>();
		for (Observations obsv : obsvList) {
			ObservationsVO obsvVO = new ObservationsVO();
			obsvVO.clone(obsv);
			obsvVOList.add(obsvVO);
		}
		return obsvVOList;
		
	}
}
