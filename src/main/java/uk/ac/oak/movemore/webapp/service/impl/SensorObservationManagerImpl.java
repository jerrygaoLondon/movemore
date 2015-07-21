package uk.ac.oak.movemore.webapp.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import lt.overdrive.trackparser.domain.Track;
import lt.overdrive.trackparser.domain.Trail;
import lt.overdrive.trackparser.parsing.GpsFileType;
import lt.overdrive.trackparser.parsing.Parser;
import lt.overdrive.trackparser.parsing.ParserException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.appfuse.dao.GenericDao;
import org.appfuse.service.impl.GenericManagerImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.oak.movemore.webapp.dao.ObservationsDao;
import uk.ac.oak.movemore.webapp.dao.ObsvActivityDetectionDao;
import uk.ac.oak.movemore.webapp.dao.ObsvCarRegPlateDetectionDao;
import uk.ac.oak.movemore.webapp.dao.ObsvDeviceDetectionDao;
import uk.ac.oak.movemore.webapp.dao.ObsvOBDDetectionDao;
import uk.ac.oak.movemore.webapp.dao.ObsvPeopleCountDao;
import uk.ac.oak.movemore.webapp.dao.ObsvVoiceDecibelDetectionDao;
import uk.ac.oak.movemore.webapp.dao.SensorNotFoundException;
import uk.ac.oak.movemore.webapp.dao.SensorsDao;
import uk.ac.oak.movemore.webapp.model.Observations;
import uk.ac.oak.movemore.webapp.model.ObsvCarRegPlateDetection;
import uk.ac.oak.movemore.webapp.model.ObsvDeviceDetection;
import uk.ac.oak.movemore.webapp.model.ObsvPeopleCount;
import uk.ac.oak.movemore.webapp.model.Sensors;
import uk.ac.oak.movemore.webapp.service.DeviceManager;
import uk.ac.oak.movemore.webapp.service.SensorObservationManager;
import uk.ac.oak.movemore.webapp.service.SensorObservationService;
import uk.ac.oak.movemore.webapp.service.SensorObservationsProcessor;
import uk.ac.oak.movemore.webapp.service.response.ObservationDetail;
import uk.ac.oak.movemore.webapp.service.response.SensorObservationSuccess;
import uk.ac.oak.movemore.webapp.service.response.JSONResponse;
import uk.ac.oak.movemore.webapp.service.response.ObservationCollection;
import uk.ac.oak.movemore.webapp.service.response.ServiceResponseFailure;
import uk.ac.oak.movemore.webapp.util.DateUtil;
import uk.ac.oak.movemore.webapp.util.ISO8601DateParser;
import uk.ac.oak.movemore.webapp.util.SensorTypeEnum;

@Service("sensorObservationManager")
@Transactional
@WebService(serviceName = "SensorObservationService", endpointInterface = "uk.ac.oak.movemore.webapp.service.SensorObservationService")
public class SensorObservationManagerImpl extends
		GenericManagerImpl<Observations, Long> implements
		SensorObservationManager, SensorObservationService {
	protected final Log log = LogFactory
			.getLog(SensorObservationManagerImpl.class);

	@Autowired
	private ObservationsDao observationsDao;

	@Autowired
	private SensorsDao sensorsDao;

	@Autowired
	private DeviceManager deviceManager;

	@Autowired
	private ObsvPeopleCountDao obsvPeopleCountDao;

	@Autowired
	private ObsvDeviceDetectionDao obsvDeviceDetectionDao;

	@Autowired
	private ObsvCarRegPlateDetectionDao obsvCarRegPlateDetectionDao;

	@Autowired
	private ObsvOBDDetectionDao obsvOBDDetectionDao;

	@Autowired
	private ObsvVoiceDecibelDetectionDao obsvVoiceDecibelDetectionDao;

	@Autowired
	private ObsvActivityDetectionDao obsvActivityDetectionDao;

	private final SensorObservationsProcessor generalSensorObsvProcessor;

	public SensorObservationManagerImpl() {
		generalSensorObsvProcessor = SensorProcessorFactory
				.createSensorObsvProcessor();
	}

	@Override
	@WebMethod(operationName = "pushSensorObservation")
	public JSONResponse pushSensorObservation(String sensorId, String value,
			String obsvTime) {
		log.debug(String
				.format(" ===== push Sensor Observations: sensor id {%s}, value is {%s}, obsv time is {%s} ",
						sensorId, value, obsvTime));

		JSONResponse obsvResp;
		Observations obsv;

		if (StringUtils.isEmpty(sensorId)) {
			obsvResp = new ServiceResponseFailure();
			obsvResp.setIsSuccess(-1);
			((ServiceResponseFailure) obsvResp)
					.setMessage("Please provide valid sensor.");
			((ServiceResponseFailure) obsvResp)
					.setReason("'{sensorId}' IS EMPTY");
			return obsvResp;
		}

		if (StringUtils.isEmpty(value)) {
			obsvResp = new ServiceResponseFailure();
			obsvResp.setIsSuccess(-1);
			((ServiceResponseFailure) obsvResp)
					.setMessage("Please provide valid observation value.");
			((ServiceResponseFailure) obsvResp).setReason("'{value}' IS EMPTY");
			return obsvResp;
		}

		if (StringUtils.isEmpty(obsvTime)) {
			obsvResp = new ServiceResponseFailure();
			obsvResp.setIsSuccess(-1);
			((ServiceResponseFailure) obsvResp)
					.setMessage("Please provide valid observation timestamp.");
			((ServiceResponseFailure) obsvResp)
					.setReason("'{obsvTime}' IS EMPTY");
			return obsvResp;
		}

		if (!isTimeStampValid(obsvTime)) {
			obsvResp = new ServiceResponseFailure();
			obsvResp.setIsSuccess(-1);
			((ServiceResponseFailure) obsvResp)
					.setMessage("Please provide valid observation timestamp in the format \"yyyy-MM-dd HH:mm:ss.SSSSSS\".");
			((ServiceResponseFailure) obsvResp)
					.setReason("'{obsvTime}' IS invalid");
			return obsvResp;
		}

		try {
			Sensors sensor = sensorsDao.get(Long.valueOf(sensorId));

			obsv = new Observations(sensor, value, Timestamp.valueOf(obsvTime));
			obsv.setLatitude(sensor.getDevice().getLatitude());
			obsv.setLongitude(sensor.getDevice().getLongitude());

			obsv = generalSensorObsvProcessor.saveObservation(this, obsv);

			obsvResp = new SensorObservationSuccess();
			obsvResp.setIsSuccess(1);
			((SensorObservationSuccess) obsvResp).setObsvId(String.valueOf(obsv
					.getObsvId()));

			return obsvResp;
		} catch (ObjectRetrievalFailureException objReEx) {
			obsvResp = new ServiceResponseFailure();
			obsvResp.setIsSuccess(-1);
			((ServiceResponseFailure) obsvResp)
					.setMessage("Sensor not found. Please provide valid sensor.");
			((ServiceResponseFailure) obsvResp).setReason(objReEx.getMessage());
			log.warn(objReEx.getMessage());
		} catch (Exception ex) {
			obsvResp = generalObsvServiceFailureResp(ex);
		}

		return obsvResp;

	}

	public static boolean isTimeStampValid(String inputString) {
		SimpleDateFormat format = new java.text.SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSSSSS");
		try {
			format.parse(inputString);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	@Override
	@WebMethod(operationName = "getSensorObservationBySensorId")
	@Transactional
	public Response getSensorObservationBySensorId(String sensorId,
			String startDate, String endDate, String orderByName,
			Boolean isAsc, Integer offset, Integer limit) {
		log.info(String
				.format("Query sensor [%s] measurements, startDate [%s], endDate[%s], orderByName [%S], isAsc[%s], offset [%s], limit [%s]",
						sensorId, startDate, endDate, orderByName, isAsc,
						offset, limit));
		if (StringUtils.isEmpty(sensorId)) {
			return badRequest("'sensorId' (physical id) is Empty");
		}

		// simply convert to Date from UTF 8601 string format
		Date _startDate = null;
		if (!StringUtils.isEmpty(startDate)) {
			try {
				_startDate = ISO8601DateParser.toDate(startDate);
			} catch (Exception pEx) {
				log.error(pEx.toString());
			}

			if (_startDate == null) {
				return badRequest(String
						.format("Unparseable startDate: \"%s\". Expect ISO 8601 format, for example: 2014-01-01T12:00:00+00:00",
								startDate));
			}
		}

		Date _endDate = null;
		if (!StringUtils.isEmpty(endDate)) {
			try {
				_endDate = ISO8601DateParser.toDate(endDate);
			} catch (Exception pEx) {
				log.error(pEx.toString());
			}

			if (_endDate == null) {
				return badRequest(String
						.format("Unparseable endDate: \"%s\". Expect ISO 8601 format, for example: 2014-01-01T12:00:00+00:00",
								endDate));
			}
		}

		Sensors sensor = null;
		try {
			sensor = sensorsDao.findSensorByPhysicalId(sensorId);
		} catch (SensorNotFoundException objRetEx) {
			log.error(objRetEx.getMessage());
		}

		if (sensor == null) {
			return badRequest("Sensor not found");
		}

		JSONArray results = generalSensorObsvProcessor
				.findObservationsBySensor(this, sensor, _startDate, _endDate,
						orderByName, isAsc, offset, limit);

		return Response.status(Status.OK).entity(results.toString()).build();
	}

	// @Override
	// @Transactional
	// public Response correctObservationTime(String sensorId) {
	//
	// Sensors sensor = null;
	// try {
	// sensor = sensorsDao
	// .findSensorByPhysicalId(sensorId);
	//
	// List<Observations> obsvList =
	// observationsDao.findObservationsBySensor(sensor);
	//
	// log.info("start to correct observation time for ["+obsvList.size()+"] observation records.");
	//
	// for (Observations obsv : obsvList) {
	// ObsvActivityDetection obsvDetection =
	// obsvActivityDetectionDao.get(obsv.getObsvId());
	// obsvDetection.setObsvTime(obsv.getObsvTime());
	// obsvActivityDetectionDao.save(obsvDetection);
	// }
	//
	// } catch (SensorNotFoundException objRetEx) {
	// log.error(objRetEx.getMessage());
	// }
	//
	// if(sensor == null) {
	// return badRequest("Sensor not found");
	// }
	//
	// return Response.status(Status.OK).build();
	// }

	private Response badRequest(String msg) {
		return Response.status(Status.BAD_REQUEST.getStatusCode()).entity(msg)
				.build();
	}

	@Override
	@WebMethod(operationName = "getSensorObservationBySensorId")
	public JSONResponse getSensorObservationBySensorId(String sensorId) {
		JSONResponse obsvResp;
		if (StringUtils.isEmpty(sensorId)) {
			obsvResp = new ServiceResponseFailure();
			obsvResp.setIsSuccess(-1);
			((ServiceResponseFailure) obsvResp)
					.setReason("'sensorId' is Empty");
			((ServiceResponseFailure) obsvResp)
					.setMessage("Please provide valid sensor id to retrive detailed observatory information.");
		}

		try {
			Sensors sensor = sensorsDao.get(Long.valueOf(sensorId));
			List<ObservationDetail> obsdetails = new ArrayList<ObservationDetail>();

			Set<Observations> obsvSet = observationsDao
					.findDistinctObservationsBySensor(sensor);

			obsvResp = new ObservationCollection();
			obsvResp.setIsSuccess(1);

			for (Observations obsv : obsvSet) {
				ObservationDetail obsdet = new ObservationDetail();
				obsdet.copyProperties(obsv);

				obsdet.setIsSuccess(1);
				obsdetails.add(obsdet);
			}
			((ObservationCollection) obsvResp)
					.setObservationDetails(obsdetails);
			return obsvResp;
		} catch (ObjectRetrievalFailureException objRetEx) {
			obsvResp = new ServiceResponseFailure();
			obsvResp.setIsSuccess(-1);
			((ServiceResponseFailure) obsvResp)
					.setReason(objRetEx.getMessage());
			((ServiceResponseFailure) obsvResp)
					.setMessage("No observation information has found.");
			return obsvResp;
		} catch (Exception ex) {
			obsvResp = generalObsvServiceFailureResp(ex);
		}

		return obsvResp;
	}

	@Override
	@WebMethod(operationName = "getSensorObservationById")
	public JSONResponse getSensorObservationById(String obsvId) {
		JSONResponse obsvResp;
		if (StringUtils.isEmpty(obsvId)) {
			obsvResp = new ServiceResponseFailure();
			obsvResp.setIsSuccess(-1);
			((ServiceResponseFailure) obsvResp).setReason("'obsvId' is Empty");
			((ServiceResponseFailure) obsvResp)
					.setMessage("Please provide valid observation id to retrive detailed observatory information.");
		}

		try {
			Observations obsv = observationsDao.get(Long.valueOf(obsvId));
			obsvResp = new ObservationDetail();
			obsvResp.setIsSuccess(1);

			((ObservationDetail) obsvResp).copyProperties(obsv);
			return obsvResp;
		} catch (ObjectRetrievalFailureException objRetEx) {
			obsvResp = new ServiceResponseFailure();
			obsvResp.setIsSuccess(-1);
			((ServiceResponseFailure) obsvResp)
					.setReason(objRetEx.getMessage());
			((ServiceResponseFailure) obsvResp)
					.setMessage("No observation information has found.");
			return obsvResp;
		} catch (Exception ex) {
			obsvResp = generalObsvServiceFailureResp(ex);
		}

		return obsvResp;
	}

	private JSONResponse generalObsvServiceFailureResp(Exception ex) {
		JSONResponse obsvResp;
		obsvResp = new ServiceResponseFailure();
		obsvResp.setIsSuccess(-1);
		((ServiceResponseFailure) obsvResp).setReason(ex.getMessage());
		((ServiceResponseFailure) obsvResp).setMessage("Unexpected Exception.");
		log.error("SensorObservationManager Error," + ex.getMessage());
		return obsvResp;
	}

	@Override
	@Transactional
	public Response saveObservation(String sensorPhysicalId, String time,
			List<String> values) {
		log.info(String.format(
				" save sensor observation value, sensor is %s, time is %s",
				sensorPhysicalId, time));

		if (StringUtils.isEmpty(sensorPhysicalId)) {
			return badRequest("Please provide valid sensor. current \"sensorId\" is empty.");
		}

		if (StringUtils.isEmpty(time)) {
			log.warn("Please provide valid observation timestamp. current {timestamp} is empty. System will use local time (auto-genereated UTC time) instead. ");
			time = DateUtil.getCurrentUTCTime();
		}

		if (!isTimeStampValid(time)) {
			return badRequest("Please provide valid observation timestamp in the format \"yyyy-MM-dd HH:mm:ss.SSSSSS\".");
		}

		if (values == null || values.isEmpty()) {
			return badRequest(String
					.format("Please provide valid observation value. current {values} sent from %s is empty.",
							sensorPhysicalId));
		}
		ResponseBuilder failureResp;
		try {
			saveObservations(sensorPhysicalId, Timestamp.valueOf(time), values);

			return Response.status(Status.OK).build();
		} catch (SensorNotFoundException objReEx) {
			log.warn("Please provide valid sensor id. Current sensorId {"
					+ sensorPhysicalId + "} has not been registered in system.");
			failureResp = Response
					.status(Status.NOT_FOUND)
					.entity(String
							.format("Sensor [%s] not found. Please provide valid sensor.",
									sensorPhysicalId));
		} catch (Exception ex) {
			log.error("sensor id:" + sensorPhysicalId + " " + ex.getMessage());
			failureResp = Response.status(Status.BAD_REQUEST.getStatusCode())
					.entity(ex.toString());
		}

		return failureResp.build();
	}

	private void saveObservations(String sensorPhysicalId, Timestamp time,
			List<String> values) throws SensorNotFoundException,JSONException, Exception {
		Sensors sensor = sensorsDao.findSensorByPhysicalId(sensorPhysicalId);

		Float devLatitude = sensor.getDevice().getLatitude();
		Float devLongitude = sensor.getDevice().getLongitude();

		Observations obsv;
		for (String value : values) {
			obsv = new Observations(sensor, value, time);

			obsv.setLatitude(devLatitude);
			obsv.setLongitude(devLongitude);

			generalSensorObsvProcessor.saveObservation(this, obsv);
		}
	}

	@Override
	public Response saveObservation(String json) {
		log.info("Message received!");
		log.info(json);
		boolean failure = false;
		String failureMessage = "";
		String sensorPhysicalId = "";
		try {
			JSONObject jsonObj = new JSONObject(json);

			if (jsonObj.isNull("sensorId")) {
				return badRequest("'sensorId' is required.");
			}

			if (jsonObj.isNull("data")) {
				return badRequest("'data' is required. No data found!");
			}

			sensorPhysicalId = (String) jsonObj.get("sensorId");

			JSONArray data = jsonObj.getJSONArray("data");
			for (int i = 0; i < data.length(); i++) {
				JSONObject oneObsv = (JSONObject) data.get(i);
				// "time" is for activity sensor and "timestamp" is for OBD
				// sensor
				if (oneObsv.isNull("time") && oneObsv.isNull("timestamp")) {
					return badRequest(String.format(
							"'time' is not found in observation value [%s]",
							oneObsv.toString()));
				}

				Long obsvTime = null;
				if (!oneObsv.isNull("time")) {
					obsvTime = oneObsv.getLong("time");
				} else if (!oneObsv.isNull("timestamp")) {
					obsvTime = oneObsv.getLong("timestamp");
				}

				if (obsvTime == null) {
					return badRequest(String
							.format("'time' is not found in 'data' observation value [%s]",
									oneObsv.toString()));
				}

				List<String> values = Arrays.asList(oneObsv.toString());
				saveObservations(sensorPhysicalId, new Timestamp(obsvTime),
						values);
			}

		} catch (JSONException jsonEx) {
			failure = true;
			log.error(jsonEx);
			failureMessage = String.format(
					"Syntax Error in JSON text. The exception is [%s]",
					jsonEx.toString());
		} catch (SensorNotFoundException objReEx) {
			failure = true;
			log.warn("Please provide valid sensor id. Current sensorId {"
					+ sensorPhysicalId + "} has not been registered in system.");
			failureMessage = String.format(
					"Sensor [%s] not found. Please provide valid sensor.",
					sensorPhysicalId);
		} catch (ClassCastException cce) {
			failure = true;
			failureMessage = "Observation data syntax error : "
					+ cce.toString();
		} catch (Exception ex) {
			failure = true;
			failureMessage = "Unexpected data parsing error. The exception is "
					+ ex.toString();
		}

		if (failure) {
			return Response.status(Status.BAD_REQUEST).entity(failureMessage)
					.build();
		}

		return Response.status(Status.OK).build();
	}

	@Override
	@Transactional
	public Response saveObservation(String sensorPhysicalId, String time,
			List<String> values, InputStream uploadedFile, String fileName) {

		log.info(String
				.format("Post sensor observation with attachment: sensor id {%s}, time {%s}, fileName {%s}",
						sensorPhysicalId, time, fileName));

		if (StringUtils.isEmpty(sensorPhysicalId)) {
			return badRequest("'sensorId' (physical id) is Empty");
		}

		if (values == null || values.isEmpty()) {
			return badRequest("Please provide valid observation values as 'values' is Empty.");
		}

		if (!isTimeStampValid(time)) {
			log.warn("Please provide valid observation timestamp in the format \"yyyy-MM-dd HH:mm:ss.SSSSSS\".");
			return badRequest(String
					.format("'time' {%s} provided is invalid. Please provide valid observation timestamp in the format \"yyyy-MM-dd HH:mm:ss.SSSSSS\".",
							time));
		}

		Sensors sensor = null;
		try {
			sensor = sensorsDao.findSensorByPhysicalId(sensorPhysicalId);
		} catch (SensorNotFoundException objReEx) {
			log.error("Sensor not found. Please provide valid sensor.");
		}

		if (sensor == null) {
			return badRequest("Sensor not found. Please provide valid sensor.");
		}

		Observations obsv;
		try {
			for (String value : values) {
				obsv = new Observations(sensor, value, Timestamp.valueOf(time));

				obsv.setLatitude(sensor.getDevice().getLatitude());
				obsv.setLongitude(sensor.getDevice().getLongitude());

				if (uploadedFile != null) {

					generalSensorObsvProcessor.saveObservation(this, obsv,
							uploadedFile, fileName);

				} else {
					generalSensorObsvProcessor.saveObservation(this, obsv);
				}
			}
		} catch (JSONException e) {
			return Response.status(Status.BAD_REQUEST.getStatusCode())
					.entity(e.toString()).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST.getStatusCode())
					.entity(e.toString()).build();
		}

		return Response.status(Status.OK.getStatusCode()).build();
	}

	/**
	 * Bulk upload for TrainingCenterDatabase data(*.tcx) and Strava: Running
	 * and Cycling GPS Tracker for iPhone (*.gpx)
	 * 
	 * @param sensorPhysicalId
	 * @param uploadedFile
	 * @param fileName
	 * @return
	 */
	@Transactional
	public Response sensorDataBulkUpLoad(String sensorPhysicalId,
			InputStream uploadedFile, String fileName) {
		log.info(String
				.format("Bulk upload tcx/gpx activity sensor data to the server. The sensor id is [%s] and the dataset file is [%s]",
						sensorPhysicalId, fileName));

		if (StringUtils.isEmpty(sensorPhysicalId)) {
			return badRequest("'sensorId' (physical id) is Empty");
		}

		Sensors sensor = null;
		try {
			sensor = sensorsDao.findSensorByPhysicalId(sensorPhysicalId);
		} catch (SensorNotFoundException objReEx) {
			log.error("Sensor not found. Please provide valid sensor.");
		}

		if (sensor == null) {
			return badRequest("Sensor not found. Please provide valid sensor.");
		}

		if (uploadedFile == null) {
			return badRequest("File content is empty!");
		}

		if (StringUtils.isEmpty(fileName)) {
			return badRequest("File name is empty!");
		}

		File trainingActivityfile;
		OutputStream outputStream = null;
		try {
			// need to assign permission to all user for the file by
			// "sudo chmod 757 -R /var/lib/tomcat7" if it throws
			// "Permission denied" problem
			trainingActivityfile = new File(Paths.get("").toAbsolutePath()
					.toString()
					+ "/" + fileName);
			outputStream = new FileOutputStream(trainingActivityfile);
			IOUtils.copy(uploadedFile, outputStream);

			Parser generalParser = new Parser(trainingActivityfile);
			GpsFileType gpsFileType = generalParser.guessFileType();

			if (GpsFileType.UNKNOWN.equals(gpsFileType)) {
				return badRequest("File format is not recognised. Only TCX and GPX formats are supported!");
			}

			ActivitySensorObsvProcessor activityObsvProcessor = new ActivitySensorObsvProcessor();

			activityObsvProcessor.saveObservations(
					Parser.parseFile(trainingActivityfile), sensor, this);

			trainingActivityfile.delete();
		} catch (IOException ioe) {
			log.error(ioe.toString());
			return Response
					.status(Status.INTERNAL_SERVER_ERROR.getStatusCode())
					.entity(ioe.toString()).build();
		} catch (ParserException pe) {
			log.error(pe.toString());
			return Response
					.status(Status.INTERNAL_SERVER_ERROR.getStatusCode())
					.entity(pe.toString()).build();
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				log.error(e);
			}
		}

		return Response.status(Status.OK.getStatusCode()).build();
	}

	public static void main(String[] args) throws Exception {
		// String vale = "2014-07-10T17:00:00+00:00122";
		//
		// try {
		// ISO8601DateParser.parse(vale);
		// } catch (ParseException e) {
		// System.out.println(e.toString());
		// e.printStackTrace();
		// }
		// 20130117-174731-Ride.gpx
		// 422147750.tcx
		File trainingActivityfile = new File(
				"C:/Users/jieg/Desktop/wesenseit-annalist-neil-data/422147750.tcx");

		InputStream fileStream;

		try {
			fileStream = new FileInputStream(trainingActivityfile);

			File trainingActivityfileInput;

			trainingActivityfileInput = new File("422147750.tcx");
			OutputStream outputStream = new FileOutputStream(
					trainingActivityfileInput);
			IOUtils.copyLarge(fileStream, outputStream);

			System.out.println(FileUtils
					.readFileToString(trainingActivityfileInput));

			Parser generalParser = new Parser(trainingActivityfileInput);
			GpsFileType gpsFileType = generalParser.guessFileType();
			System.out.println(gpsFileType.toString());
			Trail trail = Parser.parseFile(trainingActivityfileInput);

			trail.getTracks();
			List<Track> trackList = trail.getTracks();
			for (Track track : trackList) {
				System.out.println(track.toString());
			}

		} catch (ParserException e) {
			e.printStackTrace();
		}

	}

	@Override
	@Transactional
	public Response aggregateObservations(String sensorId, String startDate,
			String endDate) {
		if (StringUtils.isEmpty(sensorId)) {
			return badRequest("'sensorId' (physical id) is Empty");
		}

		// simply convert to Date from UTF 8601 string format
		Date _startDate = null;
		if (!StringUtils.isEmpty(startDate)) {
			try {
				_startDate = ISO8601DateParser.toDate(startDate);
			} catch (Exception pEx) {
				log.error(pEx.toString());
			}

			if (_startDate == null) {
				return badRequest(String
						.format("Unparseable startDate: \"%s\". Expect ISO 8601 format, for example: 2014-01-01T12:00:00+00:00",
								startDate));
			}
		}

		Date _endDate = null;
		if (!StringUtils.isEmpty(endDate)) {
			try {
				_endDate = ISO8601DateParser.toDate(endDate);
			} catch (Exception pEx) {
				log.error(pEx.toString());
			}

			if (_endDate == null) {
				return badRequest(String
						.format("Unparseable endDate: \"%s\". Expect ISO 8601 format, for example: 2014-01-01T12:00:00+00:00",
								endDate));
			}
		}

		Sensors sensor = null;
		try {
			sensor = sensorsDao.findSensorByPhysicalId(sensorId);
		} catch (SensorNotFoundException objRetEx) {
			log.error(objRetEx.getMessage());
		}

		if (sensor == null) {
			return badRequest("Sensor not found");
		}

		JSONArray jsonResults = generalSensorObsvProcessor
				.aggregateObservationResultsBySensor(this, sensor, _startDate,
						_endDate);

		JSONObject result = new JSONObject();
		result.put("results", jsonResults);

		return Response.status(Status.OK.getStatusCode())
				.entity(result.toString()).build();
	}

	@Override
	public Set<ObsvDeviceDetection> findDetectedDeviceObsvsBySensor(
			Sensors sensor) {
		return obsvDeviceDetectionDao
				.findDistinctDeviceObservationsBySensor(sensor);
	}

	@Override
	public Set<ObsvPeopleCount> findPeopleCounterObservationsBySensor(
			Sensors sensor) {
		return obsvPeopleCountDao
				.findDistinctPeopleCounterObservationsBySensor(sensor);
	}

	@Override
	public Set<ObsvCarRegPlateDetection> findCarRegPlateObservationsBySensor(
			Sensors sensor) {
		return obsvCarRegPlateDetectionDao
				.findDistinctCarRegPlateObservationsBySensor(sensor);
	}

	@Override
	@Transactional
	@WebMethod(operationName = "deleteObservation")
	public Response deleteObservation(String observationId) {
		boolean isExist = observationsDao.exists(Long.valueOf(observationId));

		if (!NumberUtils.isNumber(observationId) && !isExist) {
			return Response.status(Status.NOT_FOUND)
					.entity("Observation Not Found.").build();
		}

		Observations obsv = observationsDao.get(Long.valueOf(observationId));

		observationsDao.removeObservationAttachment(obsv);
		observationsDao.remove(obsv);

		return Response.status(Status.OK).build();
	}

	@Override
	@Transactional
	public Response classifyObservations(String sensorId) {

		if (StringUtils.isEmpty(sensorId) || !NumberUtils.isNumber(sensorId)) {
			return badRequest("'sensorId' (system id) is invalid.");
		}

		if (!sensorsDao.exists(Long.valueOf(sensorId))) {
			return badRequest("'sensorId' not found.");
		}

		Sensors sensor = sensorsDao.get(Long.valueOf(sensorId));

		List<Observations> obsvList = generalSensorObsvProcessor
				.queryUnclassifiedObservations(this, sensor);

		log.info(String.format(
				"Total [%s] unclassified observations found for sensor [%s]",
				obsvList.size(), sensor.getSensorPhysicalId()));

		log.info("starting to migrate observations...");

		generalSensorObsvProcessor.migrateUnclassifiedObservations(this,
				sensor, obsvList);

		log.info("Data migration completed!");
		return Response.status(Status.OK.getStatusCode()).build();
	}

	@Override
	@Transactional
	public Response queryRealtimeDataStream(Integer limit) {

		List<Observations> obsvList = observationsDao
				.queryRealtimeDataStream(limit);

		JSONArray jsonResArr = new JSONArray();

		DateFormat dateDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		for (Observations obsv : obsvList) {
			JSONObject jsonObj = new JSONObject();

			jsonObj.put("created", dateDf.format(obsv.getCreated()));
			jsonObj.put("obsvId", obsv.getObsvId());
			jsonObj.put("value", obsv.getValue());
			jsonObj.put("name", obsv.getSensor().getName());
			jsonResArr.put(jsonObj);
		}

		return Response.status(Status.OK.getStatusCode())
				.entity(jsonResArr.toString()).build();
	}

	@Override
	@Transactional
	public Response countSensorObsvSubCategories(String sensorId,
			String startDate, String endDate) {
		Date _startDate = null;
		Date _endDate = null;
		Sensors sensor = null;

		if (StringUtils.isEmpty(sensorId)) {
			return badRequest("'sensorId' (physical id) is Empty");
		}

		// simply convert to Date from UTF 8601 string format
		if (!StringUtils.isEmpty(startDate)) {
			try {
				_startDate = ISO8601DateParser.toDate(startDate);
			} catch (Exception pEx) {
				log.error(pEx.toString());
			}

			if (_startDate == null) {
				return badRequest(String
						.format("Unparseable startDate: \"%s\". Expect ISO 8601 format, for example: 2014-01-01T12:00:00+00:00",
								startDate));
			}
		}

		if (!StringUtils.isEmpty(endDate)) {
			try {
				_endDate = ISO8601DateParser.toDate(endDate);
			} catch (Exception pEx) {
				log.error(pEx.toString());
			}

			if (_endDate == null) {
				return badRequest(String
						.format("Unparseable endDate: \"%s\". Expect ISO 8601 format, for example: 2014-01-01T12:00:00+00:00",
								endDate));
			}
		}

		try {
			sensor = sensorsDao.findSensorByPhysicalId(sensorId);
		} catch (SensorNotFoundException objRetEx) {
			log.error(objRetEx.getMessage());
		}

		if (sensor == null) {
			return badRequest("Sensor not found");
		}

		JSONArray countRes = generalSensorObsvProcessor
				.countSensorObsvSubCategories(this, sensor, _startDate,
						_endDate);
		JSONObject result = new JSONObject();
		result.put("results", countRes);

		return Response.status(Status.OK.getStatusCode())
				.entity(result.toString()).build();
	}

	@Override
	public Response deleteAllObservations(String sensorId) {
		if (StringUtils.isEmpty(sensorId)) {
			return badRequest("'sensorId' (physical id) is Empty");
		}
		Sensors sensor = null;
		try {
			sensor = sensorsDao.findSensorByPhysicalId(sensorId);
		} catch (SensorNotFoundException objRetEx) {
			log.error(objRetEx.getMessage());
		}

		if (sensor == null) {
			return badRequest("Sensor not found");
		}

		observationsDao.removeAllObsvsBySensor(sensor);

		return Response.status(Status.OK.getStatusCode()).build();
	}

	@Override
	@WebMethod(exclude = true)
	public List<Observations> getAll() {
		return observationsDao.getAll();
	}

	@Override
	@WebMethod(exclude = true)
	public Observations get(Long id) {
		return observationsDao.get(id);
	}

	@Override
	@WebMethod(exclude = true)
	public boolean exists(Long id) {
		return observationsDao.exists(id);
	}

	@Override
	@WebMethod(exclude = true)
	public Observations save(Observations object) {
		return observationsDao.save(object);
	}

	@Override
	@WebMethod(operationName = "removeObsvObj", exclude = true)
	public void remove(Observations object) {
		observationsDao.removeObservationAttachment(object);
		observationsDao.remove(object);
	}

	@Override
	@WebMethod(operationName = "removeObsvId", exclude = true)
	public void remove(Long id) {
		observationsDao.removeObservationAttachment(id);
		observationsDao.remove(id);
	}

	@Override
	@WebMethod(exclude = true)
	public void reindex() {
		observationsDao.reindex();
	}

	@Override
	@WebMethod(exclude = true)
	public void reindexAll(boolean async) {
		observationsDao.reindexAll(async);
	}

	@Override
	@WebMethod(exclude = true)
	public boolean equals(Object arg0) {
		return observationsDao.equals(arg0);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public GenericDao getObservationDao(SensorTypeEnum sensorType) {
		switch (sensorType.getSensorType().intValue()) {
		case 1:
			return obsvDeviceDetectionDao;
		case 2:
			return obsvDeviceDetectionDao;
		case 3:
			return obsvPeopleCountDao;
		case 4:
			return obsvCarRegPlateDetectionDao;
		case 5:
			return obsvOBDDetectionDao;
		case 6:
			return obsvVoiceDecibelDetectionDao;
		case 7:
			return obsvActivityDetectionDao;
		default:
			return observationsDao;
		}
	}

	@Override
	public Response normaliseObservation(Long obsvId, String finalActivityType,
			String finalLatitude, String finalLongitude, String finalConfidence) {
		log.info(String
				.format("normalise sensor observations : obsvId [%s], finalActivityType[%s], finalLatitude[%s], finalLongitude[%s], finalConfidence[%s]",
						obsvId, finalActivityType, finalLatitude,
						finalLongitude, finalConfidence));

		if (StringUtils.isEmpty(finalActivityType)
				&& isInvalidateGeoValue(finalLatitude)
				&& isInvalidateGeoValue(finalLongitude)) {
			return badRequest(String
					.format("Nothing to be normalised! Requested finalActivityType[%s],finalLatitude[%s], finalLongitude[%s], finalConfidence[%s]",
							finalActivityType, finalLatitude, finalLongitude,
							finalConfidence));
		}

		if (!observationsDao.exists(Long.valueOf(obsvId))) {
			return badRequest(String.format("'obsvId' [%s] is not exist!",
					obsvId));
		}

		Map<String, String> normalisationValues = new HashMap<String, String>();
		normalisationValues.put("finalActivityType", finalActivityType);
		normalisationValues.put("finalLatitude", finalLatitude);
		normalisationValues.put("finalLongitude", finalLongitude);
		normalisationValues.put("finalConfidence", finalConfidence);

		generalSensorObsvProcessor.normaliseObservation(this,
				Long.valueOf(obsvId), normalisationValues);

		return Response.accepted().build();
	}

	private boolean isInvalidateGeoValue(String value) {
		return StringUtils.isEmpty(value) || !NumberUtils.isNumber(value)
				|| (Double.valueOf(value) == 0.0d);
	}
}
