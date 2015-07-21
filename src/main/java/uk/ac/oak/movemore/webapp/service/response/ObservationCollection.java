package uk.ac.oak.movemore.webapp.service.response;

import java.io.Serializable;
import java.util.List;

public class ObservationCollection extends SensorObservationSuccess implements Serializable{

	private static final long serialVersionUID = 1963210699268895832L;
	
	private List<ObservationDetail> observations;
    
	public List<ObservationDetail> getObservationDetails(){
            return observations;
        }
	
        public void setObservationDetails(List<ObservationDetail> obsdetails){
            observations= obsdetails;
        }
        
}
