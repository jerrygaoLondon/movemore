package uk.ac.oak.movemore.spatialClustering;

import java.io.File;
import java.io.IOException;

import weka.clusterers.DBSCAN;
import weka.clusterers.FilteredClusterer;
import weka.clusterers.MakeDensityBasedClusterer;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

/**
 * 
 * The same geo-location point dataset but in a different scale would result very
 * different
 * 
 * @author jieg
 * 
 */
public class DBSCANCluster extends Cluster {

	/**
	 * Specifies the radius for a range-query
	 */
	private double epsilon = 0.1;

	/**
	 * Specifies the density (the range-query must contain at least minPoints
	 * DataObjects)
	 */
	private int minPoints = 6;

	public static void main(String[] args) {
		String filePath = "C:\\Users\\jieg\\Documents\\weka-geo-location-outlier\\wesenseit-activity-sensorData\\data\\20140918-356843053521889-Rep2Activity-data.csv";
		DBSCANCluster dbscanCluster = new DBSCANCluster();
		dbscanCluster.clustering(filePath, CLUSTERING_METHOD_DBSCAN);
	}

	@Override
	Boolean clustering(String fileAbsPath, String method) {
		CSVLoader csvLoader = new CSVLoader();

		try {
			csvLoader.setSource(new File(fileAbsPath));
			Instances dataset = csvLoader.getDataSet();

			System.out.println("instance size from csv loader:"
					+ dataset.size());
			if (CLUSTERING_METHOD_DBSCAN.equals(method)) {
				Instances newClusteredDataset = wekaDBSCANClustering(dataset,
						getEpsilon(), getMinPoints());
				exportArffInstancesToCSV(newClusteredDataset,
						fileAbsPath.replace(".csv", "-wekaDBSCANClusters-["+getEpsilon()+"-"+getMinPoints()+".csv"));
	
				System.out
						.println("compute outlier score for each instance in the data ... ");
				Instances lofDetectedInstances = lofOutlierDetection(dataset);
				exportArffInstancesToCSV(lofDetectedInstances,
						fileAbsPath.replace(".csv", "-outlierDetection.csv"));
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Basic implementation of DBSCAN clustering algorithm that should *not* be
	 * used as a reference for runtime benchmarks: more sophisticated
	 * implementations exist! Clustering of new instances is not supported
	 * 
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public Instances wekaDBSCANClustering(Instances dataset, double epsilon,
			int minPoints) throws Exception {
		DBSCAN dbscan = new DBSCAN();
		dbscan.setEpsilon(epsilon);
		dbscan.setMinPoints(minPoints);

		FilteredClusterer fc = wrapByGeoFilteredMakeDensityBasedClusterer(
				dataset, dbscan);

		boolean isExistNoise = false;
		try {
			fc.buildClusterer(dataset);
		} catch (Exception e) {
			isExistNoise = true;
		}

		int[] clusterAssignments = populateClusterAssignments(dataset,
				(MakeDensityBasedClusterer) fc.getClusterer());

		if (!isExistNoise) {
			// log clustering statistics
			System.out.println(((DBSCAN) ((MakeDensityBasedClusterer) fc
					.getClusterer()).getClusterer()).toString());
			System.out.println(((MakeDensityBasedClusterer) fc.getClusterer())
					.toString());
		} else {
			// e.g., 20140831-356843053521889-Rep2Activity-data.csv is the
			// example of dataset containing noisy data
			// set epsilon = 0.01 to test the dataset clustered with some of
			// dataset
			System.out.println("NOISY DATASET IS DETECTED. SKIPP STATISTICS!");
		}

		Instances centroids = null;
		double[] priorProbabilities = null;
		if (!isExistNoise) {
			priorProbabilities = ((MakeDensityBasedClusterer) fc.getClusterer())
					.clusterPriors();
		}

		Instances newDataset = addAdditionalClusteringAttributes(dataset,
				clusterAssignments, centroids, priorProbabilities);
		return newDataset;
	}

	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	public int getMinPoints() {
		return minPoints;
	}

	public void setMinPoints(int minPoints) {
		this.minPoints = minPoints;
	}
}
