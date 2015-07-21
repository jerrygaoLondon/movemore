package uk.ac.oak.movemore.webapp.service.testObj;

/**
 * Created by Fabio Ciravegna, The University of Sheffield on 19/11/14.
 * f.ciravegna@shef.ac.uk
 *
 * Distributed to the WeSenseIt consortium under the project agreement
 *
 * A subclass used to send location information to the server
 */
public class SimplifiedLocationStructure {
    float accuracy=0;
    float bearing=0;
    float speed=0;
    double latitude;
    double longitude;

    public SimplifiedLocationStructure(float accuracy, float bearing, float speed, double latitude, double longitude) {
        this.accuracy = accuracy;
        this.bearing = bearing;
        this.speed = speed;
        this.latitude = latitude;
        this.longitude = longitude;
    }



    public SimplifiedLocationStructure() {
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
