package uk.ac.oak.movemore.webapp.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Pre-requisite: The test code depends on two main libraries including "Apache HttpClient 4.3.4 API" and "JSON in Java"
 * 
 * You may download 
 * "Apache HttpClient 4.3.4 API" library from http://hc.apache.org/downloads.cgi
 * "JSON in Java" library from http://www.json.org/java/
 * 
 * Maven configurations for these libraries:
 * 
 *  <dependency>
 *		<groupId>org.apache.httpcomponents</groupId>
 *		<artifactId>httpclient</artifactId>
 *		<version>4.3.4</version>
 *	</dependency>
 *		
 *	<dependency>
 *		<groupId>org.json</groupId>
 *		<artifactId>json</artifactId>
 *		<version>20140107</version>
 *	</dependency>	
 *		
 * @author jieg
 *
 */
public class TestSensorRESTService {

	public static void main(String[] args) {
		//testAddNewSensor();
		//testRemoveSensor();
		testGetSensor();
	}
	
	private static void testGetSensor() {
		HttpClient client = HttpClientBuilder.create().build();
		URI restURL;
		
		try {
			restURL = new URIBuilder().setScheme("https")
			        .setHost("wesenseit-vm1.shef.ac.uk:8443")
			        .setPath("/moveMore/services/json/sensor")
			        .build();
			
			HttpGet getRequest = new HttpGet(restURL);
			//submit http get request and return the response synchronously
			HttpResponse resp = client.execute(getRequest);
			int statusCode = resp.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				System.out.println("Success!Currently registered sensors:");
				HttpEntity entity = resp.getEntity();
				String respbody = EntityUtils.toString(entity);
				JSONObject sensors = new JSONObject(respbody);
				JSONArray sensorArray = sensors.getJSONArray("sensors");
				System.out.println(sensorArray.toString());
			}else{
				System.err.println("Request Failure ! Status code is :"+statusCode);				
			}
				
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This is the example client code of registering a new sensor via the RESTful API
	 * http://localhost:8080/services/json/device/{devicePhysicalId}/{latitude}/{longtitude}/{name}
	 * 
	 * Note: The sensor id is the system id which will get at the point of sensor registration. 
	 */
	private static void testAddNewSensor() {
		HttpClient client = HttpClientBuilder.create().build();
		URI restURL;
		
		//host name in VM is "wesenseit-vm1.shef.ac.uk:8080/movemore-1.0-SNAPSHOT"
		String hostname = "sil-dev.wesenseit.softwaremind.pl/sensor-integration-layer/";
		String deviceId="123123";
		String sensorName="New Test Sensor for device 1000003";
		
		try {
			restURL = new URIBuilder().setScheme("http")
			        .setHost(hostname)
			        .setPath("services/Sensors/rest/sensors"+"/"+deviceId)
			        .build();
			//create a http get request
			HttpGet getRequest = new HttpGet(restURL);
			//submit http get request and return the response synchronously
			HttpResponse resp = client.execute(getRequest);
			
			HttpEntity entity = resp.getEntity();
			String respbody = EntityUtils.toString(entity);
			JSONObject jsonobj = new JSONObject(respbody);
			Integer isSuccess = (Integer) jsonobj.get("isSuccess");
			
			if(isSuccess < 0) {
				// failure response, do sth...
				System.out.println(jsonobj.get("message"));
				System.out.println(jsonobj.get("reason"));
			} else {
				// return sensor id
				String sensorId = jsonobj.getString("sensorId");
				System.out.println("New sensor id is: "+sensorId);
			}
			
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void testRemoveSensor() {
		HttpClient client = HttpClientBuilder.create().build();
		URI restURL;
		
		//host name in VM is "http://wesenseit-vm1.shef.ac.uk:8080/movemore-1.0-SNAPSHOT"
		String hostname = "localhost:8080";
		String sensorId="3000001";
		
		try {
			restURL = new URIBuilder().setScheme("http")
			        .setHost(hostname)
			        .setPath("/services/json/sensor"+"/"+sensorId)
			        .build();
			//create a http delete request
			HttpDelete deleteRequest = new HttpDelete(restURL);
			
			//submit delete request and return the response synchronously
			HttpResponse resp = client.execute(deleteRequest);
			
			HttpEntity entity = resp.getEntity();
			
			String respbody = EntityUtils.toString(entity);
			JSONObject jsonobj = new JSONObject(respbody);
			Integer isSuccess = (Integer) jsonobj.get("isSuccess");
			
			if(isSuccess < 0) {
				// failure response, do sth...
				System.out.println(jsonobj.get("message"));
				System.out.println(jsonobj.get("reason"));
			} else {
				System.out.println("DELETION IS SUCCESSFUL");
			}
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
