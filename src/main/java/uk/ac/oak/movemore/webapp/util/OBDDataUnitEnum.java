package uk.ac.oak.movemore.webapp.util;

public enum OBDDataUnitEnum {
	OBD_DATA_AMBIENT_AIR_TEMPERATURE ("Ambient Air Temperature", "C"),
	OBD_DATA_VEHICLE_SPEED ("Vehicle Speed", "mph"),
	OBD_DATA_FUEL_ECONOMY ("Fuel Economy", "l/100km"),
	OBD_DATA_ENGINE_RPM ("Engine RPM", "RPM"),
	OBD_DATA_FUEL_LEVEL ("Fuel Level", "%"),
	OBD_DATA_ENGINE_COOLANT_TEMPERATURE ("Engine Coolant Temperature", "C"),
	OBD_DATA_ENGINE_LOAD ("Engine Load", "%"),
	OBD_DATA_Engine_RUNTIME ("Engine Runtime", ""),
	OBD_DATA_BAROMETRIC_PRESSURE ("Barometric Pressure", "kPa"),
	OBD_DATA_FUEL_CONSUMPTION ("Fuel Consumption", ""),
	OBD_DATA_FUEL_PRESSURE("Fuel Pressure", "kPa"),
	OBD_DATA_MASS_AIR_FLOW ("Mass Air Flow", "g/s"),
	OBD_DATA_INTAKE_MANIFOLD_PRESSURE ("Intake Manifold Pressure", "kPa");
	
	private String type;
	private String unit;
	
	OBDDataUnitEnum (String dataType, String dataUnit) {
		this.type = dataType;
		this.unit = dataUnit;
	}

	public static OBDDataUnitEnum forDataType (String dataType) {
		for (OBDDataUnitEnum _OBDDataUnitEnum : values()) {
			if (_OBDDataUnitEnum.getType().equals(dataType)) {
				return _OBDDataUnitEnum;
			}
		}
		return null;
	}
	
	public static String getUnitForDataType (String dataType) {
		for (OBDDataUnitEnum _OBDDataUnitEnum : values()) {
			if (_OBDDataUnitEnum.getType().equals(dataType)) {
				return _OBDDataUnitEnum.getUnit();
			}
		}
		return null;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
