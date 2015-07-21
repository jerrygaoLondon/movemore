package uk.ac.oak.movemore.webapp.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import uk.ac.oak.movemore.webapp.model.ObsvOBDDetection;
import uk.ac.oak.movemore.webapp.util.DateUtil;

public class ObsvOBDDetectionVO  implements Serializable{

	private static final long serialVersionUID = -4791177082173735775L;
	private Long obsvId;
	private Long sensorId;
	// Fuel Economy (unit: OBDDataUnitEnum.OBD_DATA_Fuel_Economy)
	private Double fuelEconomy;

	// Vehicle Speed (unit: OBDDataUnitEnum.OBD_DATA_Vehicle_Speed)
	private Double vehicleSpeed;

	// Ambient Air Temperature (unit:
	// OBDDataUnitEnum.OBD_DATA_Ambient_Air_Temperature)
	private Double ambientAirTemperature;

	// Engine RPM (unit: OBDDataUnitEnum.OBD_DATA_Engine_RPM)
	private Double engineRPM;

	// Fuel Level (unit: OBDDataUnitEnum.OBD_DATA_Fuel_Level)
	private Double fuelLevel;

	// The observation time sent from sensor
	private String obsvTime;

	// observation location
	private Float longitude;
	private Float latitude;
	
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
	public Double getFuelEconomy() {
		return fuelEconomy;
	}
	public void setFuelEconomy(Double fuelEconomy) {
		this.fuelEconomy = fuelEconomy;
	}
	public Double getVehicleSpeed() {
		return vehicleSpeed;
	}
	public void setVehicleSpeed(Double vehicleSpeed) {
		this.vehicleSpeed = vehicleSpeed;
	}
	public Double getAmbientAirTemperature() {
		return ambientAirTemperature;
	}
	public void setAmbientAirTemperature(Double ambientAirTemperature) {
		this.ambientAirTemperature = ambientAirTemperature;
	}
	public Double getEngineRPM() {
		return engineRPM;
	}
	public void setEngineRPM(Double engineRPM) {
		this.engineRPM = engineRPM;
	}
	public Double getFuelLevel() {
		return fuelLevel;
	}
	public void setFuelLevel(Double fuelLevel) {
		this.fuelLevel = fuelLevel;
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
	
	public Double getEngineCoolantTemperature() {
		return engineCoolantTemperature;
	}
	public void setEngineCoolantTemperature(Double engineCoolantTemperature) {
		this.engineCoolantTemperature = engineCoolantTemperature;
	}
	public BigDecimal getEngineLoad() {
		return engineLoad;
	}
	public void setEngineLoad(BigDecimal engineLoad) {
		this.engineLoad = engineLoad;
	}
	public Time getEngineRuntime() {
		return EngineRuntime;
	}
	public void setEngineRuntime(Time engineRuntime) {
		EngineRuntime = engineRuntime;
	}
	public Double getBarometricPressure() {
		return barometricPressure;
	}
	public void setBarometricPressure(Double barometricPressure) {
		this.barometricPressure = barometricPressure;
	}
	public Double getFuelConsumption() {
		return fuelConsumption;
	}
	public void setFuelConsumption(Double fuelConsumption) {
		this.fuelConsumption = fuelConsumption;
	}
	public Double getFuelPressure() {
		return fuelPressure;
	}
	public void setFuelPressure(Double fuelPressure) {
		this.fuelPressure = fuelPressure;
	}
	public Double getMassAirFlow() {
		return massAirFlow;
	}
	public void setMassAirFlow(Double massAirFlow) {
		this.massAirFlow = massAirFlow;
	}
	public Double getIntakeManifoldPressure() {
		return intakeManifoldPressure;
	}
	public void setIntakeManifoldPressure(Double intakeManifoldPressure) {
		this.intakeManifoldPressure = intakeManifoldPressure;
	}
	public void clone(ObsvOBDDetection obsvOBDDetection) {
		setObsvId(obsvOBDDetection.getObsvId());
		setSensorId(obsvOBDDetection.getObdSensor().getSensor().getSensorId());
		setAmbientAirTemperature(obsvOBDDetection.getAmbientAirTemperature());
		setEngineRPM(obsvOBDDetection.getEngineRPM());
		setFuelEconomy(obsvOBDDetection.getFuelEconomy());
		setFuelLevel(obsvOBDDetection.getFuelLevel());
		setLatitude(obsvOBDDetection.getLatitude());
		setLongitude(obsvOBDDetection.getLongitude());
		setVehicleSpeed(obsvOBDDetection.getVehicleSpeed());
		setObsvTime(DateUtil.convertUTCDateToLocalTime(obsvOBDDetection.getObsvTime()));		
		
		setEngineCoolantTemperature(obsvOBDDetection.getEngineCoolantTemperature());
		setEngineLoad(obsvOBDDetection.getEngineLoad());
		setEngineRuntime(obsvOBDDetection.getEngineRuntime());
		setBarometricPressure(obsvOBDDetection.getBarometricPressure());
		setFuelConsumption(obsvOBDDetection.getFuelConsumption());
		setFuelPressure(obsvOBDDetection.getFuelPressure());
		setMassAirFlow(obsvOBDDetection.getMassAirFlow());
		setIntakeManifoldPressure(obsvOBDDetection.getIntakeManifoldPressure());
		
//		setCreated(obsvOBDDetection.getCreated());
		setUpdated(obsvOBDDetection.getUpdated());
		setVersion(obsvOBDDetection.getVersion());
	}
	
	public static List<ObsvOBDDetectionVO> copyCollection(List<ObsvOBDDetection> obsvOBDDetectionList) {
		List<ObsvOBDDetectionVO> obsvOBDDetectionVOList = new LinkedList<ObsvOBDDetectionVO>();
		
		for (ObsvOBDDetection obsvOBDDetection : obsvOBDDetectionList) {
			ObsvOBDDetectionVO obsvOBDDetectionVO = new ObsvOBDDetectionVO();
			obsvOBDDetectionVO.clone(obsvOBDDetection);
			obsvOBDDetectionVOList.add(obsvOBDDetectionVO);
		}
		
		return obsvOBDDetectionVOList;
		
	}
}
