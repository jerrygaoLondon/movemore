package uk.ac.oak.movemore.webapp.service.testObj;

/**
 * Created by Fabio Ciravegna, The University of Sheffield on 05/09/2014.
 * f.ciravegna@shef.ac.uk
 * Distributed to the WeSenseIt consortium under the project agreement
 *
 * Service class to represent the activity and its confidence (to send to the server)
 *
 */
public class ActivityAndConfidence {
    String activity;
    int confidence;

    public ActivityAndConfidence(String activity, int confidence) {
        this.activity = activity;
        this.confidence = confidence;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }
}
