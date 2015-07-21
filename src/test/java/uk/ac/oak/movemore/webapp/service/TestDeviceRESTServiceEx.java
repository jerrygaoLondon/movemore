package uk.ac.oak.movemore.webapp.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
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
public class TestDeviceRESTServiceEx {

	
	public static void main(String[] args) {
		//testRegisterOrUpdateDeviceInfo();
		testRegisterNewDevice();
		//testUpdateDeviceLocation();
	}
	
	private static void testRegisterOrUpdateDeviceInfo() {

		String newDevicePhysicalId="b827eb2ed26d";
	//	double latitude = 1;
	//	double longitude = 1;
		String newDeviceName="Face-Detector";		
		String newSensorPhysicalId="numfaces";
		String newSensorName="Number of faces";		
		
		HttpClient client = HttpClientBuilder.create().build();
		URI restURL;
		try {
			//remote service URI is http://wesenseit-vm1.shef.ac.uk:8080/movemore-1.0-SNAPSHOT/services/json/device
			//restURL = new URI("http://wesenseit-vm1.shef.ac.uk:8080/movemore-1.0-SNAPSHOT/services/json/device");
			restURL = new URI("https://wesenseit-vm1.shef.ac.uk:8443/movemore/services/json/device");
			//create http post request
			HttpPost postRequest = new HttpPost(restURL);
			
			//assemble nameValuePair for posting
			List<NameValuePair> peopleCounterValues = new ArrayList<NameValuePair>();
			
			peopleCounterValues.add(new BasicNameValuePair("deviceId", newDevicePhysicalId));
			peopleCounterValues.add(new BasicNameValuePair("deviceName", newDeviceName));
			peopleCounterValues.add(new BasicNameValuePair("sensorId", newSensorPhysicalId));
			peopleCounterValues.add(new BasicNameValuePair("sensorName", newSensorName));
		//	peopleCounterValues.add(new BasicNameValuePair("batteryLevel", "0.80"));
//			peopleCounterValues.add(new BasicNameValuePair("latitude", String.valueOf(latitude)));
//			peopleCounterValues.add(new BasicNameValuePair("longitude", longitude + ""));
			peopleCounterValues.add(new BasicNameValuePair("sensorDesc", "The numfaces sensor is developed by Dr. Stuart to provide real-time monitoring of pedestrian flow. The sensor is implemented using OpenCV and able to count people in a crowd by using face detection from cameras automatically."));
	        postRequest.setEntity(new UrlEncodedFormEntity(peopleCounterValues));
			
	      //submit post request and return the response synchronously
			HttpResponse resp = client.execute(postRequest);
			if(resp.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = resp.getEntity();
				String respbody = EntityUtils.toString(entity);
				JSONObject jsonobj = new JSONObject(respbody);
				Integer isSuccess = (Integer) jsonobj.get("isSuccess");
				if(isSuccess < 0) {
					// failure response, do sth...
					System.out.println(jsonobj.get("message"));
					System.out.println(jsonobj.get("reason"));
				} else {
					System.out.print("Bluetooth scanner sensor reading has been sumibitted successfully.");
				}
			} else if (resp.getStatusLine().getStatusCode() == 302) {
				System.out.print("Serivce for the REST URL is not available.");
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
	
	/**
	 * This is the example client code of registering a new sensor via the RESTful API
	 * http://localhost:8080/services/json/device/{devicePhysicalId}/{latitude}/{longtitude}/{name}
	 * 
	 * Note: The device physical id is the physical id (e.g., Mac Address) sent from device which can be used to identify a device
	 */
	private static void testRegisterNewDevice() {
		HttpClient client = HttpClientBuilder.create().build();
		URI restURL;
		try {
			//http get parameters
			//host name in VM is "wesenseit-vm1.shef.ac.uk:8080/movemore-1.0-SNAPSHOT"
			String hostname = "https://wesenseit-vm1.shef.ac.uk:8443/movemore";
			String devicePhysicalId="00CC02NN038C33BC";
			Float latitude=38.891300f;
			Float longitude=-77.025900f;
			String deviceName = "Device Test Name";
			
			//assemble URI for http "get" request
			restURL = new URIBuilder().setScheme("http")
							        .setHost(hostname)
							        .setPath("/services/json/device"+"/"+devicePhysicalId+"/"+latitude+"/"+longitude+"/"+deviceName)
							        .build();
			
			//create http post request
			HttpGet getRequest = new HttpGet(restURL);
			
			//submit post request and return the response synchronously
			HttpResponse resp = client.execute(getRequest);
			System.out.print("status code:"+resp.getStatusLine().getStatusCode());
			
			/*
			HttpEntity entity = resp.getEntity();
			String respbody = EntityUtils.toString(entity);
			System.out.print("respbody");
			JSONObject jsonobj = new JSONObject(respbody);
			Integer isSuccess = (Integer) jsonobj.get("isSuccess");
			if(isSuccess < 0) {
				// failure response, do sth...
				System.out.println(jsonobj.get("message"));
				System.out.println(jsonobj.get("reason"));
			} else {
				// return device system id 
				String deviceId = jsonobj.getString("deviceId");
				System.out.println(deviceId);
			}*/
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
	
	/**
	 * This is the example client code of update a sensor location via the RESTful API
	 * http://localhost:8080/services/json/device/{deviceId}/{latitude}/{longtitude}
	 * 
	 * Note: The sensor id is the system id which will get at the point of sensor registration.
	 */
	private static void testUpdateDeviceLocation() {
		HttpClient client = HttpClientBuilder.create().build();
		URI restURL;
		try {
			//http get parameters
			//host name in VM is "wesenseit-vm1.shef.ac.uk:8080/movemore-1.0-SNAPSHOT"
			String hostname = "localhost:8080";
			String deviceId="1000001";
			Float latitude=38.891300f;
			Float longitude=-77.025900f;
			
			//assemble URI for http "get" request
			restURL = new URIBuilder().setScheme("http")
							        .setHost(hostname)
							        .setPath("/services/json/device"+"/"+deviceId+"/"+latitude+"/"+longitude)
							        .build();
			
			//create http "get" request
			HttpGet getRequest = new HttpGet(restURL);
			
			//submit post request and return the response synchronously
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
				// return device system id 
				deviceId = jsonobj.getString("deviceId");
				System.out.println(deviceId);
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
}
