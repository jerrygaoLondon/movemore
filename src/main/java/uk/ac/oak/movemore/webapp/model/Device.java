package uk.ac.oak.movemore.webapp.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.appfuse.model.BaseObject;

/**
 * This class is used to represent available sensor devices (e.g.,Raspberry Pi
 * box) in the database.
 * 
 */
@Entity
@Table(name = "device")
@NamedQueries({ @NamedQuery(name = "findDeviceByName", query = "select d from Device d where d.name = :name "),
	@NamedQuery(name = "findDeviceByPhysicalId", query = "select d from Device d where d.devicePhysicalId = :devicePhysicalId ")})
public class Device extends BaseObject implements Serializable {

	private static final long serialVersionUID = -2076144249499518455L;
	private Long deviceId;
	private String name;
	private String description;
	private String devicePhysicalId;
	// device MAC Address
//	private String macAddress;
	// device current location
	private Float longitude;

	private Float latitude;
	
	private Float batteryLevel;
	// device current/last known IP address
	private String lastKnownIP;
	private Long numericLastKnownIP;
	private Set<Sensors> sensors = new HashSet<Sensors>(0);
//	private Set<DeviceMessages> deviceMessages = new HashSet<DeviceMessages>(0);
	
	public Device() {
	}
	
	public Device (String name, String description) {
		this.name = name;
		this.description = description;
		setUpdated(new Date());
	}
	
	public Device (String devicePhysicalId, String name, String description) {
		this.devicePhysicalId = devicePhysicalId;
		this.name = name;
		this.description = description;
		setUpdated(new Date());
	}
	
	public Device (String name, String description, Set<Sensors> sensors) {
		this.name = name;
		this.description = description;
		this.sensors = sensors;
		setUpdated(new Date());
	}	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="device_id", unique = true, nullable = false)
	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}

	@Column(length = 100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(length = 150)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "longitude", precision = 13, scale = 10)
	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}
	
	@Column(name = "latitude", precision = 13, scale = 10)
	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	@Column(name = "batteryLevel", precision = 13, scale = 10)
	public Float getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(Float batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	@Column(name = "numericLastKnownIP", length = 64)
	public Long getNumericLastKnownIP() {
		return this.numericLastKnownIP;
	}
	
	public void setNumericLastKnownIP(Long numericLastKnownIP) {
		this.numericLastKnownIP = numericLastKnownIP;
	}

	@Column(length = 64)
	public String getLastKnownIP() {
		return lastKnownIP;
	}

	public void setLastKnownIP(String lastKnownIP) {
		this.lastKnownIP = lastKnownIP;
		this.numericLastKnownIP = getNumericIP(lastKnownIP);
	}
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "device")
	public Set<Sensors> getSensors() {
		return this.sensors;
	}
 
	public void setSensors(Set<Sensors> sensors) {
		this.sensors = sensors;
	}	
	
//	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JoinTable(
//	            name = "device_messages",
//	            joinColumns = { @JoinColumn(name = "device_id", nullable = false, updatable = false) }
//	)
//	public Set<DeviceMessages> getDeviceMessages() {
//		return this.deviceMessages;
//	}
//
//	public void setDeviceMessages(Set<DeviceMessages> deviceMessages) {
//		this.deviceMessages = deviceMessages;
//	}
//	@Column(name = "macAddress")
//	public String getMacAddress() {
//		return this.macAddress;
//	}
//
//	public void setMacAddress(String macAddress) {
//		this.macAddress = macAddress;
//	}
	
	@Override
	public String toString() {
		return getName() + "," + getLongitude() + "," + getLatitude() + ","
				+ getLastKnownIP() + "," + getDescription();
	}
	
	@Column(name = "device_physical_id", length=30)
	public String getDevicePhysicalId() {
		return devicePhysicalId;
	}

	public void setDevicePhysicalId(String devicePhysicalId) {
		this.devicePhysicalId = devicePhysicalId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Device) {
			final Device otherDevice = (Device) obj;
			return new EqualsBuilder().append(name, otherDevice.name)
					.append(description, otherDevice.description)
					.append(latitude, otherDevice.latitude)
					.append(longitude, otherDevice.longitude).isEquals();
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(name).append(description)
				.append(latitude).append(longitude).toHashCode();
	}

	public static String getStandardIP(long numericIP) {
		return ((numericIP >> 24) & 0xFF) + "." + ((numericIP >> 16) & 0xFF)
				+ "." + ((numericIP >> 8) & 0xFF) + "." + (numericIP & 0xFF);
	}

	public static long getNumericIP(String standardIP) {
		long result = 0;
		if (!StringUtils.isEmpty(standardIP)) {

			String[] ipAddressInArray = standardIP.split("\\.");

			for (int i = 3; i >= 0; i--) {

				long ip = Long.parseLong(ipAddressInArray[3 - i]);

				// left shifting 24,16,8,0 and bitwise OR

				// 1. 192 << 24
				// 1. 168 << 16
				// 1. 1 << 8
				// 1. 2 << 0
				result |= ip << (i * 8);
			}
		}
		return result;
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

	public static void main(String[] args) {
		String ipaddress = "143.167.10.249";
		long numericIp = getNumericIP(ipaddress);
		System.out.println("numericIp: " + numericIp);

		String convertedIPAddress = getStandardIP(numericIp);
		System.out.println("ip address: " + convertedIPAddress);
	
		String macAddress = "AA:BB:CC:DD:EE:FF";
		String[] macAddressParts = macAddress.split(":");

		// convert hex string to byte values
		Byte[] macAddressBytes = new Byte[6];
		for(int i=0; i<6; i++){
		    Integer hex = Integer.parseInt(macAddressParts[i], 16);
		    macAddressBytes[i] = hex.byteValue();
		}
	}
}
