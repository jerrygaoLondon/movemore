package uk.ac.oak.movemore.webapp.service;

import javax.jws.WebService;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import uk.ac.oak.movemore.webapp.vo.DeviceVO;

@WebService
@Path("/device")
@Api(value = "/device", description = "Operations about device")
@Produces({ "application/json" })
public interface DeviceService {

	@ApiOperation(value = "update device location", notes = "update device location by device (physical) id")
	@RequestMapping(method = RequestMethod.GET)
	@GET
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Path("/{deviceId}/{latitude}/{longitude}")
	@Produces(MediaType.APPLICATION_JSON)
	Response updateDeviceLocation(
			@ApiParam(value = "Device physical id (e.g., mac address) that is pre-registered in the system", allowableValues = "range[1,30]", required = true) @PathParam("deviceId") String deviceId,
			@ApiParam(value = "Device current latitude", allowableValues = "range[1,13]", required = true) @PathParam("latitude") Double latitude,
			@ApiParam(value = "Device current longitude", allowableValues = "range[1,13]", required = true) @PathParam("longitude") Double longitude);

	@ApiOperation(value = "register a new device", notes = "register a new device by device (physical) id")
	@RequestMapping(method = RequestMethod.GET)
	@GET
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Path("/{devicePhysicalId}/{latitude}/{lonitude}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	Response registerNewDevice(
			@ApiParam(value = "Device physical id", allowableValues = "range[1,300]", required = true) @PathParam("devicePhysicalId") String devicePhysicalId,
			@ApiParam(value = "Device current latitude", allowableValues = "range[1,13]", required = true) @PathParam("latitude") Double latitude,
			@ApiParam(value = "Device current longitude", allowableValues = "range[1,13]", required = true) @PathParam("lonitude") Double longitude,
			@ApiParam(value = "Device name", allowableValues = "range[1,100]", required = true) @PathParam("name") String deviceName);

	@ApiOperation(value = "register or update a new device with a new sensor", notes = "register or update a new device and sensor by device (physical) id and sensor physical id")
	@RequestMapping(method = RequestMethod.POST)
	@POST
	@CrossOriginResourceSharing(allowAllOrigins = true)
	public Response registerOrUpdateDeviceInfo(
			@ApiParam(value = "Device physical id", allowableValues = "range[1,300]", required = true) @FormParam("deviceId") String deviceId,
			@ApiParam(value = "Device name", allowableValues = "range[1,100]", required = true) @FormParam("deviceName") String deviceName,
			@ApiParam(value = "Sensor physical id", allowableValues = "range[1,300]", required = true) @FormParam("sensorId") String sensorId,
			@ApiParam(value = "Sensor name", allowableValues = "range[1,100]", required = false) @FormParam("sensorName") String sensorName,
			@ApiParam(value = "Device current latitude", allowableValues = "range[1,13]", required = false) @FormParam("latitude") Double latitude,
			@ApiParam(value = "Device current longitude", allowableValues = "range[1,13]", required = false) @FormParam("longitude") Double longitude,
			@ApiParam(value = "Device battery level", allowableValues = "range[1,13]", required = false) @FormParam("batteryLevel") Float batteryLevel,
			@ApiParam(value = "Sensor description", allowableValues = "range[1,30]", required = false) @FormParam("sensorDesc") String sensorDescription,
			@ApiParam(value = "Sensor Type", allowableValues = "range[1,30]", required = false) @FormParam("sensorType") String sensorType);

	@ApiOperation(value = "Get device sensors", notes = "Returns sensors based on sensor physical id")
	@RequestMapping(method = RequestMethod.GET)
	@GET
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Path("/{deviceId}/sensors")
	public Response getAllSensors(@ApiParam(value = "Device physical id", allowableValues = "range[1,300]", required = true) @PathParam("deviceId") String deviceId);
	
	@ApiOperation(value = "Get devices", notes = "Returns all devices", response = DeviceVO.class)
	@RequestMapping(method = RequestMethod.GET)
	@GET
	@CrossOriginResourceSharing(allowAllOrigins = false)
	@Path("")
	public Response getAllDevices();
}
