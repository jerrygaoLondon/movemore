package uk.ac.oak.movemore.webapp.service;

import java.io.InputStream;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


//import org.apache.cxf.annotations.GZIP;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import uk.ac.oak.movemore.webapp.service.response.JSONResponse;

//@GZIP(threshold = 5)
@WebService
@Path("/sensorObservation")
@Api(value = "/sensorObservation", description = "Operations about sensor observations")
@Produces({ "application/json" })
public interface SensorObservationService {
	@ApiOperation(value = "submit sensor readings", notes = "send sensor readings (value and time) by sensor system id. Please provide time in the format of 'yyyy-MM-dd HH:mm:ss.SSSSSS'")
	@RequestMapping(method = RequestMethod.GET)
	@GET
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Path("/{sensorId}/{value}/{obsvTime}")
	@Produces(MediaType.APPLICATION_JSON)
	JSONResponse pushSensorObservation(
			@ApiParam(value = "System ID of sensor that associates with the observation values", allowableValues = "range[1,30]", required = true) @PathParam("sensorId") String deviceId,
			@ApiParam(value = "Observation value (e.g., number of people or car, mac address detected)", allowableValues = "range[1,255]", required = true) @PathParam("value") String value,
			@ApiParam(value = "Observation time should be provided in the format of 'yyyy-MM-dd HH:mm:ss.SSSSSS'", allowableValues = "range[1,27]", required = true) @PathParam("obsvTime") String obsvTime);

	@ApiOperation(value = "get an observation", notes = "get observations by observation id", response = JSONResponse.class)
	@RequestMapping(method = RequestMethod.GET)
	@GET
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Path("/pull/{obsvId}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONResponse getSensorObservationById(
			@ApiParam(value = "ID of sensor observation that needs to be fetched", allowableValues = "range[1,20]", required = true) @PathParam("obsvId") String obsvId);

	@ApiOperation(value = "get all observations of a sensor", notes = "get observations by sensor (system) id")
	@RequestMapping(method = RequestMethod.GET)
	@GET
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Path("/{sensorId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSensorObservationBySensorId(
			@ApiParam(value = "Physical ID of sensor", allowableValues = "range[1,20]", required = true) @PathParam("sensorId") String sensorId,
			@ApiParam(value = "startDate in ISO 8601 format, for example: 2014-01-01T12:00:00+00:00", allowableValues = "range[1,20]", required = false) @QueryParam("startDate") String startDate,
			@ApiParam(value = "endDate in ISO 8601 format, for example: 2014-01-01T12:00:00+00:00", allowableValues = "range[1,20]", required = false) @QueryParam("endDate") String endDate,
			@ApiParam(value = "order by 'obsv_id' or 'obsv_time'", allowableValues = "range[1,20]", required = false) @QueryParam("orderBy") String orderByName,
			@ApiParam(value = "set 'true' or 'false' to determine asc or desc order", allowableValues = "range[1,20]", required = false) @QueryParam("asc") Boolean isAsc,
			@ApiParam(value = "offset within total result set for first measurement returned", allowableValues = "range[1,20]", required = false, defaultValue = "0") @QueryParam("offset") Integer offset,
			@ApiParam(value = "maximum number of measurements to return", allowableValues = "range[1,20]", required = false, defaultValue = "1000") @QueryParam("limit") Integer limit);

	@ApiOperation(value = "post sensor observations", notes = "post all the observations (one time) by sensor physical id. Sensor Physical id is the URI generated in sensor system and should be pre-registered in the system. The observation time should be in the format of 'yyyy-MM-dd HH:mm:ss.SSSSSS'. Return: 400 - Bad Request due to unacceptable request parameters; 404 - Sensor Not Found; 200 - success submit.")
	@RequestMapping(method = RequestMethod.POST)
	@POST
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Consumes({"application/x-www-form-urlencoded"})
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveObservation(
			@ApiParam(value = "Physical sensor Id that associates with a registered sensor for which the sensor observations to be posted", allowableValues = "range[1,30]", required = true) @FormParam("sensorId") String sensorPhysicalId,
			@ApiParam(value = "Observation time in the format of 'yyyy-MM-dd HH:mm:ss.SSSSSS'", allowableValues = "range[1,27]", required = true) @FormParam("time") String time,
			@ApiParam(value = "An array of text about sensor observations", allowableValues = "range[1,255]", required = true) @FormParam("values") List<String> values);

	@ApiOperation(value = "post sensor observations with attachment", notes = "post all the observations by sensor physical id. Sensor Physical id is the URI generated in sensor system and should be pre-registered in the system. The observation time should be in the format of 'yyyy-MM-dd HH:mm:ss.SSSSSS'")
	@RequestMapping(method = RequestMethod.POST)
	@POST
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Consumes("multipart/form-data")
	@Produces(MediaType.APPLICATION_JSON)
	@WebMethod(operationName = "saveObservationWithAttachment")
	public Response saveObservation(
			@ApiParam(value = "Physical sensor Id that associates with a registered sensor for which the sensor observations to be posted", allowableValues = "range[1,30]", required = true) @Multipart("sensorId") String sensorPhysicalId,
			@ApiParam(value = "Observation time in the format of 'yyyy-MM-dd HH:mm:ss.SSSSSS'", allowableValues = "range[1,27]", required = true) @Multipart("time") String time,
			@ApiParam(value = "An array of text about sensor observations", allowableValues = "range[1,255]", required = true) @Multipart("values") List<String> values,
			@ApiParam(value = "Media files from sensor", allowableValues = "range[1,255]", required = false) @Multipart(value = "mediaFile") InputStream fileInputSteam,
			@ApiParam(value = "Media file name with extension from sensor", allowableValues = "range[1,255]", required = false) @Multipart(value = "fileName") String fileName);

	@ApiOperation(value = "Delete a sensor observation", notes = "delete one observation/measurement record by ID")
	@RequestMapping(method = RequestMethod.DELETE)
	@DELETE
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Path("/{obsvId}")
	public Response deleteObservation(
			@ApiParam(value = "System ID of observation/measurement", allowableValues = "range[1,20]", required = true) @PathParam("obsvId") String observationId);

	@ApiOperation(value = "Counts devices detected by a given sensor", notes = "Aggregate observation results (i.e.,detected encrypted device macAddress or car registration plate no) by observation time within a time range. The result is JSON Object containing JSON Array result like {\"results\" : [{\"time\": {\"type\": \"string\", \"count\": {\"type\" : \"int64\"}}]} ")
	@RequestMapping(method = RequestMethod.GET)
	@GET
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Path("/{sensorId}/counts")
	public Response aggregateObservations(
			@ApiParam(value = "Physical ID of sensor", allowableValues = "range[1,20]", required = true) @PathParam("sensorId") String sensorId,
			@ApiParam(value = "startDate in ISO 8601 format, for example: 2014-01-01T12:00:00+00:00", allowableValues = "range[1,20]", required = false) @QueryParam("startDate") String startDate,
			@ApiParam(value = "endDate in ISO 8601 format, for example: 2014-01-01T12:00:00+00:00", allowableValues = "range[1,20]", required = false) @QueryParam("endDate") String endDate);

//	@ApiOperation(value = "Run batch job to correct activity sensor observation time", notes = "Run batch job to correct sensor observation time from the GPS \"mTime\" to \"time\".")
//	@RequestMapping(method = RequestMethod.GET)
//	@GET
//	@CrossOriginResourceSharing(allowAllOrigins = true)
//	@Path("/{sensorId}/correctObsvTime")
//	public Response correctObservationTime(
//			@ApiParam(value = "Physical ID of sensor", allowableValues = "range[1,20]", required = true) @PathParam("sensorId") String sensorId);
	
	@ApiOperation(value = "Classify sensor readings from the observations big table to the optimised specific table", notes = "The operation serves as a data migration tool for sensor observation data.")
	@RequestMapping(method = RequestMethod.GET)
	@GET
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Path("/{sensorId}/classify")
	public Response classifyObservations(
			@ApiParam(value = "System ID of sensor", allowableValues = "range[1,20]", required = true) @PathParam("sensorId") String sensorId);

	@ApiOperation(value = "Query realtime data stream", notes = "The operation will query data input stream with default limit 20. The query will return data creation time, observation id, sensor reading, and sensor name.")
	@RequestMapping(method = RequestMethod.GET)
	@GET
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Path("/query/realtimeDataStream")
	public Response queryRealtimeDataStream(@QueryParam("limit") Integer limit);
	
	@ApiOperation(value = "Counts sensor readings by sub-categories (e.g., activity types for activity sensors)", notes = "Aggregate observation results by sub-categories and observation time within a time range. The result is JSON Object containing JSON Array result like {\"results\" : [{\"type\": \"string\", \"count\": {\"type\" : \"int64\"}]} ")
	@RequestMapping(method = RequestMethod.GET)
	@GET
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Path("/{sensorId}/counts/datatype")
	public Response countSensorObsvSubCategories(
			@ApiParam(value = "Physical ID of sensor", allowableValues = "range[1,20]", required = true) @PathParam("sensorId") String sensorId,
			@ApiParam(value = "startDate in ISO 8601 format, for example: 2014-01-01T12:00:00+00:00", allowableValues = "range[1,20]", required = false) @QueryParam("startDate") String startDate,
			@ApiParam(value = "endDate in ISO 8601 format, for example: 2014-01-01T12:00:00+00:00", allowableValues = "range[1,20]", required = false) @QueryParam("endDate") String endDate);

	@ApiOperation(value = "Sensor Data Bulk Upload", notes = "CTX/GPX training activity data bulk uploading")
	@RequestMapping(method = RequestMethod.POST)
	@POST
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Consumes("multipart/form-data")
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/bulkUploadGPSData")
	@WebMethod(operationName = "sensorDataBulkUpload")
	Response sensorDataBulkUpLoad(
			@ApiParam(value = "Physical sensor Id that associates with a registered sensor for which the sensor observations to be posted", allowableValues = "range[1,30]", required = true) @Multipart("sensorId") String sensorPhysicalId,
			@ApiParam(value = "TCX/GPX Activity tracking data file to be submit", allowableValues = "range[1,255]", required = false) @Multipart(value = "trackingDataFile") InputStream fileInputSteam,
			@ApiParam(value = "TCX/GPX Activity tracking data file file name with extension", allowableValues = "range[1,255]", required = false) @Multipart(value = "fileName") String fileName);

	@ApiOperation(value = "Delete all the observations", notes = "delete all the observation/measurement records of a given sensor physical id")
	@RequestMapping(method = RequestMethod.DELETE)
	@DELETE
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Path("/removeAll/{sensorId}")
	public Response deleteAllObservations(
			@ApiParam(value = "Sensor Physical ID", allowableValues = "range[1,20]", required = true) @PathParam("sensorId") String sensorId);

	@ApiOperation(value = "noramlise observation data", notes = "It currently supports update normalised activity data including activity type, latitude, longitude and confidence.")
	@RequestMapping(method = RequestMethod.POST)
	@POST
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Path("/normalise/{obsvId}")
	public Response normaliseObservation(
			@ApiParam(value = "Observation id", allowableValues = "range[1,30]", required = true) @PathParam("obsvId") Long obsvId,
			@ApiParam(value = "Final (normalised) Activity Type", allowableValues = "range[1,30]", required = false) @FormParam("finalActivityType") String finalActivityType,
			@ApiParam(value = "Final (normalised) Activity Type", allowableValues = "range[1,30]", required = false) @FormParam("finalLatitude") String finalLatitude,
			@ApiParam(value = "Final (normalised) Activity Type", allowableValues = "range[1,30]", required = false) @FormParam("finalLongitude") String finalLongitude,
			@ApiParam(value = "Final (normalised) Confidence", allowableValues = "range[1,30]", required = false) @FormParam("finalConfidence") String finalConfidence);	

	@ApiOperation(value = "Push sensor observations within compressed gzip", notes = "All the consumable data will be constructed in a json text/format: {\"data\": [{...}, {...}], \"sensorId\": \"XXXX\"} and compressed in gzip")
	@RequestMapping(method = RequestMethod.POST)
	@POST
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@WebMethod(operationName = "saveObservationsWithinGZIP")
	public Response saveObservation(String json);
}
