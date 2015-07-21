package uk.ac.oak.movemore.spatialClustering;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import weka.clusterers.AbstractClusterer;
import weka.clusterers.CascadeSimpleKMeans;
import weka.clusterers.FilteredClusterer;
import weka.clusterers.MakeDensityBasedClusterer;
import weka.clusterers.forOPTICSAndDBScan.DataObjects.DataObject;
import weka.core.DenseInstance;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.CSVSaver;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.Add;
import weka.filters.unsupervised.attribute.LOF;
import weka.filters.unsupervised.attribute.Remove;

public abstract class Cluster {

	public static final String CLUSTERING_METHOD_KMEANS = "kmeans";
	public static final String CLUSTERING_METHOD_CASCADE_KMEANS = "cascadeKMeans";
	public static final String CLUSTERING_METHOD_XMEANS = "xmeans";
	public static final String CLUSTERING_METHOD_DBSCAN = "wekaDbscan";
	public static final String CLUSTERING_METHOD_WEKA_OPTICS = "wekaOPTICS";
	public static final String CLUSTERING_METHOD_ELKI_OPTICS = "elkiOPTICS";
	
	abstract Boolean clustering(String fileAbsPath, String method);
	
	protected Instance convertOriginalInstToGeoInst(int latAttrNum,
			int longAttrNum, Instance currentInst) {
		double inst_lat = currentInst.value(latAttrNum);
		double inst_long = currentInst.value(longAttrNum);

		double[] currentGeoValue = new double[2];
		currentGeoValue[0] = inst_lat;
		currentGeoValue[1] = inst_long;
		Instance geoInst = new DenseInstance(1.0, currentGeoValue);
		return geoInst;
	}

	/**
	 * Filter to remove all the other attributes other than 'latitude' and
	 * 'longitude'
	 * 
	 * @param dataset
	 * @return Remove filter
	 * @throws Exception
	 */
	protected Remove geolocAttributesRemoveFilter(Instances dataset)
			throws Exception {
		if (dataset.attribute("latitude") == null) {
			throw new Exception("'latitude' attribute is not found!");
		}
		if (dataset.attribute("longitude") == null) {
			throw new Exception("'longitude' attribute is not found!");
		}

		int latAttrNum = dataset.attribute("latitude").index() + 1;
		int longAttrNum = dataset.attribute("longitude").index() + 1;

		String[] options = new String[2];
		StringBuffer columnIndexsToFilter = new StringBuffer();
		for (int numAttr = 1; numAttr <= dataset.numAttributes(); numAttr++) {
			if (numAttr != latAttrNum && numAttr != longAttrNum) {
				columnIndexsToFilter.append(numAttr);
				if (numAttr != dataset.numAttributes()) {
					columnIndexsToFilter.append(",");
				}
			}

		}

		System.out.println("latitude attribute no [" + latAttrNum
				+ "], longitude attribute no [" + longAttrNum + "]");
		System.out.println("Ignore attributes ["
				+ columnIndexsToFilter.toString() + "]");
		// "range"
		options[0] = "-R";
		// we want to ignore the other attributes except latitude and longitude
		options[1] = columnIndexsToFilter.toString();

		// new instance of filter
		Remove remove = new Remove();
		// set options
		remove.setOptions(options);
		remove.setInputFormat(dataset);
		return remove;
	}

	/**
	 * A filter that applies the LOF(Local Outlier Factor) algorithm to compute
	 * an "outlier" score for each instance in the data For more information,
	 * see:<br/>
	 * <br/>
	 * Markus M. Breunig, Hans-Peter Kriegel, Raymond T. Ng, Jorg Sander (2000).
	 * LOF: Identifying Density-Based Local Outliers. ACM SIGMOD Record.
	 * 29(2):93-104.
	 * <p/>
	 * 
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	protected Instances lofOutlierDetection(Instances dataset) throws Exception {
		MultiFilter multiFilter = new MultiFilter();

		Remove remove = geolocAttributesRemoveFilter(dataset);

		LOF localOutlierFactorFilter = new LOF();
		localOutlierFactorFilter.setInputFormat(dataset);

		Filter[] filters = new Filter[2];
		filters[0] = remove;
		filters[1] = localOutlierFactorFilter;
		multiFilter.setFilters(filters);
		multiFilter.setInputFormat(dataset);

		Instances lofInstances = MultiFilter.useFilter(dataset, multiFilter);

		return lofInstances;
	}

	/**
	 * Export weka instances to csv file by pre-configured output file path
	 * config property value in config.properties
	 * 
	 * @param dataset
	 * @param outputFileConfig
	 * @throws IOException
	 */
	public void exportArffInstancesToCSV(Instances dataset,
			String outputFileAbsPath) throws IOException {

		String fileAbsPath = outputFileAbsPath;

		File outputFile = new File(fileAbsPath);

		if (!outputFile.exists()) {
			outputFile.createNewFile();
		}

		CSVSaver csvSaver = new CSVSaver();
		csvSaver.setDestination(outputFile);
		csvSaver.setFile(outputFile);

		csvSaver.setInstances(dataset);

		csvSaver.writeBatch();

		System.out.println("Success: Export current instances to ["
				+ fileAbsPath + "]");
	}

	/**
	 * Add new attribute with a ordered list of values into a given dataset
	 * 
	 * @param dataset
	 * @param attributeName
	 * @param values
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	protected Instances addNewAttributes(Instances dataset,
			String attributeName, LinkedList<String> values) throws Exception,
			IOException {
		Instances newDataset = new Instances(dataset);

		// add a new attribute
		Add addNewAttribute = new Add();
		// add new attribute to end
		addNewAttribute.setAttributeType(new SelectedTag(2, Add.TAGS_TYPE));
		addNewAttribute.setAttributeIndex("last");
		addNewAttribute.setAttributeName(attributeName);
		addNewAttribute.setInputFormat(newDataset);

		newDataset = Add.useFilter(dataset, addNewAttribute);

		for (int i = 0; i < values.size(); i++) {
			newDataset.instance(i).setValue(newDataset.numAttributes() - 1,
					values.get(i));
		}
		return newDataset;
	}

	/**
	 * Cluster centroids are the mean vectors for each cluster.
	 * 
	 * This function is to iterate dataset to map original instance and the
	 * centroid instance result with minimum (Euclidean) distance
	 * 
	 * 
	 * @param dataset
	 * @param centroidInst
	 * @return
	 */
	public Instance mapCentroidInstances(Instances dataset,
			Instance centroidInst) {
		int latAttrNum = dataset.attribute("latitude").index() + 1;
		int longAttrNum = dataset.attribute("longitude").index() + 1;

		Iterator<Instance> datasetIter = dataset.iterator();
		double minDistance = 0.0;
		Instance originalCentroidInstance = null;

		int i = 0;
		while (datasetIter.hasNext()) {
			Instance currentInst = datasetIter.next();

			Instance geoInst = convertOriginalInstToGeoInst(latAttrNum,
					longAttrNum, currentInst);

			EuclideanDistance ed = new EuclideanDistance();
			double inst_distance = ed.distance(geoInst, centroidInst);
			if (i == 0) {
				minDistance = inst_distance;
				originalCentroidInstance = currentInst;
			}
			// find a minimum distance instance
			if (minDistance > inst_distance) {
				minDistance = inst_distance;
				originalCentroidInstance = currentInst;
			}

			i++;
		}

		return originalCentroidInstance;
	}

	/**
	 * wrap any of weka cluster with MakeDensityBasedClusterer and
	 * FilteredClusterer
	 * 
	 * FilteredClusterer will enable to ignore any attributes other than
	 * geo-attributes (i.e., 'latitude' and 'longitude')
	 * 
	 * The function will also build the clustering model by default
	 * 
	 * @param dataset
	 * @param kmeans
	 * @return FilteredClusterer
	 * @throws Exception
	 */
	protected FilteredClusterer wrapByGeoFilteredMakeDensityBasedClusterer(
			Instances dataset, AbstractClusterer cluster) throws Exception {
		FilteredClusterer fc = new FilteredClusterer();
		// Contructs a MakeDensityBasedClusterer wrapping a kmeans Clusterer.
		MakeDensityBasedClusterer mdbClusterer = new MakeDensityBasedClusterer(
				cluster);

		Remove remove = geolocAttributesRemoveFilter(dataset);
		fc.setFilter(remove);
		fc.setClusterer(mdbClusterer);

		// build the model
//		fc.buildClusterer(dataset);
		return fc;
	}
	
	protected FilteredClusterer wrapByGeoFilteredClusterer(
			Instances dataset, AbstractClusterer cluster) throws Exception {
		FilteredClusterer fc = new FilteredClusterer();
		
		Remove remove = geolocAttributesRemoveFilter(dataset);
		fc.setFilter(remove);
		fc.setClusterer(cluster);

		// build the model
//		fc.buildClusterer(dataset);
		return fc;
	}

	protected Instances addAdditionalClusteringAttributes(Instances dataset,
			int[] clusterAssignments, Instances centroids,
			double[] priorProbabilities) throws Exception {
		LinkedList<String> clusterValues = new LinkedList<String>();
		LinkedList<String> clusterCentroidsValues = new LinkedList<String>();
		LinkedList<String> priorProbabilityValues = new LinkedList<String>();

		for (int instNum = 0; instNum < clusterAssignments.length; instNum++) {
			int clusterNum = clusterAssignments[instNum];
			if (DataObject.NOISE == clusterNum) {
				clusterValues.add("NOISE");
			} else {
				clusterValues.add("cluster" + clusterNum);
			}
			if (centroids != null) {
				clusterCentroidsValues.add(centroids.get(clusterNum)
						.toStringNoWeight());
			}
			if (priorProbabilities!=null) {
				priorProbabilityValues.add(String
						.valueOf(priorProbabilities[clusterNum]));
			}
		}

		Instances newDataset = addNewAttributes(dataset, "cluster",
				clusterValues);

		newDataset = addNewAttributes(newDataset, "centroid",
				clusterCentroidsValues);
		newDataset = addNewAttributes(newDataset, "priorProbability",
				priorProbabilityValues);
		return newDataset;
	}

	protected int[] populateClusterAssignments(Instances dataset, MakeDensityBasedClusterer mdbcluster)
			throws Exception {
				int[] clusterAssignments = new int[dataset.size()];
				int latAttrNum = dataset.attribute("latitude").index() + 1;
				int longAttrNum = dataset.attribute("longitude").index() + 1;
			
				for (int i = 0; i < dataset.size(); i++) {
					Instance currentInst = dataset.get(i);
					Instance geoInstForClustering = convertOriginalInstToGeoInst(
							latAttrNum, longAttrNum, currentInst);
					try {
						clusterAssignments[i] = mdbcluster.getClusterer().clusterInstance(
								geoInstForClustering);
					} catch (Exception e) {
						//NOISE
						clusterAssignments[i] = DataObject.NOISE;
					}
				}
				return clusterAssignments;
			}
	
	protected int[] populateClusterAssignments(Instances dataset, CascadeSimpleKMeans cascadeKMeansCluster)
			throws Exception {
				int[] clusterAssignments = new int[dataset.size()];
				int latAttrNum = dataset.attribute("latitude").index() + 1;
				int longAttrNum = dataset.attribute("longitude").index() + 1;
			
				for (int i = 0; i < dataset.size(); i++) {
					Instance currentInst = dataset.get(i);
					Instance geoInstForClustering = convertOriginalInstToGeoInst(
							latAttrNum, longAttrNum, currentInst);
					try {
						clusterAssignments[i] = cascadeKMeansCluster.clusterInstance(
								geoInstForClustering);
					} catch (Exception e) {
						//NOISE
						clusterAssignments[i] = DataObject.NOISE;
					}
				}
				return clusterAssignments;
			}

}
