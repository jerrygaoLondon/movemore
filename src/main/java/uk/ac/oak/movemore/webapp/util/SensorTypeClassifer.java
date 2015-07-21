package uk.ac.oak.movemore.webapp.util;

public class SensorTypeClassifer {

	public static SensorTypeEnum guessSensorType (String sensorPhysicalId, String sensorName) {
		if (sensorPhysicalId.contains("OBD")) {
			return SensorTypeEnum.SENSOR_TYPE_OBD_CAR_SPEED_SENSOR;
		} else if (sensorPhysicalId.contains("face")){
			return SensorTypeEnum.SENSOR_TYPE_PEOPLE_COUNTER;
		} else if (sensorPhysicalId.contains("Activ")) {
			return SensorTypeEnum.SENSOR_TYPE_ACTIVITY_SENSOR;
		} else if (sensorPhysicalId.contains("wifi")) {
			return SensorTypeEnum.SENSOR_TYPE_WIFI_SENSOR;
		} else if (MACAddress.isMacAddress(sensorPhysicalId)) {
			return SensorTypeEnum.SENSOR_TYPE_BLUETOOTH_SENSOR;
		} else if (sensorPhysicalId.contains("Mic")) {
			return SensorTypeEnum.SENSOR_TYPE_VOICE_SENSOR;
		} else if (sensorName.contains("Blue")) {
			return SensorTypeEnum.SENSOR_TYPE_BLUETOOTH_SENSOR;
		}
		
		return SensorTypeEnum.SENSOR_TYPE_UNCLASSIFIED;
	}
	
	public static void main(String[] args) {
		SensorTypeEnum predictedSensorType1 = SensorTypeClassifer.guessSensorType("356194054518031-OBD", "");
		System.out.println("predictedSensorType1: "+predictedSensorType1.getName());
		
		SensorTypeEnum predictedSensorType2 = SensorTypeClassifer.guessSensorType("numfaces", "");
		System.out.println("predictedSensorType2: "+predictedSensorType2.getName());
		
		SensorTypeEnum predictedSensorType3 = SensorTypeClassifer.guessSensorType("359276053846666-Activity", "");
		System.out.println("predictedSensorType3: "+predictedSensorType3.getName());
		
		SensorTypeEnum predictedSensorType4 = SensorTypeClassifer.guessSensorType("357841038401812-wifiCounter", "");
		System.out.println("predictedSensorType4: "+predictedSensorType4.getName());
	}
}
