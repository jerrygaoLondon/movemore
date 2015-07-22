package uk.ac.oak.movemore.webapp.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
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
import org.appfuse.model.BaseObject;

import uk.ac.oak.movemore.webapp.util.OBDDataUnitEnum;

@Entity
@Table(name = "obsv_obd_sensor")
@NamedQueries({ @NamedQuery(name = "findOBDObservationsBySensor", query = "SELECT distinct d FROM ObsvOBDDetection d JOIN d.obdSensor o where o.sensor =:sensor order by d.created desc") })
public class ObsvOBDDetection extends BaseObject implements Serializable {

	private static final long serialVersionUID = 1925170428903376411L;

	private Long obsvId;

	private Observations obdSensor;

	// Fuel Economy (unit: OBDDataUnitEnum.OBD_DATA_FUEL_ECONOMY)
	private Double fuelEconomy;

	// Vehicle Speed (unit: OBDDataUnitEnum.OBD_DATA_VEHICLE_SPEED)
	private Double vehicleSpeed;

	// Ambient Air Temperature (unit:
	// OBDDataUnitEnum.OBD_DATA_AMBIENT_AIR_TEMPERATURE)
	private Double ambientAirTemperature;

	// Engine RPM (unit: OBDDataUnitEnum.OBD_DATA_ENGINE_RPM)
	private Double engineRPM;

	// Fuel Level (unit: OBDDataUnitEnum.OBD_DATA_FUEL_LEVEL)
	private Double fuelLevel;

	// The observation time sent from sensor
	private Date obsvTime;

	//Engine Coolant Temperature (unit: OBDDataUnitEnum.OBD_DATA_ENGINE_COOLANT_TEMPERATURE)
	private Double engineCoolantTemperature;
	//Engine Load (unit: OBDDataUnitEnum.OBD_DATA_Engine_LOAD)
	private BigDecimal engineLoad;
	//Engine Runtime, e.g., "11:12:13"
	private Time EngineRuntime;
	//Barometric Pressure (unit: OBDDataUnitEnum.OBD_DATA_BAROMETRIC_PRESSURE)
	private Double barometricPressure;
	//Fuel Consumption (unit: OBDDataUnitEnum.OBD_DATA_FUEL_CONSUMPTION)
	private Double fuelConsumption;
	//Fuel Pressure (unit: OBDDataUnitEnum.OBD_DATA_FUEL_PRESSURE)
	private Double fuelPressure;
	//Mass Air Flow (unit: OBDDataUnitEnum.OBD_DATA_MASS_AIR_FLOW)
	private Double massAirFlow;
	//Intake Manifold Pressure (unit: OBD_DATA_INTAKE_MANIFOLD_PRESSURE)
	private Double intakeManifoldPressure;
	
	// observation location
	private Double longitude;
	private Double latitude;
	
	public ObsvOBDDetection() {
		
	}
	
	public ObsvOBDDetection (Double longitude, Double latitude, Date obsvTime) {
		setLongitude(longitude);
		setLatitude(latitude);
		setObsvTime(obsvTime);
		setUpdated(new Date());
	}

	/**
	 * set Java Persistence->JPA->Errors/Warnings->Queries and Generators and
	 * set level of “Generator is not defined in the persistence unit” to
	 * warning if it complains compilation error
	 * 
	 * @return
	 */
	@Id
	@Column(unique = true, nullable = false)
	public Long getObsvId() {
		return this.obsvId;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@MapsId
	public Observations getObdSensor() {
		return obdSensor;
	}

	public void setObdSensor(Observations obdSensor) {
		this.obdSensor = obdSensor;
	}

	public void setObsvId(Long obsvId) {
		this.obsvId = obsvId;
	}

	@Column
	public Double getFuelEconomy() {
		return fuelEconomy;
	}

	public void setFuelEconomy(Double fuelEconomy) {
		this.fuelEconomy = fuelEconomy;
	}

	@Column
	public Double getVehicleSpeed() {
		return vehicleSpeed;
	}

	public void setVehicleSpeed(Double vehicleSpeed) {
		this.vehicleSpeed = vehicleSpeed;
	}

	@Column
	public Double getAmbientAirTemperature() {
		return ambientAirTemperature;
	}

	public void setAmbientAirTemperature(Double ambientAirTemperature) {
		this.ambientAirTemperature = ambientAirTemperature;
	}

	@Column
	public Double getEngineRPM() {
		return engineRPM;
	}

	public void setEngineRPM(Double engineRPM) {
		this.engineRPM = engineRPM;
	}

	@Column
	public Double getFuelLevel() {
		return fuelLevel;
	}

	public void setFuelLevel(Double fuelLevel) {
		this.fuelLevel = fuelLevel;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "obsv_time", updatable = false)
	public Date getObsvTime() {
		return obsvTime;
	}

	public void setObsvTime(Date obsvTime) {
		this.obsvTime = obsvTime;
	}

	@Column(name = "longitude", precision = 13, scale = 10, updatable = false)
	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Column(name = "latitude", precision = 13, scale = 10, updatable = false)
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	@Column(name="engineCoolantTemperature")
	public Double getEngineCoolantTemperature() {
		return engineCoolantTemperature;
	}

	public void setEngineCoolantTemperature(Double engineCoolantTemperature) {
		this.engineCoolantTemperature = engineCoolantTemperature;
	}
	
	@Column(name="engineLoad", precision = 3, scale = 2)
	public BigDecimal getEngineLoad() {
		return engineLoad;
	}

	public void setEngineLoad(BigDecimal engineLoad) {
		this.engineLoad = engineLoad;
	}
	@Column(name="engineRuntime", columnDefinition="TIME")
	public Time getEngineRuntime() {
		return EngineRuntime;
	}

	public void setEngineRuntime(Time engineRuntime) {
		EngineRuntime = engineRuntime;
	}
	
	@Column(name="barometricPressure")
	public Double getBarometricPressure() {
		return barometricPressure;
	}

	public void setBarometricPressure(Double barometricPressure) {
		this.barometricPressure = barometricPressure;
	}

	@Column(name="fuelConsumption")
	public Double getFuelConsumption() {
		return fuelConsumption;
	}

	public void setFuelConsumption(Double fuelConsumption) {
		this.fuelConsumption = fuelConsumption;
	}
	
	@Column(name="fuelPressure")
	public Double getFuelPressure() {
		return fuelPressure;
	}

	public void setFuelPressure(Double fuelPressure) {
		this.fuelPressure = fuelPressure;
	}

	@Column(name="massAirFlow")
	public Double getMassAirFlow() {
		return massAirFlow;
	}

	public void setMassAirFlow(Double massAirFlow) {
		this.massAirFlow = massAirFlow;
	}

	@Column(name="intakeManifoldPressure")
	public Double getIntakeManifoldPressure() {
		return intakeManifoldPressure;
	}

	public void setIntakeManifoldPressure(Double intakeManifoldPressure) {
		this.intakeManifoldPressure = intakeManifoldPressure;
	}

	@Override
	public String toString() {
		return this.getObsvId() + "," + this.fuelEconomy
				+ OBDDataUnitEnum.OBD_DATA_FUEL_ECONOMY.getUnit() + ","
				+ this.vehicleSpeed
				+ OBDDataUnitEnum.OBD_DATA_VEHICLE_SPEED.getUnit() + ","
				+ this.ambientAirTemperature
				+ OBDDataUnitEnum.OBD_DATA_AMBIENT_AIR_TEMPERATURE.getUnit()
				+ "," + this.engineRPM
				+ OBDDataUnitEnum.OBD_DATA_ENGINE_RPM.getUnit() + ","
				+ this.fuelLevel
				+ OBDDataUnitEnum.OBD_DATA_FUEL_LEVEL.getUnit() + ","
				+ this.latitude + "," + this.longitude + this.getObsvTime();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ObsvOBDDetection) {
			final ObsvOBDDetection otherObsv = (ObsvOBDDetection) obj;
			return new EqualsBuilder()
					.append(fuelEconomy, otherObsv.getFuelEconomy())
					.append(vehicleSpeed, otherObsv.getVehicleSpeed())
					.append(ambientAirTemperature,
							otherObsv.getAmbientAirTemperature())
					.append(engineRPM, otherObsv.getEngineRPM())
					.append(fuelLevel, otherObsv.getFuelLevel())
					.append(obsvTime, otherObsv.getObsvTime())
					.append(latitude, otherObsv.getLatitude())
					.append(longitude, otherObsv.getLongitude()).isEquals();
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(obsvTime).append(fuelEconomy)
				.append(vehicleSpeed).append(ambientAirTemperature)
				.append(engineRPM).append(fuelLevel).append(latitude)
				.append(longitude).toHashCode();
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
}
