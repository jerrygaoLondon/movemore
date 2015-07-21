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

	1) Prepare the Certificate Keystore:
		/usr/bin$ /usr/bin/keytool -genkey -alias tomcat7 -keyalg RSA -keystore /home/ac1jgx/.movemore-sslcert
			password is movemore
			What is your first and last name? -> use hostname here (corresponding to the name used to generate cert for client side)
	
	2) Edit the Tomcat Configuration File
		/var/lib/tomcat7/conf/server.xml
	3) check https://wesenseit-vm1.shef.ac.uk:8443/
	4) To modify the web.xml file to enable HTTPS
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
	5) add certificate as trusted in java
		5.1) Add Trusted Keystore by running uk.ac.oak.movemore.webapp.util.InstallCert 
			uk.ac.oak.movemore.webapp.util.InstallCert wesenseit-vm1.shef.ac.uk:8443
			
			Run InstallCert.java, with your hostname "wesenseit-vm1.shef.ac.uk" and https port, and press “1” when ask for input. It will add your “wesenseit-vm1.shef.ac.uk” as a trusted keystore, and generate a file named “jssecacerts“.
			
		5.2) Verify Trusted Keystore
			Try run the InstallCert command again, the connection should be ok now.
		5.3) Copy jssecacerts
			Copy the generated “jssecacerts” file to your “$JAVA_HOME\jre\lib\security” folder.
			Run your web service client again, it should be working now.
	6) copy "jssecacerts" to server jre security
		sudo cp /home/ac1jgx/jssecacerts /usr/lib/jvm/default-java/jre/lib/security

HTTP Compression
====================
	Movemore API supports automatic compression of API responses using the gzip algorithm.

	To enable compression of API response traffic, please include the following HTTP header with any API requests:

	Accept-encoding: gzip