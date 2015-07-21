package uk.ac.oak.movemore.webapp.service.impl;

import java.util.Collection;

import uk.ac.oak.movemore.webapp.service.SensorObservationsProcessor;

public class SensorProcessorFactory {

	public static SensorObservationsProcessor createSensorObsvProcessor () {
		final CompositeSensorObsvProcessor compositeSensorObsvProcessor = new CompositeSensorObsvProcessor();
		
		final Collection<SensorObservationsProcessor> processors = compositeSensorObsvProcessor.getProcessors();
		
		processors.add(new BluetoothSensorObsvProcessor());
		processors.add(new WifiDeviceSensorObsvProcessor());
		processors.add(new PeopleCounterObsvProcessor());
		processors.add(new CarRegPlateSensorObsvProcessor());
		processors.add(new OBDCarSpeedSensorObsvProcessor());
		processors.add(new VoiceSensorObsvProcessor());
		processors.add(new ActivitySensorObsvProcessor());
		processors.add(new UnclassifiedSensorObsvProcessor());
		
		return compositeSensorObsvProcessor;
	}
}
