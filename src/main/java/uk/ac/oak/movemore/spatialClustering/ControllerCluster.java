package uk.ac.oak.movemore.spatialClustering;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Runnable by pre-defined maven profile
 * 
 * mvn -PSpatialDataClustering -Dexec.args=""
 * 
 * <p>
 * kmeans: <br/>
 * mvn -PSpatialDataClustering
 * -Dexec.args="kmeans <filePath> <numberOfClusters>"
 * 
 * For example, mvn -PSpatialDataClustering
 * -Dexec.args="kmeans C:\\20140820-356843053521889-Rep2Activity-data.csv 10"
 * </p>
 * <p>
 * cascadeKMeans:<br>
 * mvn -PSpatialDataClustering
 * -Dexec.args="cascadeKMeans <filePath> <minNumClusters> <maxNumClusters>"<br>
 * For example, mvn -PSpatialDataClustering -Dexec.args=
 * "cascadeKMeans C:\\20140820-356843053521889-Rep2Activity-data.csv 2 20"
 * </p>
 * <p>
 * xmeans<br>
 * mvn -PSpatialDataClustering
 * -Dexec.args="xmeans <filePath> <minNumClusters> <maxNumClusters>" <br>
 * For example,For example, mvn -PSpatialDataClustering -Dexec.args=
 * "xmeans C:\\20140820-356843053521889-Rep2Activity-data.csv 2 10"
 * </p>
 * <p>
 * wekaDbscan<br>
 * mvn -PSpatialDataClustering
 * -Dexec.args="wekaDbscan <filePath> <epsilon> <minPoints>" <br>
 * 
 *  For example,For example, mvn -PSpatialDataClustering -Dexec.args=
 * "wekaDbscan C:\\20140820-356843053521889-Rep2Activity-data.csv 0.1 6"
 * </p>
 * 
 * <p>
 * wekaOPTICS<br>
 * mvn -PSpatialDataClustering
 * -Dexec.args="wekaOPTICS <filePath> <minPoints>" <br>
 * 
 *  For example,For example, mvn -PSpatialDataClustering -Dexec.args=
 * "wekaOPTICS C:\\20140820-356843053521889-Rep2Activity-data.csv 0.1 6"
 * </p>
 * 
 * @author jieg
 * 
 */
public class ControllerCluster extends Cluster {

	private Collection<Cluster> clusters = new ArrayList();

	public ControllerCluster() {
		clusters.add(new KMeansCluster());
		clusters.add(new DBSCANCluster());
		clusters.add(new OPTICSCluster());
	}

	@Override
	Boolean clustering(String fileAbsPath, String method) {
		for (Cluster spatialCluster : clusters) {
			Boolean result = spatialCluster.clustering(fileAbsPath, method);
			if (result != null) {
				return true;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("No suitable parameter provided!");
			return;
		}

		String method = args[0];
		String datafile = args[1];

		System.out.println("Clustering method:" + method);
		System.out.println("Dataset source file:" + datafile);

		if (CLUSTERING_METHOD_KMEANS.equalsIgnoreCase(method)
				|| CLUSTERING_METHOD_CASCADE_KMEANS.equalsIgnoreCase(method)
				|| CLUSTERING_METHOD_XMEANS.equalsIgnoreCase(method)) {
			KMeansCluster kmeansCluster = new KMeansCluster();

			setRuntimeParametersForKMeansClusters(args, method, kmeansCluster);

			kmeansCluster.clustering(datafile, method);
		}

		if (CLUSTERING_METHOD_DBSCAN.equalsIgnoreCase(method)) {
			DBSCANCluster dbscanCluster = new DBSCANCluster();

			setRuntimeParametersForDBSCANClusters(args, dbscanCluster);
			dbscanCluster.clustering(datafile, method);
		}

		if (CLUSTERING_METHOD_WEKA_OPTICS.equalsIgnoreCase(method)) {
			OPTICSCluster opticsCluster = new OPTICSCluster();
			if (args.length == 3) {
				if (args.length == 3) {
					String minPoints = args[2];
					opticsCluster.setMinPoints(Integer.valueOf(minPoints));
				} else {
					System.out
							.println("===No minPoints is provided for OPTICS clustering!==== ");
					System.out.println("===minPoints is default as ["
							+ opticsCluster.getMinPoints() + "]");
				}

			}
		}
	}

	private static void setRuntimeParametersForDBSCANClusters(String[] args,
			DBSCANCluster dbscanCluster) {
		if (args.length == 4) {
			String epsilon = args[2];
			String minPoints = args[3];
			dbscanCluster.setEpsilon(Double.valueOf(epsilon));
			dbscanCluster.setMinPoints(Integer.valueOf(minPoints));
		} else {
			System.out
					.println("====No 'epsilon' & 'minPoints' is provided for DBSCAN====");
			System.out.println("====The threshold for DBSCAN is default to ["
					+ dbscanCluster.getEpsilon() + ","
					+ dbscanCluster.getMinPoints() + "]====");
		}
	}

	private static void setRuntimeParametersForKMeansClusters(String[] args,
			String method, KMeansCluster kmeansCluster) {
		if (CLUSTERING_METHOD_KMEANS.equalsIgnoreCase(method)) {
			if (args.length == 3) {
				String numOfCluster = args[2];
				kmeansCluster.setKmeansNumOfCluster(Integer
						.valueOf(numOfCluster));
			} else {
				System.out
						.println("===No numberOfCluster is provided for kmeans clustering!==== ");
				System.out.println("===numOfCluster is default as ["
						+ kmeansCluster.getKmeansNumOfCluster() + "]");
			}
		}

		if (CLUSTERING_METHOD_CASCADE_KMEANS.equalsIgnoreCase(method)) {
			if (args.length == 4) {
				String minNumCluster = args[2];
				String maxNumCluster = args[3];
				kmeansCluster.setCascadeSimpleKmeansMinNumClusters(Integer
						.valueOf(minNumCluster));
				kmeansCluster.setCascadeSimpleKmeansMaxNumClusters(Integer
						.valueOf(maxNumCluster));
			} else {
				System.out
						.println("====num of cluster range is provided for cascadeKMeans====");
				System.out.println("====The range of clusters is default to ["
						+ kmeansCluster.getCascadeSimpleKmeansMinNumClusters()
						+ ","
						+ kmeansCluster.getCascadeSimpleKmeansMaxNumClusters()
						+ "]====");
			}
		}

		if (CLUSTERING_METHOD_XMEANS.equalsIgnoreCase(method)) {
			if (args.length == 4) {
				String minNumCluster = args[2];
				String maxNumCluster = args[3];

				kmeansCluster.setXmeansMinNumClusters(Integer
						.valueOf(minNumCluster));
				kmeansCluster.setXmeansMaxNumClusters(Integer
						.valueOf(maxNumCluster));
			} else {
				System.out
						.println("====num of cluster range is provided for xmeans====");
				System.out.println("====The range of clusters is default to ["
						+ kmeansCluster.getXmeansMinNumClusters() + ","
						+ kmeansCluster.getXmeansMaxNumClusters() + "]====");
			}
		}
	}
}
