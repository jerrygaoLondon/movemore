package uk.ac.oak.movemore.webapp.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
/**
 * Example of Sensor Observation REST APIs Test
 * 
 * Pre-requisite: The test code depends on two main libraries including
 * "Apache HttpClient 4.3.4 API" and "JSON in Java"
 * 
 * You may download "Apache HttpClient 4.3.4 API" library from
 * http://hc.apache.org/downloads.cgi "JSON in Java" library from
 * http://www.json.org/java/
 * 
 * Maven configurations for these libraries:
 * 
 * <dependency> <groupId>org.apache.httpcomponents</groupId>
 * <artifactId>httpclient</artifactId> <version>4.3.4</version> </dependency>
 * 
 * <dependency> <groupId>org.json</groupId> <artifactId>json</artifactId>
 * <version>20140107</version> </dependency>
 * 
 * @author jieg
 * 
 */
public class TestSensorObservationRESTServiceEx {

	/**
	 * This is the example client code of sending Bluetooth scanner sensor
	 * observations to the RESTful API
	 * 
	 * @param args
	 *            include {sensorId}, {macAddresses}, {time}
	 * 
	 *            Note: The sensor id is the system id which will get at the
	 *            point of sensor registration.
	 */
	public static void main(String[] args) {
		HttpClient client = HttpClientBuilder.create().build();
		URI restURL;
		try {
			// remote service URI is
			restURL = new URI(
					"https://wesenseit-vm1.shef.ac.uk:8443/moveMore/services/json/sensorObservation");
			
			// HttpPost postRequest = constructActivitySensorParameter(restURL);
			HttpPost postRequest = constructActivitySensorGZIPRequest(restURL);
			
			// submit post request and return the response synchronously
			HttpResponse resp = client.execute(postRequest);
			if (resp.getStatusLine().getStatusCode() == 200) {
				System.out
						.println("sensor reading has been sumibitted successfully.");
				HttpEntity entity = resp.getEntity();
				String respbody = EntityUtils.toString(entity);
				System.out.println(respbody);
			} else if (resp.getStatusLine().getStatusCode() == 302) {
				System.out
						.println("Serivce for the REST URL is not available.");
			} else if (resp.getStatusLine().getStatusCode() == 400) {
				System.out
						.println("sensor reading submit failed due to bad request parameters!");
				HttpEntity entity = resp.getEntity();
				String respbody = EntityUtils.toString(entity);
				System.out.println(respbody);
			} else if (resp.getStatusLine().getStatusCode() == Status.ACCEPTED
					.getStatusCode()) {
				System.out.println("sensor reading submit accepted!");

				HttpEntity entity = resp.getEntity();
				String respbody = EntityUtils.toString(entity);
				System.out.println(respbody);

			} else {
				System.out.println("sensor reading submit failed!");

				HttpEntity entity = resp.getEntity();
				String respbody = EntityUtils.toString(entity);
				System.out.println(respbody);
			}
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException cpex) {
			cpex.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private static HttpPost constructActivitySensorGZIPRequest(URI restURL) {
		//String obsv_value="{\"data\":[{\"stepsDoneToday\":187,\"activityDescription\":{\"type\":3,\"duration\":43141,\"time\":\"2015-07-21T12:00:00+00:00\",\"confidence\":0},\"floorClimbed\":0,\"activityLocation\":{\"accuracy\":31.5,\"longitude\":-1.4804662466049194,\"latitude\":-1.4804662466049194}},{\"stepsDoneToday\":188,\"activityDescription\":{\"type\":5,\"duration\":39838,\"time\":\"2015-07-21T12:00:00+00:00\",\"confidence\":0},\"floorClimbed\":0,\"activityLocation\":{\"accuracy\":31.5,\"longitude\":0,\"latitude\":0}}],\"sensorId\":\"8944200012885048334-MoveMore\"}";
		String obsv_value1="{\"stepsDoneToday\":187,\"activityDescription\":{\"type\":3,\"duration\":43141,\"time\":\"2015-07-21T12:00:00+00:00\",\"confidence\":0},\"floorClimbed\":0,\"activityLocation\":{\"accuracy\":31.5,\"longitude\":-1.4804662466049194,\"latitude\":-1.4804662466049194}}";
		String obsv_value2="{\"stepsDoneToday\":188,\"activityDescription\":{\"type\":5,\"duration\":39838,\"time\":\"2015-07-21T12:00:00+00:00\",\"confidence\":0},\"floorClimbed\":0,\"activityLocation\":{\"accuracy\":31.5,\"longitude\":-1.4804662466049194,\"latitude\":-1.4804662466049194}}";
		
		JSONObject sensorData = new JSONObject();
		JSONArray observations = new JSONArray();
		observations.put(new JSONObject(obsv_value1));
		observations.put(new JSONObject(obsv_value2));
		
		sensorData.put("data", observations);
		sensorData.put("sensorId", "356194054489613-ActivitySensor");
		
		
		System.out.println(sensorData.toString());
		//System.out.println(sensorData.toString().replaceAll("\\\\", ""));
		
		HttpPost postRequest = new HttpPost(restURL.toString());
		
//		JSONObject jsonObj = new JSONObject(value);
		
		HttpEntity entity = EntityBuilder.create().setText(sensorData.toString()).gzipCompress()
				.build();
//		HttpEntity entity = EntityBuilder.create().setBinary(value.getBytes()).gzipCompress()
//				.build();
		postRequest.setEntity(entity);
		return postRequest;
	}

	private static HttpPost constructActivitySensorParameter(URI restURL)
			throws UnsupportedEncodingException {
		String sensorPhysicalId = "356194054489613-Activity";

		String obsvTime = "2014-07-14 17:40:24.903";
		// String value =
		// "{\"activity\":\"In Vehicle\",\"finalActivity\":\"In Vehicle\",\"time\":1405691513180,\"longitude\":-1.2802644,\"latitude\":53.3078235,\"id\":0,\"locAccuracy\":14.0,\"bearing\":142.0,\"sendToServer\":true,\"speed\":17.278961,\"confidence\":100}";
		String value = "{\"movedSignificantly\":false,\"okToSendToServer\":true,\"storedInDb\":true,\"activity\":\"On Foot\",\"location\":{\"mResults\":[0.0,0.0],\"mProvider\":\"fused\",\"mDistance\":0.0,\"mElapsedRealtimeNanos\":183419398249281,\"mTime\":1406640650112,\"mAltitude\":159.0,\"mLongitude\":-1.4803821,\"mLon2\":0.0,\"mLon1\":0.0,\"mLatitude\":53.3811076,\"mLat2\":0.0,\"mLat1\":0.0,\"mIsFromMockProvider\":false,\"mInitialBearing\":0.0,\"mHasSpeed\":true,\"mHasBearing\":true,\"mHasAltitude\":true,\"mHasAccuracy\":true,\"mAccuracy\":10.0,\"mSpeed\":1.25,\"mBearing\":209.0},\"finalActivity\":\"On Foot\",\"id\":0,\"time\":1406640651484,\"sentToServer\":false,\"confidence\":92,\"validActivity\":true,\"validLocation\":true}";
		// create http post request
		HttpPost postRequest = new HttpPost(restURL);

		// assemble nameValuePair for posting
		List<NameValuePair> _OBDSensorObsv = new ArrayList<NameValuePair>();

		_OBDSensorObsv
				.add(new BasicNameValuePair("sensorId", sensorPhysicalId));
		_OBDSensorObsv.add(new BasicNameValuePair("values", value));
		_OBDSensorObsv.add(new BasicNameValuePair("time", obsvTime));

		postRequest.setEntity(new UrlEncodedFormEntity(_OBDSensorObsv));
		return postRequest;
	}
}
