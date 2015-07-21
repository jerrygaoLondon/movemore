package uk.ac.oak.movemore.webapp.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import uk.ac.oak.movemore.webapp.service.testObj.PersonalActivity;
import uk.ac.oak.movemore.webapp.service.testObj.SimplifiedLocationStructure;
import uk.ac.oak.movemore.webapp.util.DateUtil;

/**
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
			// http://wesenseit-vm1.shef.ac.uk:8080/movemore-1.0-SNAPSHOT/services/json/sensorObservation
/*			restURL = new URI(
					"http://wesenseit-vm1.shef.ac.uk:8080/movemore-1.0-SNAPSHOT/services/json/sensorObservation");*/
			restURL = new URI(
					"http://localhost:8080/CityActivityServer-0.0.1-SNAPSHOT/got");
			// restURL = new
			// URI("http://wesenseit-vm1.shef.ac.uk:8080/movemore-1.0-SNAPSHOT/services/json/sensorObservation");

			// HttpPost postRequest =
			// constructBluetoothSensorParameter(restURL);
			// HttpPost postRequest = constructWifiSensorParameter(restURL);
//			HttpPost postRequest = constructOBDSensorParameter(restURL);
			// HttpPost postRequest = constructVoiceSensorParameter(restURL);
			// HttpPost postRequest = constructActivitySensorParameter(restURL);
			// HttpPost postRequest =
			// constructGPSSensorDataForBulkUploading(restURL);
			// HttpPost postRequest =
			// constructVoiceSensorParameterWithAudioFile(restURL);
			// HttpPost postRequest =
			// constructActivityNormalisedDataForUpdating(restURL);
//			HttpPost postRequest = constructActivitySensorGZIPRequest(restURL);
//			HttpPost postRequest = constructOBDSensorGZIPRequest(restURL);
			HttpPost postRequest = constructPersonActivityGZIPRequest(restURL);
			
			// submit post request and return the response synchronously
			HttpResponse resp = client.execute(postRequest);
			if (resp.getStatusLine().getStatusCode() == 200) {
				// HttpEntity entity = resp.getEntity();
				// String respbody = EntityUtils.toString(entity);
				// JSONObject jsonobj = new JSONObject(respbody);
				// Integer isSuccess = (Integer) jsonobj.get("isSuccess");
				// if (isSuccess < 0) {
				// // failure response, do sth...
				// System.out.print(jsonobj.get("message"));
				// System.out.print(jsonobj.get("reason"));
				// } else {
				// System.out
				// .print("sensor reading has been sumibitted successfully.");
				// }
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

	private static HttpPost constructVoiceSensorParameterWithAudioFile(
			URI restURL) throws UnsupportedEncodingException {
		String sensorPhysicalId = "Mic";

		String obsvTime = new Timestamp(new Date().getTime()).toString();
		String value = "{\"unit\":\"dB\",\"soundlevel\":63.0}";

		// create http post request
		HttpPost postRequest = new HttpPost(restURL);

		// assemble nameValuePair for posting
		// List<NameValuePair> _OBDSensorObsv = new ArrayList<NameValuePair>();

		// _OBDSensorObsv.add(new BasicNameValuePair("sensorId",
		// sensorPhysicalId));
		// _OBDSensorObsv.add(new BasicNameValuePair("values", value));
		// _OBDSensorObsv.add(new BasicNameValuePair("time", obsvTime));

		HttpEntity reqEntity;
		File file = new File(
				"C:\\Users\\Public\\Music\\Sample Music\\Kalimba.mp3");

		InputStream fileStream;
		try {
			fileStream = new FileInputStream(file);

			reqEntity = MultipartEntityBuilder.create()
					.addTextBody("sensorId", sensorPhysicalId)
					.addTextBody("values", value).addTextBody("time", obsvTime)
					.addBinaryBody("mediaFile", fileStream)
					.addTextBody("fileName", file.getName()).build();

			postRequest.setEntity(reqEntity);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return postRequest;
	}

	private static HttpPost constructGPSSensorDataForBulkUploading(URI restURI) {
		// JGPSTrack-Activity-04092014
		// TrainingCenterDatabase-tcx-03092014
		String sensorPhysicalId = "JGPSTrack-Activity-04092014";
		// create http post request
		HttpPost postRequest = new HttpPost(restURI.toString()
				+ "/bulkUploadGPSData");
		HttpEntity reqEntity;

		File trainingActivityTestFile = new File(Paths.get("").toAbsolutePath()
				.toString()
				+ "/src/test/resources/20130117-174731-Ride.gpx");

		InputStream fileStream;
		try {
			fileStream = new FileInputStream(trainingActivityTestFile);

			reqEntity = MultipartEntityBuilder
					.create()
					.addTextBody("sensorId", sensorPhysicalId)
					.addBinaryBody("trackingDataFile", fileStream)
					.addTextBody("fileName", trainingActivityTestFile.getName())
					.build();

			postRequest.setEntity(reqEntity);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return postRequest;
	}

	private static HttpPost constructActivityNormalisedDataForUpdating(
			URI restURI) throws UnsupportedEncodingException {
		String obsvId = "5082198";

		HttpPost postRequest = new HttpPost(restURI.toString() + "/normalise/"
				+ obsvId);

		// assemble nameValuePair for posting
		List<NameValuePair> normalisedObs = new ArrayList<NameValuePair>();

		String finalActivityType = "Testing";
		String finalLatitude = "53.3712";
		String finalLongitude = "-1.4935";
		String finalConfidence = "98";

		// normalisedObs
		// .add(new BasicNameValuePair("finalActivityType", finalActivityType));
		normalisedObs
				.add(new BasicNameValuePair("finalLatitude", finalLatitude));
		normalisedObs.add(new BasicNameValuePair("finalLongitude",
				finalLongitude));
		normalisedObs.add(new BasicNameValuePair("finalConfidence",
				finalConfidence));

		postRequest.setEntity(new UrlEncodedFormEntity(normalisedObs));

		return postRequest;
	}

	private static HttpPost constructWifiSensorParameter(URI restURL)
			throws UnsupportedEncodingException {
		String sensorPhysicalId = "b8:27:eb:a5:99:69-wifiCounter";
		// String detectedMacAddress1 =
		// "L4Cr3f+CTzqtiB7P5LbhPylJvuPopgLAXda1TuSzH4g=,-720";
		// String detectedMacAddress2 =
		// "L4Cr3f+1TzqtiB7P5LbhPylJvuPopgLAXda1TuSzH4g=,-200";
		// String detectedMacAddress3 =
		// "L4Cr3f+NTzqtiB7P5LbhPylJvuPopgLAXda1TuSzH4g=,-180";
		// String detectedMacAddress4 =
		// "L4Cr3f+nTzqtiB7P5LbhPylJvuPopgLAXda1TuSzB4g=,-400";
		// String detectedMacAddress5 =
		// "L4Cr3f+4TzqtiB7P5LbHPylJvuPopgLAXda1TuSzH4g=,-180";
		// String detectedMacAddress6 =
		// "L4Cr3f+2TzqtiB7P5LbhMylJvuPopgLAXda1TuSzH4g=,-990";
		String detectedMacAddress1 = "{\"id\":\"jQszvKOHDkvLWIc3weRVOKIi0+YG+Hletlxopqy75/k=\", \"obs\":[{\"power\": -78, \"firstTime\":1406133779048, \"lastTime\": 1406133856498}, {\"power\": -51, \"firstTime\":1406135346060, \"lastTime\": 1406133856498}]}";
		String obsvTime = new Timestamp(new Date().getTime()).toString();

		// create http post request
		HttpPost postRequest = new HttpPost(restURL);

		// assemble nameValuePair for posting
		List<NameValuePair> peopleCountObsv = new ArrayList<NameValuePair>();

		peopleCountObsv
				.add(new BasicNameValuePair("sensorId", sensorPhysicalId));

		List<NameValuePair> detectedPhones = new ArrayList<NameValuePair>();
		detectedPhones
				.add(new BasicNameValuePair("values", detectedMacAddress1));
		// detectedPhones
		// .add(new BasicNameValuePair("values", detectedMacAddress2));
		// detectedPhones
		// .add(new BasicNameValuePair("values", detectedMacAddress3));
		// detectedPhones
		// .add(new BasicNameValuePair("values", detectedMacAddress4));
		// detectedPhones
		// .add(new BasicNameValuePair("values", detectedMacAddress5));
		// detectedPhones
		// .add(new BasicNameValuePair("values", detectedMacAddress6));

		peopleCountObsv.addAll(detectedPhones);
		peopleCountObsv.add(new BasicNameValuePair("time", obsvTime));

		postRequest.setEntity(new UrlEncodedFormEntity(peopleCountObsv));
		return postRequest;
	}

	private static HttpPost constructBluetoothSensorParameter(URI restURL)
			throws UnsupportedEncodingException {
		String sensorPhysicalId = "356194054489613";
		// String detectedMacAddress1 =
		// "L4Cr3f+CTzqtiB7P5LbhPylJvuPopgLAXda1TuSzH4g=,-720";
		// String detectedMacAddress2 =
		// "L4Cr3f+1TzqtiB7P5LbhPylJvuPopgLAXda1TuSzH4g=,-200";
		// String detectedMacAddress3 =
		// "L4Cr3f+NTzqtiB7P5LbhPylJvuPopgLAXda1TuSzH4g=,-180";
		// String detectedMacAddress4 =
		// "L4Cr3f+nTzqtiB7P5LbhPylJvuPopgLAXda1TuSzB4g=,-400";
		// String detectedMacAddress5 =
		// "L4Cr3f+4TzqtiB7P5LbHPylJvuPopgLAXda1TuSzH4g=,-180";
		// String detectedMacAddress6 =
		// "L4Cr3f+2TzqtiB7P5LbhMylJvuPopgLAXda1TuSzH4g=,-990";
		String detectedMacAddress1 = "{\"id\":\"daqn2vV25OkmFnPE9rQNBFKqLDbiBegOvV4jQwyiRxo=\", \"firstTime\":1406133779048, \"lastTime\": 1406133856498}";
		String obsvTime = new Timestamp(new Date().getTime()).toString();

		// create http post request
		HttpPost postRequest = new HttpPost(restURL);

		// assemble nameValuePair for posting
		List<NameValuePair> peopleCountObsv = new ArrayList<NameValuePair>();

		peopleCountObsv
				.add(new BasicNameValuePair("sensorId", sensorPhysicalId));

		List<NameValuePair> detectedPhones = new ArrayList<NameValuePair>();
		detectedPhones
				.add(new BasicNameValuePair("values", detectedMacAddress1));
		// detectedPhones
		// .add(new BasicNameValuePair("values", detectedMacAddress2));
		// detectedPhones
		// .add(new BasicNameValuePair("values", detectedMacAddress3));
		// detectedPhones
		// .add(new BasicNameValuePair("values", detectedMacAddress4));
		// detectedPhones
		// .add(new BasicNameValuePair("values", detectedMacAddress5));
		// detectedPhones
		// .add(new BasicNameValuePair("values", detectedMacAddress6));

		peopleCountObsv.addAll(detectedPhones);
		peopleCountObsv.add(new BasicNameValuePair("time", obsvTime));

		postRequest.setEntity(new UrlEncodedFormEntity(peopleCountObsv));
		return postRequest;
	}

	private static HttpPost constructOBDSensorParameter(URI restURL)
			throws UnsupportedEncodingException {
		String sensorPhysicalId = "356194054489613-OBD";

		String obsvTime = DateUtil.getCurrentUTCTime();

		// String value =
		// "[{\"name\":\"Fuel Economy\",\"value\":\"-1.0 l/100km\"},{\"name\":\"Engine RPM\",\"value\":\"899 RPM\"},{\"name\":\"Fuel Level\",\"value\":\"0.0%\"},{\"name\":\"Ambient Air Temperature\",\"value\":\"25C\"},{\"name\":\"Vehicle Speed\",\"value\":\"4km/h\"},{\"name\":\"Fuel Economy\",\"value\":\"-1.0 l/100km\"},{\"name\":\"Engine RPM\",\"value\":\"901 RPM\"},{\"name\":\"Fuel Level\",\"value\":\"0.0%\"},{\"name\":\"Ambient Air Temperature\",\"value\":\"25C\"},{\"name\":\"Vehicle Speed\",\"value\":\"0km/h\"},{\"name\":\"Fuel Economy\",\"value\":\"-1.0 l/100km\"},{\"name\":\"Engine RPM\",\"value\":\"899 RPM\"},{\"name\":\"Fuel Level\",\"value\":\"0.0%\"},{\"name\":\"Ambient Air Temperature\",\"value\":\"25C\"},{\"name\":\"Vehicle Speed\",\"value\":\"0km/h\"},{\"name\":\"Fuel Economy\",\"value\":\"-1.0 l/100km\"},{\"name\":\"Engine RPM\",\"value\":\"898 RPM\"}]";
		 String value =
		 "[{\"key\":\"Engine RPM\",\"value\":\"1452 RPM\",\"time\":\"2014-07-29 10:46:08.228\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Engine Load\",\"value\":\"77.3%\",\"time\":\"2014-07-29 10:46:08.439\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Engine Runtime\",\"value\":\"00:00:00\",\"time\":\"2014-07-29 10:46:09.035\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Ambient Air Temperature\",\"value\":\"0C\",\"time\":\"2014-07-29 10:46:09.308\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Barometric Pressure\",\"value\":\"0kPa\",\"time\":\"2014-07-29 10:46:09.599\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Engine Coolant Temperature\",\"value\":\"86C\",\"time\":\"2014-07-29 10:46:09.804\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Fuel Level\",\"value\":\"0.0%\",\"time\":\"2014-07-29 10:46:10.387\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Fuel Economy\",\"value\":\"-1.0 l/100km\",\"time\":\"2014-07-29 10:46:10.671\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Fuel Consumption\",\"value\":\"-1.0\",\"time\":\"2014-07-29 10:46:10.959\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Fuel Pressure\",\"value\":\"0kPa\",\"time\":\"2014-07-29 10:46:11.25\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Mass Air Flow\",\"value\":\"27.94g/s\",\"time\":\"2014-07-29 10:46:11.457\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Intake Manifold Pressure\",\"value\":\"125kPa\",\"time\":\"2014-07-29 10:46:11.662\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Vehicle Speed\",\"value\":\"44km/h\",\"time\":\"2014-07-29 10:46:13.877\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Engine RPM\",\"value\":\"1754 RPM\",\"time\":\"2014-07-29 10:46:14.083\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Engine Load\",\"value\":\"81.6%\",\"time\":\"2014-07-29 10:46:14.288\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Engine Runtime\",\"value\":\"00:00:00\",\"time\":\"2014-07-29 10:46:14.881\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Ambient Air Temperature\",\"value\":\"0C\",\"time\":\"2014-07-29 10:46:15.172\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Barometric Pressure\",\"value\":\"0kPa\",\"time\":\"2014-07-29 10:46:15.464\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Engine Coolant Temperature\",\"value\":\"86C\",\"time\":\"2014-07-29 10:46:15.677\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Fuel Level\",\"value\":\"0.0%\",\"time\":\"2014-07-29 10:46:16.25\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Fuel Economy\",\"value\":\"-1.0 l/100km\",\"time\":\"2014-07-29 10:46:16.541\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Fuel Consumption\",\"value\":\"-1.0\",\"time\":\"2014-07-29 10:46:16.832\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Fuel Pressure\",\"value\":\"0kPa\",\"time\":\"2014-07-29 10:46:17.143\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Mass Air Flow\",\"value\":\"29.91g/s\",\"time\":\"2014-07-29 10:46:17.349\",\"lon\":-3.6837126,\"lat\":50.433478},{\"key\":\"Intake Manifold Pressure\",\"value\":\"124kPa\",\"time\":\"2014-07-29 10:46:17.553\",\"lon\":-3.6837126,\"lat\":50.433478}]";
		// create http post request
		HttpPost postRequest = new HttpPost(restURL.toString());
		// +"/gzip"
		// assemble nameValuePair for posting
		List<NameValuePair> _OBDSensorObsv = new ArrayList<NameValuePair>();

		_OBDSensorObsv
				.add(new BasicNameValuePair("sensorId", sensorPhysicalId));
		_OBDSensorObsv.add(new BasicNameValuePair("values", value));
		_OBDSensorObsv.add(new BasicNameValuePair("time", obsvTime));

		 postRequest.setEntity(new UrlEncodedFormEntity(_OBDSensorObsv));
		 
		return postRequest;
	}

	private static HttpPost constructActivitySensorGZIPRequest(URI restURL) {
		//String value = "{\"data\": [{\"activity\":\"On Foot\",\"otherActivities\":[{\"activity\":\"\",\"confidence\":100}],\"finalActivity\":\"On Foot\",\"location\":{\"mResults\":[0.0,0.0],\"mProvider\":\"fused\",\"mDistance\":0.0,\"mElapsedRealtimeNanos\":72722055724946,\"mTime\":1410801422055,\"mAltitude\":0.0,\"mLongitude\":-1.4897624,\"mLon2\":0.0,\"mLon1\":0.0,\"mLatitude\":53.3710584,\"mLat2\":0.0,\"mLat1\":0.0,\"mIsFromMockProvider\":false,\"mInitialBearing\":0.0,\"mHasSpeed\":false,\"mHasBearing\":false,\"mHasAltitude\":false,\"mHasAccuracy\":true,\"mAccuracy\":37.5,\"mSpeed\":0.0,\"mBearing\":0.0},\"time\":1410801503228,\"locationFixAvailable\":false,\"confidence\":100,\"sentToServer\":false,\"id\":0,\"validActivity\":true,\"validLocation\":true}, {\"activity\":\"On Foot\",\"otherActivities\":[{\"activity\":\"\",\"confidence\":100}],\"finalActivity\":\"On Foot\",\"location\":{\"mResults\":[0.0,0.0],\"mProvider\":\"fused\",\"mDistance\":0.0,\"mElapsedRealtimeNanos\":72722055724946,\"mTime\":1410801422055,\"mAltitude\":0.0,\"mLongitude\":-1.4897624,\"mLon2\":0.0,\"mLon1\":0.0,\"mLatitude\":53.3710584,\"mLat2\":0.0,\"mLat1\":0.0,\"mIsFromMockProvider\":false,\"mInitialBearing\":0.0,\"mHasSpeed\":false,\"mHasBearing\":false,\"mHasAltitude\":false,\"mHasAccuracy\":true,\"mAccuracy\":37.5,\"mSpeed\":0.0,\"mBearing\":0.0},\"time\":1410801503228,\"locationFixAvailable\":false,\"confidence\":100,\"sentToServer\":false,\"id\":0,\"validActivity\":true,\"validLocation\":true}], \"sensorId\":\"356194054489613-Activity\"}";
		//String value = "{\"data\":[{\"name\":\"values\",\"value\":\"{\\"activity\\":\\"Unknown\\",\\"finalActivity\\":\\"Unknown\\",\\"location\\":{\\"mResults\\":[0.0,0.0],\\"mProvider\\":\\"database\\",\\"mDistance\\":0.0,\\"mElapsedRealtimeNanos\\":0,\\"mTime\\":0,\\"mAltitude\\":0.0,\\"mLongitude\\":-1.4840399,\\"mLon2\\":0.0,\\"mLon1\\":0.0,\\"mLatitude\\":53.3775902,\\"mLat2\\":0.0,\\"mLat1\\":0.0,\\"mIsFromMockProvider\\":false,\\"mInitialBearing\\":0.0,\\"mHasSpeed\\":true,\\"mHasBearing\\":true,\\"mHasAltitude\\":false,\\"mHasAccuracy\\":true,\\"mAccuracy\\":990.0,\\"mSpeed\\":0.0,\\"mBearing\\":0.0},\\"time\\":1411063481731,\\"locationFixAvailable\\":true,\\"confidence\\":81,\\"sentToServer\\":false,\\"id\\":0,\\"validActivity\\":true,\\"validLocation\\":true}\"},{\"name\":\"sensorId\",\"value\":\"356843053521889-Rep2Activity\"},{\"name\":\"time\",\"value\":\"2014-09-18 19:04:41.731\"}]}";
		String value="[[[";
		
		String obsv_value="{\"activity\":\"On Foot\",\"otherActivities\":[{\"activity\":\"\",\"confidence\":100}],\"finalActivity\":\"On Foot\",\"location\":{\"mResults\":[0.0,0.0],\"mProvider\":\"fused\",\"mDistance\":0.0,\"mElapsedRealtimeNanos\":72722055724946,\"mTime\":1410801422055,\"mAltitude\":0.0,\"mLongitude\":-1.4897624,\"mLon2\":0.0,\"mLon1\":0.0,\"mLatitude\":53.3710584,\"mLat2\":0.0,\"mLat1\":0.0,\"mIsFromMockProvider\":false,\"mInitialBearing\":0.0,\"mHasSpeed\":false,\"mHasBearing\":false,\"mHasAltitude\":false,\"mHasAccuracy\":true,\"mAccuracy\":37.5,\"mSpeed\":0.0,\"mBearing\":0.0},\"time\":1410801503228,\"locationFixAvailable\":false,\"confidence\":100,\"sentToServer\":false,\"id\":0,\"validActivity\":true,\"validLocation\":true}";
		
		JSONObject sensorData = new JSONObject();
		JSONArray observations = new JSONArray();
		observations.put(new JSONObject(obsv_value));
		sensorData.put("data", observations);
		sensorData.put("sensorId", "356194054489613-Activity");
		
		System.out.println(sensorData.toString().replaceAll("\\\\", ""));
		
		HttpPost postRequest = new HttpPost(restURL.toString());
		
//		JSONObject jsonObj = new JSONObject(value);
		
		HttpEntity entity = EntityBuilder.create().setText(sensorData.toString()).gzipCompress()
				.build();
//		HttpEntity entity = EntityBuilder.create().setBinary(value.getBytes()).gzipCompress()
//				.build();
		postRequest.setEntity(entity);
		return postRequest;
	}
	
	private static HttpPost constructOBDSensorGZIPRequest(URI restURL) {
		String value1 = "{\"timestamp\":1411202519298,\"readings\":{\"rpm\":{\"value\":\"1762\",\"unit\":\"RPM\"},\"speed\":{\"value\":\"30\",\"unit\":\"C\"},\"temperature\":{\"value\":\"18.0\",\"unit\":\"18.0\"}},\"longitude\":-1.493503,\"latitude\":53.371209,\"sensorId\":\"356843053521889-OBD\",\"vin\":\"\"}";
		String value2 = "{\"timestamp\":1411202520299,\"readings\":{\"rpm\":{\"value\":\"800\",\"unit\":\"RPM\"},\"speed\":{\"value\":\"29\",\"unit\":\"C\"},\"temperature\":{\"value\":\"18.0\",\"unit\":\"18.0\"}},\"longitude\":-1.4935014,\"latitude\":53.3712089,\"sensorId\":\"356843053521889-OBD\",\"vin\":\"\"}";
		
		JSONObject sensorData = new JSONObject();
		JSONArray observations = new JSONArray();
		observations.put(new JSONObject(value1));
		observations.put(new JSONObject(value2));
		sensorData.put("data", observations);
		sensorData.put("sensorId", "356843053521889-OBD");
		
		HttpPost postRequest = new HttpPost(restURL.toString());
		HttpEntity entity = EntityBuilder.create().setText(sensorData.toString()).gzipCompress()
				.build();
		postRequest.setEntity(entity);
		return postRequest;
	}
	
	private static HttpPost constructPersonActivityGZIPRequest(URI restURL) {
		PersonalActivity pa = new PersonalActivity();
		pa.setActivity("Walking");
		pa.setConfidence(100);
		pa.setTime(new Date().getTime());
		pa.setFinalActivity("Walking");
		SimplifiedLocationStructure ls = new SimplifiedLocationStructure();
		ls.setLatitude(1.999222);
		ls.setLongitude(-0.99922);
		ls.setAccuracy(88);
		ls.setBearing(45);
		ls.setSpeed(56);
		pa.setLocation(ls);
		
		Gson gson = new Gson();
		
		HttpPost postRequest = new HttpPost(restURL.toString());
		HttpEntity entity = EntityBuilder.create().setText(gson.toJson(pa)).gzipCompress()
				.build();
		postRequest.setEntity(entity);
		return postRequest;
	}

	private static HttpPost constructVoiceSensorParameter(URI restURL)
			throws UnsupportedEncodingException {
		String sensorPhysicalId = "Mic";

		String obsvTime = new Timestamp(new Date().getTime()).toString();
		String value = "{\"unit\":\"dB\",\"soundlevel\":63.0}";

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
