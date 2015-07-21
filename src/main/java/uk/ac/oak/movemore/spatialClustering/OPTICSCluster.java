package uk.ac.oak.movemore.spatialClustering;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.lmu.ifi.dbs.elki.algorithm.clustering.optics.OPTICSXi;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.StaticArrayDatabase;
import de.lmu.ifi.dbs.elki.datasource.FileBasedDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.filter.FixedDBIDsFilter;
import de.lmu.ifi.dbs.elki.datasource.parser.NumberVectorLabelParser;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;
import weka.clusterers.FilteredClusterer;
import weka.clusterers.OPTICS;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.unsupervised.attribute.Remove;

/**
 * OPTICS is an extension of DBScan that provides a visualization and the
 * ability to extract hierarchies (via the visualization)
 * 
 * the visualization is the main interface to OPTICS
 * 
 * The parameter epsilon is strictly speaking not necessary. It can be set to a
 * maximum value. When a spatial index is available, it does however play a
 * practical role when it comes to complexity.
 * 
 * @author jieg
 * 
 */
public class OPTICSCluster extends Cluster {

	// two parameters determine a density threshold for clustering
	/**
	 * Specifies the radius for a range-query The maximum radius of the
	 * neighborhood to be considered.
	 */
	private double epsilon = Double.POSITIVE_INFINITY;

	/**
	 * Specifies the density (the range-query must contain at least minPoints
	 * DataObjects)
	 * 
	 * Threshold for minimum number of points in the epsilon-neighborhood of a
	 * point.
	 */
	private int minPoints = 6;

	public static void main(String[] args) {
		String filePath = "C:\\Users\\jieg\\Documents\\weka-geo-location-outlier\\wesenseit-activity-sensorData\\data\\20140918-356843053521889-Rep2Activity-data.csv";

		OPTICSCluster opticsCluster = new OPTICSCluster();
		opticsCluster.clustering(filePath, CLUSTERING_METHOD_WEKA_OPTICS);
		// opticsCluster.elkiOPTICSClustering();
	}

	@Override
	Boolean clustering(String fileAbsPath, String method) {
		if (!CLUSTERING_METHOD_WEKA_OPTICS.equalsIgnoreCase(method) &&
				!CLUSTERING_METHOD_ELKI_OPTICS.equalsIgnoreCase(method)) {
			return null;
		}
		
		CSVLoader csvLoader = new CSVLoader();

		try {
			csvLoader.setSource(new File(fileAbsPath));
			Instances dataset = csvLoader.getDataSet();

			System.out.println("instance size from csv loader:"
					+ dataset.size());

			if (CLUSTERING_METHOD_WEKA_OPTICS.equalsIgnoreCase(method)) {
				wekaOPTICSClustering(dataset, getEpsilon(), getMinPoints());
				return true;
			} else if (CLUSTERING_METHOD_ELKI_OPTICS.equalsIgnoreCase(method)) {
				// remove attributes except latitude&longitude
				Remove removeFilter = geolocAttributesRemoveFilter(dataset);

				Instances newDataset = Remove.useFilter(dataset, removeFilter);
				String preparedDataFile = fileAbsPath.replace(".csv",
						"dataForOPTICS.csv");
				exportArffInstancesToCSV(newDataset, preparedDataFile);
				System.out.println(preparedDataFile
						+ " is exported for further analysis in ELKI.");
				// Launch ELKI tool via
				// java -jar elki.jar -algorithm clustering.OPTICSXi -dbc.in
				// geodata.csv -opticsxi.xi 0.038 -optics.minpts 10
				// elkiOPTICSClustering(preparedDataFile);
				return true;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public Instances wekaOPTICSClustering(Instances dataset, double epsilon,
			int minPoints) throws Exception {
		OPTICS opticsCluster = new OPTICS();
		opticsCluster.setEpsilon(epsilon);
		opticsCluster.setMinPoints(minPoints);

		FilteredClusterer fc = wrapByGeoFilteredMakeDensityBasedClusterer(
				dataset, opticsCluster);

		fc.buildClusterer(dataset);
		// opticsCluster.clusterInstance(instance);

		return null;
	}

	public Instances elkiOPTICSClustering(String preparedDataFile) {
		NumberVectorLabelParser<DoubleVector> nvllParser = new NumberVectorLabelParser<DoubleVector>(
				DoubleVector.FACTORY);

		FileBasedDatabaseConnection fileDBConn = new FileBasedDatabaseConnection(
				null, nvllParser, preparedDataFile);

		StaticArrayDatabase db = new StaticArrayDatabase(fileDBConn, null);
		db.initialize();
		// Database db = makeSimpleDatabase(
		// "C:\\oak-project\\movemore\\lib\\geodata.csv", 315);

		ListParameterization params = new ListParameterization();
		params.addParameter(
				de.lmu.ifi.dbs.elki.algorithm.clustering.optics.OPTICS.Parameterizer.MINPTS_ID,
				getMinPoints());
		params.addParameter(OPTICSXi.Parameterizer.XI_ID, 0.038);

		OPTICSXi opticsXiCluster = ClassGenericsUtil.parameterizeOrAbort(
				OPTICSXi.class, params);

		// Relation<?> rel = db.getRelation(TypeUtil.DOUBLE_VECTOR_FIELD);
		// run OPTICS on database
		Clustering<?> clustering = opticsXiCluster.run(db);
		System.out.println(clustering);

		return null;
	}

	/**
	 * Generate a simple DoubleVector database from a file.
	 * 
	 * @param filename
	 *            File to load
	 * @param expectedSize
	 *            Expected size in records
	 * @return Database
	 */
	protected <T> Database makeSimpleDatabase(String filename, int expectedSize) {
		return makeSimpleDatabase(filename, expectedSize,
				new ListParameterization(), null);
	}

	protected <T> Database makeSimpleDatabase(String filename,
			int expectedSize, ListParameterization params, Class<?>[] filters) {
		params.addParameter(FileBasedDatabaseConnection.Parameterizer.INPUT_ID,
				filename);

		List<Class<?>> filterlist = new ArrayList<>();
		filterlist.add(FixedDBIDsFilter.class);
		// if (filters != null) {
		// for (Class<?> filter : filters) {
		// filterlist.add(filter);
		// }
		// }
		params.addParameter(
				FileBasedDatabaseConnection.Parameterizer.FILTERS_ID, null);

		// params.addParameter(FixedDBIDsFilter.Parameterizer.IDSTART_ID, 1);
		Database db = ClassGenericsUtil.parameterizeOrAbort(
				StaticArrayDatabase.class, params);

		db.initialize();

		return db;
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
