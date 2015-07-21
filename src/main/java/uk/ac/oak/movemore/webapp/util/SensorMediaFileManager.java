package uk.ac.oak.movemore.webapp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SensorMediaFileManager {
	protected final Log log = LogFactory.getLog(SensorMediaFileManager.class);

	public String onSumbit(InputStream fileStream, String fileName, String devicePhysicalId,
			String sensorPhysicalId) throws IOException {		

		if (fileStream.available() == 0) {
			log.error("File is empty.");
			return "";
		}

		if (StringUtils.isEmpty(devicePhysicalId)) {
			log.error("Device Id is empty. Please provide valid device physical id to create top level directory");
			return "";
		}

		if (StringUtils.isEmpty(sensorPhysicalId)) {
			log.error("Sensor id is empty. Please provide valid sensor id for sensor media file directory.");
			return "";
		}
		
		String fileRelativePath="";

		// the directory to upload to
		String uploadDirPath = null;

		try {
			uploadDirPath = getDirectoryPath();
		} catch (IOException e) {
			log.error("Sensor Media File Directory is not found. Check the setting in config.properties");
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateDir = sdf.format(new Date());
		String fileRelativeDirectory = "/"+devicePhysicalId.trim().replaceAll(" ", "_")
				+ "/" + sensorPhysicalId.trim().replaceAll(" ", "_") + "/"
				+ dateDir + "/";
		if (uploadDirPath != null) {
			uploadDirPath += "/" + fileRelativeDirectory;
		}

		// Create the device directory if it doesn't exist
		File sensorDirPath = new File(uploadDirPath);

		if (!sensorDirPath.exists()) {
			sensorDirPath.mkdirs();
		}
		
		String fileExtension = ".unkown";
		if (StringUtils.isNotEmpty(fileName) && fileName.contains(".")) {
			fileExtension = fileName.substring(fileName.lastIndexOf("."));
		}

		String newFileName = new Date().getTime() + fileExtension;
		try {
			// write the file to the file specified
			OutputStream bos = new FileOutputStream(uploadDirPath + newFileName);
			int bytesRead;
			byte[] buffer = new byte[fileStream.available()];

			while ((bytesRead = fileStream.read(buffer, 0, 8192)) != -1) {
				bos.write(buffer, 0, bytesRead);
			}
			
			bos.close();

			// close the stream
			fileStream.close();
			
			fileRelativePath = fileRelativeDirectory + newFileName;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.error(e.toString());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			log.error(ioe.toString());
		}

		return fileRelativePath;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		File file = new File("C:\\Users\\Public\\Music\\Sample Music\\Kalimba.mp3");
		System.out.println(file.length());
		System.out.println(file.getName());
		System.out.println("file type: "+ file.getName().substring(file.getName().lastIndexOf(".")));
		
		
		SensorMediaFileManager fileManager = new SensorMediaFileManager();
		fileManager.onSumbit(new FileInputStream(file), file.getName(), "devicePhysicalId", "sensorPhysicalId");
		
//		boolean success = fileManager.delete("/devicePhysicalId/sensorPhysicalId/2014-07-17/1405615518736.mp3");
//		System.out.println("is deleted ? "+ success);
	}

	public boolean delete (String fileRelativePath) {
		boolean success = true;
		String fileAbsolutePath = "";
		try {
			fileAbsolutePath = getAbsolutePath(fileRelativePath);
		} catch (IOException ioe) {
			log.error(String.format("Fail to delete file. Unable to get file path {%s}", fileRelativePath));
			log.error(ioe.toString());
		}
		
		if (StringUtils.isEmpty(fileAbsolutePath)) {
			success = false;
		} else {
			File file = new File(fileAbsolutePath);
			success = file.delete();
		}		
		return success;
	}
	
	public String getAbsolutePath(String fileRelativePath) throws IOException {
		return getDirectoryPath() + fileRelativePath;
	}
	
	public String getDirectoryPath() throws IOException {
		String result = "";
		Properties prop = new Properties();
		String propFileName = "config.properties";

		InputStream inputStream = getClass().getClassLoader()
				.getResourceAsStream(propFileName);
		prop.load(inputStream);

		if (inputStream == null) {
			throw new FileNotFoundException("property file '" + propFileName
					+ "' not found in the classpath");
		}

		result = prop.getProperty("sensor.fileDir.path");

		return result;
	}
}
