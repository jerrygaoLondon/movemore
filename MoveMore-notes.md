Deployment
===============================
	sudo cp /home/ac1jgx/moveMore-0.1.war /var/lib/tomcat7/webapps/moveMore.war

Usage Restrictions
===============================
registration is needed before accepting data

SSL Encrypted Access
===============================

Movemore server supports secure and encrypted access via the Secure Sockets Layer (SSL) protocol.
	
When communicating with a server using a self-signed SSL using a Java based client, this custom certificate must be known by the callee side of the communication. This can be done manually by adding the certificate to the client JVM.

- 1) Prepare the Certificate Keystore:
		/usr/bin$ /usr/bin/keytool -genkey -alias tomcat7 -keyalg RSA -keystore /home/ac1jgx/.movemore-sslcert
			password is movemore
			What is your first and last name? -> use hostname here (corresponding to the name used to generate cert for client side)
	
- 2) Edit the Tomcat Configuration File
		/var/lib/tomcat7/conf/server.xml
- 3) check https://wesenseit-vm1.shef.ac.uk:8443/
- 4) To modify the web.xml file to enable HTTPS
		Add a <security-constraint> section to cause all pages to be hosted with HTTPS.
		
		<web-app>
			...
			<security-constraint>
				<web-resource-collection>
					<web-resource-name>Viewpoint Secure URLs</web-resource-name>
						<url-pattern>/*</url-pattern>
					</web-resource-collection>
					<user-data-constraint>
						<transport-guarantee>CONFIDENTIAL</transport-guarantee>
					</user-data-constraint>
			</security-constraint>
		</web-app>
- 5) add certificate as trusted in java
		5.1) Add Trusted Keystore by running uk.ac.oak.movemore.webapp.util.InstallCert 
			uk.ac.oak.movemore.webapp.util.InstallCert wesenseit-vm1.shef.ac.uk:8443
			
			Run InstallCert.java, with your hostname "wesenseit-vm1.shef.ac.uk" and https port, and press “1” when ask for input. It will add your “wesenseit-vm1.shef.ac.uk” as a trusted keystore, and generate a file named “jssecacerts“.
			
		5.2) Verify Trusted Keystore
			Try run the InstallCert command again, the connection should be ok now.
		5.3) Copy jssecacerts
			Copy the generated “jssecacerts” file to your “$JAVA_HOME\jre\lib\security” folder.
			Run your web service client again, it should be working now.
- 6) copy "jssecacerts" to server jre security
		sudo cp /home/ac1jgx/jssecacerts /usr/lib/jvm/default-java/jre/lib/security

HTTP Compression
====================
	Movemore API supports automatic compression of API responses using the gzip algorithm.

	To enable compression of API response traffic, please include the following HTTP header with any API requests:

	Accept-encoding: gzip
	
Optimisation for large scale
======================
Tomcat server
----------------
Currently, tomcat server is configured using NIO (many more connections than threads). maxConnections defaults 10,000 and maxThread is set to 500.
see more details via http://stackoverflow.com/questions/24678661/tomcat-maxthreads-vs-maxconnections and current setting in /etc/tomcat7/server.xml.


Mysql database
----------------
Recommend to configure in my.cnf to avoid configuration loss after rebooting. Edit my.cnf in ubuntu via /etc/mysql/my.cnf

Backup the original my.cnf file and replace with the new one:
	sudo cp /etc/mysql/my.cnf /etc/mysql/my.cnf.backup
	

Good practice is introduced here (https://www.percona.com/blog/2011/01/07/high-rate-insertion-with-mysql-and-innodb/ and http://dimitrik.free.fr/blog/archives/2010/12/mysql-performance-analyzing-perconas-tpcclike-workload-on-mysql-55.html).

**max_connections**

"The maximum number of connections MySQL can support depends on the quality of the thread library on a given platform, the amount of RAM available, how much RAM is used for each connection, the workload from each connection, and the desired response time. Linux or Solaris should be able to support at 500 to 1000 simultaneous connections routinely and as many as 10,000 connections if you have many gigabytes of RAM available and the workload from each is low or the response time target undemanding. "

*Check current max_connections:*
	mysql> SHOW VARIABLES LIKE 'max_connections';
		default max_connections is 151
	
	mysql> SHOW VARIABLES LIKE 'max_user_connections';
		set max_user_connections to be 0 to disable the limits

*Set the value dynamically:*
	e.g., mysql> SET GLOBAL max_connections = 2000;

*max_heap_table_size*
*wait_timeout*
*thread_cache_size*
*tmp_table_size*
*innodb_buffer_pool_instances*


Stress Test
===============
for inserting 412055 from 30 clients running in parallel, the server did pretty well and didn't break down.

Notes: iceberg VM has restriction for Restrict the maximum number of independent IP connection on specific port at the same time (possbibly for DoS protection that blocks clients that attempt to establish too many connections).
More details see http://stackoverflow.com/questions/410616/increasing-the-maximum-number-of-tcp-ip-connections-in-linux

- Maximum socket connections can be changed via /etc/sysctl.conf by setting net.core.somaxconn
(*the somaxconn value shouldn't exceed 65535*)
	net.core.somaxconn=65535
	This can also be changed via "sudo sysctl -w net.core.somaxconn=65535" and can be checked via "sudo sysctl -a | grep somaxconn"

- JMeter
	Alternative option for stress test is to use JMeter to create a test plan.