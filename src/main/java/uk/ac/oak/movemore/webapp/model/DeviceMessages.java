package uk.ac.oak.movemore.webapp.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.appfuse.model.BaseObject;

@Entity
@Table(name = "device_messages")
@NamedQueries({ @NamedQuery(name = "findDeviceMessagesById", query = "select m from DeviceMessages m where m.deviceMessagesId.messageId = :messageId "),
				@NamedQuery(name = "findDeviceMessagesByDeviceId", query = "select m from DeviceMessages m where m.deviceMessagesId.deviceId = :deviceId ")})
public class DeviceMessages extends BaseObject implements Serializable {

	private static final long serialVersionUID = -7328549691951782119L;

	@EmbeddedId
	private DeviceMessagesId deviceMessagesId;

	private String message;
	private boolean isActive;
		
	public DeviceMessagesId getDeviceMessagesId() {
		return deviceMessagesId;
	}

	public void setDeviceMessagesId(DeviceMessagesId deviceMessagesId) {
		this.deviceMessagesId = deviceMessagesId;
	}

	@Column(length = 200)
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@Column(name = "isActive", nullable = false, columnDefinition = "TINYINT(1)")
	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
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
    @Column(name = "updated", nullable = false)
	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = new Date();
	}
	
	//avoid conflicting updates
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Override
	public String toString() {
		return getDeviceMessagesId().getMessageId() + "," + getDeviceMessagesId().getDeviceId() + "," + getMessage();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DeviceMessages) {
			final DeviceMessages otherMessage = (DeviceMessages) obj;
			return new EqualsBuilder().append(getDeviceMessagesId().getMessageId(), otherMessage.getDeviceMessagesId().getMessageId())
					.isEquals();
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getDeviceMessagesId().getMessageId()).append(getDeviceMessagesId().getDeviceId()).toHashCode();
	}
}
