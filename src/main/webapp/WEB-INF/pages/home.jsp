<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="home.title"/></title>
    <meta name="menu" content="Home"/>
    
    <link href='https://fonts.googleapis.com/css?family=Droid+Sans:400,700' rel='stylesheet' type='text/css'/>
	<link href='styles/reset.css' media='screen' rel='stylesheet' type='text/css'/>
	<link href='styles/screen.css' media='screen' rel='stylesheet' type='text/css'/>
	<link href='styles/reset.css' media='print' rel='stylesheet' type='text/css'/>
	<link href='styles/screen.css' media='print' rel='stylesheet' type='text/css'/>
	<script type="text/javascript" src="scripts/shred.bundle.js"></script>
	<script src='scripts/jquery-1.8.0.min.js' type='text/javascript'></script>
	<script src='scripts/jquery.slideto.min.js' type='text/javascript'></script>
	<script src='scripts/jquery.wiggle.min.js' type='text/javascript'></script>
	<script src='scripts/jquery.ba-bbq.min.js' type='text/javascript'></script>
	<script src='scripts/handlebars-1.0.0.js' type='text/javascript'></script>
	<script src='scripts/underscore-min.js' type='text/javascript'></script>
	<script src='scripts/backbone-min.js' type='text/javascript'></script>
	<script src='scripts/swagger.js' type='text/javascript'></script>
	<script src='scripts/swagger-ui.js' type='text/javascript'></script>
	<script src='scripts/highlight.7.3.pack.js' type='text/javascript'></script>
	
	<!-- enabling this will enable oauth2 implicit scope support -->
    <script src='lib/swagger-oauth.js' type='text/javascript'></script>
  
  	<script type="text/javascript">
	    $(function () {
	      window.swaggerUi = new SwaggerUi({
	      /* url: "http://petstore.swagger.wordnik.com/api/api-docs",*/
	      /* Need to change to https://wesenseit-vm1.shef.ac.uk:8443/moveMore/services/json/api-docs */
		  url: "https://wesenseit-vm1.shef.ac.uk:8443/moveMore/services/json/api-docs",
	      dom_id: "swagger-ui-container",
	      supportedSubmitMethods: ['get', 'post', 'put', 'delete'],
	      onComplete: function(swaggerApi, swaggerUi){
	        log("Loaded SwaggerUI");
	
	        if(typeof initOAuth == "function") {
	          /*
	          initOAuth({
	            clientId: "your-client-id",
	            realm: "your-realms",
	            appName: "your-app-name"
	          });
	          */
	        }
	        $('pre code').each(function(i, e) {
	          hljs.highlightBlock(e)
	        });
	      },
	      onFailure: function(data) {
	        log("Unable to Load SwaggerUI");
	      },
	      docExpansion: "none"
	    });
	
	    $('#input_apiKey').change(function() {
	      var key = $('#input_apiKey')[0].value;
	      log("key: " + key);
	      if(key && key.trim() != "") {
	        log("added key " + key);
	        window.authorizations.add("key", new ApiKeyAuthorization("api_key", key, "query"));
	      }
	    })
	    window.swaggerUi.load();
	  });
	</script>
</head>

<body class="swagger-section">
	
	<div id='header'>
	  <div class="swagger-ui-wrap">
	  	<h2><fmt:message key="home.heading"/></h2>
		<p><fmt:message key="home.message"/></p>
	    <form id='api_selector'>
	    </form>
	  </div>
	</div>
	
	<div id="message-bar" class="swagger-ui-wrap">&nbsp;</div>
	<div id="swagger-ui-container" class="swagger-ui-wrap"></div>
</body>