package uk.ac.oak.movemore.webapp.service;

class SimulatedMobileUser implements Runnable{
	@Override
	public void run() {
		for (int i=0 ;i<1;i++){
			TestSensorObservationRESTServiceEx.testActivitySensorGZippedDataSubmit();
		}
		
	}
}

public class SensorObservationRESTServiceStressTest {

	
	public static void main(String[] args) {		
		for (int i = 0; i < 100; i++) {
			Thread client = new Thread(new SimulatedMobileUser());
			client.start();
			
		}
		
	}
}
