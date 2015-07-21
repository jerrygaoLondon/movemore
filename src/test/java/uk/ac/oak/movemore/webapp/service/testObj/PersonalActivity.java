/**
 *
 * Created by Fabio Ciravegna, The University of Sheffield on 23/07/2014.
 * f.ciravegna@shef.ac.uk
 *
 * Distributed to the WeSenseIt consortium under the project agreement
 *
 *
 */
package uk.ac.oak.movemore.webapp.service.testObj;

//import android.location.Location;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * this class is used to communicate with the external world the findings about the activity
 */

public class PersonalActivity  {
    /**
     * the activity id. This is not guaranteed to be identical in time. It is just an identifier to recognise one id from another after passing it to the server
     */
    private int id = 0;
    static int idCounter=0;
    String activity;
    long time;
    int confidence;
    SimplifiedLocationStructure location;
    private String finalActivity;
    boolean validLocation = true;
    boolean validActivity = true;
    boolean sentToServer= false;
    boolean locationFixAvailable =true;
    List<ActivityAndConfidence> otherActivities=null;
    private String externalGsonData="";


    public String getExternalGsonData() {
        return externalGsonData;
    }

    public void setExternalGsonData(String exgs) {
        this.externalGsonData = exgs;
    }



    public PersonalActivity(String activity, long time, int conf) {
        id= idCounter++;
        this.activity = activity;
        this.time = time;
        this.confidence= conf;
        this.finalActivity= activity;
    }

    public PersonalActivity() {
        id= idCounter++;
    }

    public PersonalActivity(PersonalActivity pa) {
        id= idCounter++;
        this.id=pa.getId();
        this.location= pa.location;
        this.activity = pa.getFinalActivity();
        this.time = pa.time;
        this.confidence= pa.confidence;
        this.validActivity= pa.validActivity;
        validLocation= pa.validLocation;
        finalActivity= pa.getFinalActivity();
        sentToServer= pa.sentToServer;
        locationFixAvailable = pa.locationFixAvailable;
        otherActivities= pa.otherActivities;
        externalGsonData= pa.getExternalGsonData();
    }


    public double getLatitude() {
        return (location!=null)?location.getLatitude():0;
    }


    public double getLongitude() {
        return (location!=null)?location.getLongitude():0;
    }


    public String getActivity() {
        return activity;
    }


    public long getTime() {
        return time;
    }


    public void setActivity(String activity) {
        this.activity = activity;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public SimplifiedLocationStructure getLocation() {
        return location;
    }

    public void setLocation(SimplifiedLocationStructure lc) {
        this.location = lc;
    }

    public boolean isValidLocation() {
        return validLocation;
    }

    public void setValidLocation(boolean validLocation) {
        this.validLocation = validLocation;
    }

    public boolean isValidActivity() {
        return validActivity;
    }

    public void setValidActivity(boolean validActivity) {
        this.validActivity = validActivity;
    }

    float getLocationAccuracy(){
        return(location!=null)?location.getAccuracy():0;
    }

    float getBearing(){
        return (location!=null)?location.getBearing():0;
    }

    float getSpeed(){
        return (location!=null)?location.getSpeed():0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFinalActivity() {
        return finalActivity;
    }

    public void setFinalActivity(String finalActivity) {
        this.finalActivity = finalActivity;
    }


    public boolean isSentToServer() {
        return sentToServer;
    }

    public void setSentToServer(boolean sentToServer) {
        this.sentToServer = sentToServer;
    }

    @Override
    public String toString() {
        String ttime = new Timestamp(time).toString();
        return "--->>> Activity ["+ " time=" + ttime + ", coord=" + getLatitude() + ", " + getLongitude()+ ", activity=" + activity + " (guessed= " + activity
                + ") sent already? " + sentToServer + "]";
    }

    public String getFormattedTime() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("E, dd-MM-yy 'at' h:mm a");
        return dateFormatter.format(getTime());

    }


    public void setLocationFixAvailable(boolean locationFixAvailable) {
        this.locationFixAvailable = locationFixAvailable;
    }

    public boolean isLocationFixAvailable() {
        return locationFixAvailable;
    }
}
