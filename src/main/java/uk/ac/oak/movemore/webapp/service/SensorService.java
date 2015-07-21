package uk.ac.oak.movemore.webapp.service;

import javax.jws.WebService;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import uk.ac.oak.movemore.webapp.service.response.JSONResponse;
import uk.ac.oak.movemore.webapp.vo.SensorVO;

@WebService
@Path("/sensor")
@Api(value = "/sensor", description = "Operations about sensors")
@Produces({ "application/json" })
public interface SensorService {

	@ApiOperation(value = "add a new sensor", notes = "add a new sensor for a pre-registered device by device physical id and sensor physical id. Sensor type could be empty if you don't know the classification no.")
	@RequestMapping(method = RequestMethod.GET)
	@GET
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Path("/{deviceId}/{sensorId}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	JSONResponse addNewSensor(
			@ApiParam(value = "Device physical id available by device (e.g., mac address) and is pre-registered in the system", allowableValues = "range[1,30]", required = true) @PathParam("deviceId") String devicePhysicalId,
			@ApiParam(value = "Sensor physical id available by sensor (e.g., mac address) and is pre-registered in the system", allowableValues = "range[1,30]", required = true) @PathParam("sensorId") String sensorPhysicalId,
			@ApiParam(value = "Sensor Name", allowableValues = "range[1,100]", required = true) @PathParam("name") String sensorName,
			@ApiParam(value = "Sensor Type", allowableValues = "range[1,100]", required = false) @PathParam("sensorType") Integer sensorType);

	@ApiOperation(value = "Get sensors", notes = "get all the sensors available in the system", response = SensorVO.class)
	@RequestMapping(method = RequestMethod.GET)
	@GET
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	Response getAllSensors();

	@ApiOperation(value = "delete sensor", notes = "delete sensor by sensor (system) id")
	@RequestMapping(method = RequestMethod.GET)
	@DELETE
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Path("/{sensorId}")
	@Produces(MediaType.APPLICATION_JSON)
	JSONResponse removeSensor(
			@ApiParam(value = "System ID of sensor that needs to be removed", allowableValues = "range[1,20]", required = true) @PathParam("sensorId") String sensorId);

	@ApiOperation(value = "Get sensor", notes = "Returns sensor with given sensor id")
	@RequestMapping(method = RequestMethod.GET)
	@GET
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Path("/{sensorId}")
	Response getSensor(
			@ApiParam(value = "Physical ID of sensor", allowableValues = "range[1,20]", required = true) @PathParam("sensorId") String sensorId);

	@ApiOperation(value = "Count sensors by type", notes = "Returns sensor type (\"sensorType\"), num of sensors (\"count\") and the sensor type name (\"typeName\").")
	@RequestMapping(method = RequestMethod.GET)
	@GET
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Path("/countSensorsByType")
	Response countSensorsByType();
}
