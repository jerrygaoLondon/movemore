package uk.ac.oak.movemore.spatialClustering;

import java.io.File;
import java.io.IOException;

import weka.clusterers.CascadeSimpleKMeans;
import weka.clusterers.FilteredClusterer;
import weka.clusterers.MakeDensityBasedClusterer;
import weka.clusterers.SimpleKMeans;
import weka.clusterers.XMeans;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.CSVLoader;
import weka.filters.unsupervised.attribute.AddCluster;

/**
 * 
 * XMeans and Cascade can help to determine the optimal K
 * 
 * visual inspection is required to further verify
 * 
 * @author jieg
 * 
 */
public class KMeansCluster extends Cluster {

	public int cascadeSimpleKmeansMaxNumClusters = 20;
	public int cascadeSimpleKmeansMinNumClusters = 2;
	public int kmeansNumOfCluster = 10;
	public int xmeansMaxNumClusters = 10;
	public int xmeansMinNumClusters = 2;

	public Boolean clustering(String fileAbsPath, String method) {
		if (!CLUSTERING_METHOD_KMEANS.equalsIgnoreCase(method)
				& !CLUSTERING_METHOD_CASCADE_KMEANS.equalsIgnoreCase(method)
				& !CLUSTERING_METHOD_XMEANS.equalsIgnoreCase(method)) {
			return null;
		}
		// CSVReader csvReader;
		CSVLoader csvLoader = new CSVLoader();
		try {
			csvLoader.setSource(new File(fileAbsPath));
			Instances inst = csvLoader.getDataSet();
			System.out.println("instance size from csv loader:" + inst.size());

			Instances newClusteredDataset = null;
			String exportFile;
			if (CLUSTERING_METHOD_KMEANS.equalsIgnoreCase(method)) {
				newClusteredDataset = simpleKMeansClustering(inst,
						getKmeansNumOfCluster(), fileAbsPath);
				exportFile = fileAbsPath.replace(".csv",
						"-kmeansClusters-numOfCluster["
								+ getKmeansNumOfCluster() + "].csv");
				exportArffInstancesToCSV(newClusteredDataset, exportFile);
				exportOutlierDetection(fileAbsPath, inst);
				return true;
			} else if (CLUSTERING_METHOD_CASCADE_KMEANS
					.equalsIgnoreCase(method)) {
				newClusteredDataset = cascadeSimpleKmeansClustering(inst);
				exportFile = fileAbsPath.replace(".csv",
						"-cascadeKmeansClusters-rangeOfCluster["
								+ getCascadeSimpleKmeansMinNumClusters() + ","
								+ getCascadeSimpleKmeansMaxNumClusters()
								+ "].csv");
				exportArffInstancesToCSV(newClusteredDataset, exportFile);
				exportOutlierDetection(fileAbsPath, inst);
				return true;
			} else if (CLUSTERING_METHOD_XMEANS.equalsIgnoreCase(method)) {
				exportFile = fileAbsPath.replace(".csv",
						"-xmeansClusters-rangeOfCluster["
								+ getXmeansMinNumClusters() + ","
								+ getXmeansMaxNumClusters() + "].csv");
				newClusteredDataset = xmeansClustering(inst);
				exportArffInstancesToCSV(newClusteredDataset, exportFile);
				exportOutlierDetection(fileAbsPath, inst);
				return true;
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void exportOutlierDetection(String fileAbsPath, Instances inst)
			throws Exception, IOException {
		System.out
				.println("compute outlier score for each instance in the data ... ");
		Instances lofDetectedInstances = lofOutlierDetection(inst);
		exportArffInstancesToCSV(lofDetectedInstances,
				fileAbsPath.replace(".csv", "-outlierDetection.csv"));
	}

	/**
	 * Cascade simple k means, selects the best k according to calinski-harabasz
	 * criterion. Initialize using the k-means++ method. Distance function to
	 * use. (default: weka.core.EuclideanDistance)
	 * 
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public Instances cascadeSimpleKmeansClustering(Instances dataset)
			throws Exception {
		// AddCluster addClusterFilter = new AddCluster();

		CascadeSimpleKMeans cascadeSimpleKMeans = new CascadeSimpleKMeans();

		cascadeSimpleKMeans.setDistanceFunction(new EuclideanDistance());
		cascadeSimpleKMeans.setManuallySelectNumClusters(false);
		// whether to initialize using the probabilistic farthest first like
		// method of the k-means++ algorithm (rather than
		// the standard random selection of initial cluster centers).
		cascadeSimpleKMeans.setInitializeUsingKMeansPlusPlusMethod(true);
		cascadeSimpleKMeans.setMaxIterations(500);
		cascadeSimpleKMeans
				.setMaxNumClusters(getCascadeSimpleKmeansMaxNumClusters());
		cascadeSimpleKMeans
				.setMinNumClusters(getCascadeSimpleKmeansMinNumClusters());
		cascadeSimpleKMeans.setRestarts(10);
		cascadeSimpleKMeans.setSeed(2);

		//not wrapped by MakeDensityBasedClusterer as the optimal K makes no sense
//		FilteredClusterer fc = wrapByGeoFilteredClusterer(dataset,
//				cascadeSimpleKMeans);
		FilteredClusterer fc = wrapByGeoFilteredMakeDensityBasedClusterer(dataset,
				cascadeSimpleKMeans);
		fc.buildClusterer(dataset);

		// log clustering statistics
		 System.out
		 .println(((CascadeSimpleKMeans) ((MakeDensityBasedClusterer) fc
		 .getClusterer()).getClusterer()).toString());
		 System.out.println(((MakeDensityBasedClusterer) fc.getClusterer())
		 .toString());

//		System.out
//				.println(((CascadeSimpleKMeans) fc.getClusterer()).toString());

		int[] clusterAssignments = populateClusterAssignments(dataset,
				(CascadeSimpleKMeans) ((MakeDensityBasedClusterer) fc
						 .getClusterer()).getClusterer());

		Instances centroids = null;

		double[] priorProbabilities = ((MakeDensityBasedClusterer) fc
				.getClusterer()).clusterPriors();
//		double[] priorProbabilities = null;

		Instances newDataset = addAdditionalClusteringAttributes(dataset,
				clusterAssignments, centroids, priorProbabilities);

		return newDataset;
	}

	/**
	 * X-Means clustering : a new algorithm that quickly estimates K
	 * 
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public Instances xmeansClustering(Instances dataset) throws Exception {
		XMeans xmeans = new XMeans();

		xmeans.setMaxKMeans(getXmeansMinNumClusters());
		xmeans.setMinNumClusters(getXmeansMinNumClusters());
		// according to p137 in Hamerly, G. (2010). Making k-means Even Faster.
		// In SDM (pp. 130-140).
		// kd-tree can be used for k means algorithm speed up for
		// low-dimensional data
		// With high-dimensional data, indexing schemes such as k-d tree do not
		// work well
		xmeans.setUseKDTree(true);

		FilteredClusterer fc = wrapByGeoFilteredMakeDensityBasedClusterer(
				dataset, xmeans);
		fc.buildClusterer(dataset);
		// log clustering statistics
		System.out.println(((XMeans) ((MakeDensityBasedClusterer) fc
				.getClusterer()).getClusterer()).toString());
		System.out.println(((MakeDensityBasedClusterer) fc.getClusterer())
				.toString());

		int[] clusterAssignments = populateClusterAssignments(dataset,
				(MakeDensityBasedClusterer) fc.getClusterer());

		Instances centroids = ((XMeans) ((MakeDensityBasedClusterer) fc
				.getClusterer()).getClusterer()).getClusterCenters();
		double[] priorProbabilities = ((MakeDensityBasedClusterer) fc
				.getClusterer()).clusterPriors();

		Instances newDataset = addAdditionalClusteringAttributes(dataset,
				clusterAssignments, centroids, priorProbabilities);

		return newDataset;
	}

	public Instances simpleKmeansClusteringAddCluster(Instances inst, int k)
			throws Exception {
		AddCluster addClusterFilter = new AddCluster();

		SimpleKMeans kmeans = new SimpleKMeans();
		kmeans.setSeed(10);
		kmeans.setPreserveInstancesOrder(true);
		kmeans.setNumClusters(k);
		// k-means++
		kmeans.setInitializationMethod(new SelectedTag(1,
				SimpleKMeans.TAGS_SELECTION));
		kmeans.setNumClusters(k);

		addClusterFilter.setIgnoredAttributeIndices("1,2,3,6,7,8,9,10,11,12");
		addClusterFilter.setClusterer(kmeans);

		addClusterFilter.setInputFormat(inst);
		Instances newClusteredDataset = AddCluster.useFilter(inst,
				addClusterFilter);
		return newClusteredDataset;
	}

	/**
	 * cluster dataset with cluster assignments, centroids (and probability)
	 * 
	 * The result shows the centroid of each cluster as well as statistics on
	 * the number and percentage of instances assigned to different clusters.
	 * Cluster centroids are the mean vectors for each cluster (so, each
	 * dimension value in the centroid represents the mean value for that
	 * dimension in the cluster) Thus, centroids can be used to characterize the
	 * clusters.
	 * 
	 * @param dataset
	 * @param k
	 * @return
	 * @throws Exception
	 */
	public Instances simpleKMeansClustering(Instances dataset, int k,
			String filePath) throws Exception {

		SimpleKMeans kmeans = new SimpleKMeans();
		kmeans.setSeed(10);
		kmeans.setPreserveInstancesOrder(true);
		kmeans.setNumClusters(k);
		// k-means++
		kmeans.setInitializationMethod(new SelectedTag(1,
				SimpleKMeans.TAGS_SELECTION));
		kmeans.setNumClusters(k);

		FilteredClusterer fc = wrapByGeoFilteredMakeDensityBasedClusterer(
				dataset, kmeans);
		fc.buildClusterer(dataset);

		// log clustering statistics
		System.out.println(((SimpleKMeans) ((MakeDensityBasedClusterer) fc
				.getClusterer()).getClusterer()).toString());
		System.out.println(((MakeDensityBasedClusterer) fc.getClusterer())
				.toString());

		int[] clusterAssignments = ((SimpleKMeans) ((MakeDensityBasedClusterer) fc
				.getClusterer()).getClusterer()).getAssignments();
		Instances centroids = ((SimpleKMeans) ((MakeDensityBasedClusterer) fc
				.getClusterer()).getClusterer()).getClusterCentroids();
		double[] priorProbabilities = ((MakeDensityBasedClusterer) fc
				.getClusterer()).clusterPriors();

		Instances newDataset = addAdditionalClusteringAttributes(dataset,
				clusterAssignments, centroids, priorProbabilities);

		return newDataset;
	}

	public int getCascadeSimpleKmeansMaxNumClusters() {
		return cascadeSimpleKmeansMaxNumClusters;
	}

	public void setCascadeSimpleKmeansMaxNumClusters(
			int cascadeSimpleKmeansMaxNumClusters) {
		this.cascadeSimpleKmeansMaxNumClusters = cascadeSimpleKmeansMaxNumClusters;
	}

	public int getCascadeSimpleKmeansMinNumClusters() {
		return cascadeSimpleKmeansMinNumClusters;
	}

	public void setCascadeSimpleKmeansMinNumClusters(
			int cascadeSimpleKmeansMinNumClusters) {
		this.cascadeSimpleKmeansMinNumClusters = cascadeSimpleKmeansMinNumClusters;
	}

	public int getKmeansNumOfCluster() {
		return kmeansNumOfCluster;
	}

	public void setKmeansNumOfCluster(int kmeansNumOfCluster) {
		this.kmeansNumOfCluster = kmeansNumOfCluster;
	}

	public int getXmeansMaxNumClusters() {
		return xmeansMaxNumClusters;
	}

	public void setXmeansMaxNumClusters(int xmeansMaxNumClusters) {
		this.xmeansMaxNumClusters = xmeansMaxNumClusters;
	}

	public int getXmeansMinNumClusters() {
		return xmeansMinNumClusters;
	}

	public void setXmeansMinNumClusters(int xmeansMinNumClusters) {
		this.xmeansMinNumClusters = xmeansMinNumClusters;
	}

	public static void main(String[] args) {
		String filePath = "C:\\Users\\jieg\\Documents\\weka-geo-location-outlier\\wesenseit-activity-sensorData\\data\\20140820-356843053521889-Rep2Activity-data.csv";
		Cluster kmeansCluster = new KMeansCluster();
		kmeansCluster.clustering(filePath, CLUSTERING_METHOD_CASCADE_KMEANS);
	}
}
