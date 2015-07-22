package uk.ac.oak.movemore.webapp.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.appfuse.model.BaseObject;

import uk.ac.oak.movemore.webapp.util.MACAddress;

/**
 * Observation details table for device detectors/sensors (e.g., wifi counter, bluetooth scanner)
 * @author jieg
 *
 */
@Entity
@Table(name = "obsv_device_detection")
@NamedQueries({ @NamedQuery(name = "findDeviceObservationsBySensor", query = "SELECT distinct d FROM ObsvDeviceDetection d JOIN d.devDetect o where o.sensor =:sensor order by d.created desc")})
public class ObsvDeviceDetection extends BaseObject implements Serializable {

	private static final long serialVersionUID = -5809121804147485225L;
	protected final Log log = LogFactory.getLog(ObsvDeviceDetection.class);
			
	private Long obsvId;
	private Observations devDetect;
	//hex encoded number of Mac address
	private Long hexMacAddress;
	private String macAddress;
	private Integer signalStrength;
	//The observation time sent from sensor
	private Date obsvTime;
	private Date firstObsvTime;
	private Date lastObsvTime;
	
	// observation location
	private Double longitude;
	private Double latitude;
	
	public ObsvDeviceDetection(){
	}
	
	public ObsvDeviceDetection(Observations obsv, Date obsvTime) {
		setDevDetect(obsv);
		setObsvTime(obsvTime);
		this.updated = new Date();
	}
	
	public ObsvDeviceDetection(Observations obsv, String macAddress, Date obsvTime) {
		setDevDetect(obsv);
		setMacAddress(macAddress);
		setObsvTime(obsvTime);
		this.updated = new Date();
	}

	@Id
	@Column(unique = true, nullable = false)
	public Long getObsvId() {
		return this.obsvId;
	}

	public void setObsvId(Long obsvId) {
		this.obsvId = obsvId;
	}
	
	@OneToOne(cascade = CascadeType.ALL)
    @MapsId
	public Observations getDevDetect() {
		return devDetect;
	}

	public void setDevDetect(Observations devDetect) {
		this.devDetect = devDetect;
	}
    
	@Column(name="hex_macAddress")
	public Long getHexMacAddress() {
		return hexMacAddress;
	}

	public void setHexMacAddress(Long hexMacAddress) {
		this.hexMacAddress = hexMacAddress;
	}

	@Column(name="macAddress", length=50, updatable=false)
	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
/*		try {
			this.hexMacAddress = MACAddress.valueOf(macAddress).toLong();
		} catch (Exception ex) {
			log.error(String.format("Error in converting mac address to long. Input mac address is %s. Exception due to [%s]", macAddress,ex.getMessage()));
		}*/
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "obsv_time", updatable=false)
	public Date getObsvTime() {
		return obsvTime;
	}

	public void setObsvTime(Date obsvTime) {
		this.obsvTime = obsvTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "first_obsv_time", updatable=false)
	public Date getFirstObsvTime() {
		return firstObsvTime;
	}

	public void setFirstObsvTime(Date firstObsvTime) {
		this.firstObsvTime = firstObsvTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_obsv_time", updatable=false)
	public Date getLastObsvTime() {
		return lastObsvTime;
	}

	public void setLastObsvTime(Date lastObsvTime) {
		this.lastObsvTime = lastObsvTime;
	}

	@Column(name = "longitude", updatable=false)
	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Column(name = "latitude", updatable=false)
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	@Column(name = "signalStrength", updatable=false)
	public Integer getSignalStrength() {
		return signalStrength;
	}

	public void setSignalStrength(Integer signalStrength) {
		this.signalStrength = signalStrength;
	}

	public static void main(String[] args) {
		String macAddress= "68:96:7B:34:D6:9E";
		
		Long hexMacAddress;
		hexMacAddress = MACAddress.valueOf(macAddress).toLong();
		System.out.println("hexMacAddress:"+ hexMacAddress);		
		
		String macAddressStr = MACAddress.valueOf(hexMacAddress).toString();
		System.out.println("macAddressStr:"+macAddressStr);
	}

	@Override
	public String toString() {
		return this.getObsvId()+","+this.getMacAddress()+","+this.getObsvTime();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ObsvDeviceDetection) {
			final ObsvDeviceDetection otherObsv = (ObsvDeviceDetection) obj;
			return new EqualsBuilder().append(obsvTime, otherObsv.getObsvTime())
					.append(macAddress, otherObsv.getMacAddress())
					.append(latitude, otherObsv.getLatitude())
					.append(longitude, otherObsv.getLongitude())
					.isEquals();
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(obsvTime).append(macAddress).append(latitude).append(longitude).toHashCode();
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
	
	//avoid conflicting updates
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
