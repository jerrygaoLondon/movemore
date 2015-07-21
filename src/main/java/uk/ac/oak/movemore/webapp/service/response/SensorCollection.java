package uk.ac.oak.movemore.webapp.service.response;

import java.io.Serializable;
import java.util.List;

public class SensorCollection extends SensorObservationSuccess implements Serializable{

	private static final long serialVersionUID = 1070112223219864304L;
	
	private List<SensorDetail> sensors;
    
	public List<SensorDetail> getSensorDetails(){
            return sensors;
        }
	
        public void setSensorDetails(List<SensorDetail> sensordetails){
            sensors= sensordetails;
        }
        
}
