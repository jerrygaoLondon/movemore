package uk.ac.oak.movemore.webapp.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.com.bytecode.opencsv.CSVReader;
import lt.overdrive.trackparser.parsing.gpx.schema.GpxType;
import lt.overdrive.trackparser.parsing.gpx.schema.MetadataType;
import lt.overdrive.trackparser.parsing.gpx.schema.TrkType;
import lt.overdrive.trackparser.parsing.gpx.schema.TrksegType;
import lt.overdrive.trackparser.parsing.gpx.schema.WptType;
import lt.overdrive.trackparser.parsing.tcx.schema.ActivityLapT;
import lt.overdrive.trackparser.parsing.tcx.schema.ActivityListT;
import lt.overdrive.trackparser.parsing.tcx.schema.ActivityT;
import lt.overdrive.trackparser.parsing.tcx.schema.ObjectFactory;
import lt.overdrive.trackparser.parsing.tcx.schema.PositionT;
import lt.overdrive.trackparser.parsing.tcx.schema.SportT;
import lt.overdrive.trackparser.parsing.tcx.schema.TPXT;
import lt.overdrive.trackparser.parsing.tcx.schema.TPXTExtensionsT;
import lt.overdrive.trackparser.parsing.tcx.schema.TrackT;
import lt.overdrive.trackparser.parsing.tcx.schema.TrackpointT;
import lt.overdrive.trackparser.parsing.tcx.schema.TrainingCenterDatabaseT;

public class TestExportTCXFile {
	protected final static Log log = LogFactory.getLog(TestExportTCXFile.class);

	public static void main(String[] args) throws ParseException {
		String fileAbsPath = "C:\\Users\\jieg\\Documents\\weka-geo-location-outlier\\"
				+ "wesenseit-activity-sensorData\\data\\"
				+ "20140918-356843053521889-Rep2Activity-data.csv";
		File file = new File(fileAbsPath);
		List<String[]> records = loadSensorActivityData(fileAbsPath);
		convertToGPX(records, file.getName().replace("csv", "gpx"));
	}

	public static void convertToGPX(List<String[]> records, String exportFileName) throws ParseException {
		Date startTime = getStartTime(records);
		String sensorId = getSensorId(records);
		
		lt.overdrive.trackparser.parsing.gpx.schema.ObjectFactory gpxFactory = new lt.overdrive.trackparser.parsing.gpx.schema.ObjectFactory();
		GpxType gpxType = gpxFactory.createGpxType();
		
		MetadataType metadataType = new MetadataType();
		metadataType.setName(sensorId);
		metadataType.setTime(toXMLGregorianCalendar(startTime));
		
		gpxType.setMetadata(metadataType);
		
		TrkType track = new TrkType();
		track.setName(startTime.toString());
		TrksegType segmentType = new TrksegType();
		int i=0;
		for(String[] raw : records) {
			if (i == 0) {
				i++;
				continue;
			}
			
			Date obsvTime = toDate(raw[5]);
			Double latitude = Double.valueOf(raw[3]);
			Double longitude = Double.valueOf(raw[4]);
//			Double speed = Double.valueOf(raw[9]);
			
			WptType wptType = new WptType();
			wptType.setLat(BigDecimal.valueOf(latitude));
			wptType.setLon(BigDecimal.valueOf(longitude));
			wptType.setTime(toXMLGregorianCalendar(obsvTime));
			
			segmentType.getTrkpt().add(wptType);
		}
		track.getTrkseg().add(segmentType);
				
		gpxType.getTrk().add(track);
		
		JAXBElement<GpxType> xml = gpxFactory.createGpx(gpxType);
		exportToFile(
				xml,
				"C:\\Users\\jieg\\Documents\\weka-geo-location-outlier\\wesenseit-activity-sensorData\\"+exportFileName,
				GpxType.class);

	}
	
	private static void convertToTCX(List<String[]> records)
			throws ParseException {
		Date startTime = getStartTime(records);
		
		TrainingCenterDatabaseT trainingDB = new TrainingCenterDatabaseT();

		ActivityListT activityList = new ActivityListT();

		ActivityT activity = new ActivityT();
		String activityType = "Running";
		activity.setSport(SportT.fromValue(activityType));
		activity.setId(toXMLGregorianCalendar(startTime));
		// List<ActivityLapT> laps = new LinkedList<ActivityLapT>();
		ActivityLapT activityLap = new ActivityLapT();
		activityLap.setStartTime(toXMLGregorianCalendar(startTime));

		TrackT trackT = new TrackT();
		
		int i=0;
		for(String[] raw : records) {
			if (i == 0) {
				i++;
				continue;
			}
			i++;
			//start: Recursively add track point	
			TrackpointT trackPointT = new TrackpointT();
			
			Date obsvTime = toDate(raw[5]);
			Double latitude = Double.valueOf(raw[3]);
			Double longitude = Double.valueOf(raw[4]);
			Double speed = Double.valueOf(raw[9]);
			
			trackPointT.setTime(toXMLGregorianCalendar(obsvTime));
			PositionT positionT = new PositionT();
			positionT.setLatitudeDegrees(latitude);
			positionT.setLongitudeDegrees(longitude);
			trackPointT.setPosition(positionT);

			TPXTExtensionsT param = new TPXTExtensionsT();
			TPXT tpxT = new TPXT();
			tpxT.setSpeed(speed);
			param.setTpxt(tpxT);
			trackPointT.setExtensions(param);
			trackT.getTrackpoint().add(trackPointT);
			//end: Recursively add track point
		}

		activityLap.getTrack().add(trackT);
		
		// laps.add(activityLap);

		activity.getLap().add(activityLap);
		activityList.getActivity().add(activity);

		trainingDB.setActivities(activityList);	
		
		ObjectFactory ofactory = new ObjectFactory();
		JAXBElement<TrainingCenterDatabaseT> element = ofactory
				.createTrainingCenterDatabase(trainingDB);
		exportToFile(
				element,
				"C:\\Users\\jieg\\Documents\\weka-geo-location-outlier\\wesenseit-activity-sensorData\\testTCXFileExport.tcx",
				TrainingCenterDatabaseT.class);
	}
	
	public static List<String[]> loadSensorActivityData(String fileAbsPath) {
		try {
			CSVReader csvReader = new CSVReader(new FileReader(fileAbsPath));
		
			List<String[]> records = csvReader.readAll();

//			for (String[] raw : records) {
//				System.out.println("activity type:"+raw[0]);
//				System.out.println("confidence:"+raw[1]);
//				System.out.println("latitude:"+raw[3]);
//				System.out.println("longitude:"+raw[4]);
//				System.out.println("obsv time:"+raw[5]);
//			}
			
			return records;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Date getStartTime(List<String[]> records) {
		if (records == null) {
			return null;
		}
		
		String[] firstRaw = records.get(1);
		String obsvTime = firstRaw[5];
		
		Date date = null;
		try {
			date = toDate(obsvTime);			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static String getSensorId(List<String[]> records) {
		if (records == null) {
			return null;
		}
		
		String[] firstRaw = records.get(1);
		return firstRaw[11];
	}
	
	public static Date toDate(String time) throws ParseException {
		//2014-09-18 07:02:42
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
	}

	public static void exportToFile(
			JAXBElement<? extends Object> element, String filePath, Class... classesToBeBound) {
		File file = new File(filePath);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(classesToBeBound);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(element, file);
			jaxbMarshaller.marshal(element, System.out);

		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Converts java.util.Date to javax.xml.datatype.XMLGregorianCalendar
	 */
	public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
		GregorianCalendar gCalendar = new GregorianCalendar();
		gCalendar.setTime(date);
		XMLGregorianCalendar xmlCalendar = null;
		try {
			xmlCalendar = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(gCalendar);
		} catch (DatatypeConfigurationException ex) {
			log.error(ex);
		}
		return xmlCalendar;
	}

}
