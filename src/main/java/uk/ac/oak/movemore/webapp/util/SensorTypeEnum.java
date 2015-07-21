package uk.ac.oak.movemore.webapp.util;

public enum SensorTypeEnum {
	SENSOR_TYPE_UNCLASSIFIED (0, "Unclassified"),
	SENSOR_TYPE_BLUETOOTH_SENSOR (1, "Bluetooth Device Sensor - Bluetooth Scanner"),
	SENSOR_TYPE_WIFI_SENSOR (2, "WIFI Device Sensor - Wifi counter"),
	SENSOR_TYPE_PEOPLE_COUNTER (3, "People Counter - Number of Faces"),
	SENSOR_TYPE_CAR_REG_PLATE_SENSOR (4, "Car Registration Plates Detector"),
	SENSOR_TYPE_OBD_CAR_SPEED_SENSOR (5, "OBD car speed detector"),
	SENSOR_TYPE_VOICE_SENSOR (6, "Voice Sensor"),
	SENSOR_TYPE_ACTIVITY_SENSOR(7, "Activity Sensor");
	
	private Integer sensorType;
	private String name;
	
	SensorTypeEnum(Integer sensorType, String name) {
		this.sensorType = sensorType;
		this.name = name;
	}
	
	public static SensorTypeEnum forEntity (Integer sensorType) {
		for (SensorTypeEnum sensorTypeItem : values()) {
			if (sensorTypeItem.getSensorType().equals(sensorType)) {
				return sensorTypeItem;
			}
		}
		return null;
	}
	
	public static String getNameForSensorTypeEntity (Integer sensorType) {
		for (SensorTypeEnum sensorTypeItem : values()) {
			if (sensorTypeItem.getSensorType().equals(sensorType)) {
				return sensorTypeItem.getName();
			}
		}
		return null;
	}

	public Integer getSensorType() {
		return sensorType;
	}

	public void setSensorType(Integer sensorType) {
		this.sensorType = sensorType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
