package uk.ac.oak.movemore.webapp.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

@Entity
@Table(name = "obsv_carregplate_detection")
@NamedQueries({ @NamedQuery(name = "findCarRegPlateObsvsBySensor", query = "SELECT distinct d FROM ObsvCarRegPlateDetection d JOIN d.carRegPlate o where o.sensor =:sensor order by d.created desc")})
public class ObsvCarRegPlateDetection extends BaseObject implements Serializable {

	private static final long serialVersionUID = -3569528628806301830L;
	protected final Log log = LogFactory.getLog(ObsvCarRegPlateDetection.class);
	
	private Long obsvId;
	private Observations carRegPlate;
	//hex encoded number of car reg plate
	//leave the column for future optimisation purpose
	private BigInteger hexCarRegPlateNum;
	private String carRegPlateNum;
	//The observation time sent from sensor
	private Date obsvTime;

	// observation location
	private Double longitude;
	private Double latitude;
	
	public ObsvCarRegPlateDetection() {
	}
	
	public ObsvCarRegPlateDetection(Observations obsv, String carRegPlateNum, Date obsvTime) {
		setCarRegPlate(obsv);
		setCarRegPlateNum(carRegPlateNum);
		setObsvTime(obsvTime);
		this.updated = new Date();
	}
	
	@Id
	@Column(unique = true, nullable = false)
	public Long getObsvId() {
		return obsvId;
	}

	public void setObsvId(Long obsvId) {
		this.obsvId = obsvId;
	}

	@OneToOne(fetch = FetchType.LAZY)
    @MapsId
	public Observations getCarRegPlate() {
		return carRegPlate;
	}

	public void setCarRegPlate(Observations carRegPlate) {
		this.carRegPlate = carRegPlate;
	}

	@Column(name="hex_carRegPlate_num", columnDefinition = "BIGINT", length=39)
	public BigInteger getHexCarRegPlateNum() {
		return hexCarRegPlateNum;
	}

	public void setHexCarRegPlateNum(BigInteger hexCarRegPlateNum) {
		this.hexCarRegPlateNum = hexCarRegPlateNum;
	}
	
	@Column(name="carRegPlate_num", updatable=false)
	public String getCarRegPlateNum() {
		return carRegPlateNum;
	}

	public void setCarRegPlateNum(String carRegPlateNum) {
		this.carRegPlateNum = carRegPlateNum;
/*		try {
			this.hexCarRegPlateNum = convertCarRegPlateNum(carRegPlateNum);
		} catch (Exception ex) {
			//log.error(String.format("Error in converting car registration plate to long. Input car plate number is %s. Exception due to [%s]", carRegPlateNum,ex.getMessage()));
		}*/
	}
	
	public static BigInteger convertCarRegPlateNum (String carRegPlateNum) {
		return new BigInteger(carRegPlateNum, 16);
	}
	
	public static void main(String[] args) {
		String entryptedCarRegPlateNum = "c518914cdda33ad6218c9b1dd6860620";
		BigInteger hexCarRegPlateNum = new BigInteger(entryptedCarRegPlateNum, 16);
		System.out.println("hexCarRegPlateNum: "+ hexCarRegPlateNum);
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "obsv_time", updatable=false)
	public Date getObsvTime() {
		return obsvTime;
	}

	public void setObsvTime(Date obsvTime) {
		this.obsvTime = obsvTime;
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
	
	@PrePersist
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
	
	@Override
	public String toString() {
		return this.getCarRegPlateNum()+","+this.getLatitude()+","+this.getLongitude()+","+getObsvTime();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ObsvCarRegPlateDetection) {
			final ObsvCarRegPlateDetection otherObsv = (ObsvCarRegPlateDetection) obj;
			return new EqualsBuilder().append(carRegPlateNum, otherObsv.getCarRegPlateNum())
					.append(obsvTime, otherObsv.getObsvTime())
					.append(latitude, otherObsv.getLatitude())
					.append(longitude, otherObsv.getLongitude())
					.isEquals();
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(obsvTime).append(carRegPlateNum).append(latitude).append(longitude).toHashCode();
	}

}
