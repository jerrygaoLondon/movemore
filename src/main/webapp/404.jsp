<%@ include file="/common/taglibs.jsp"%>

<page:applyDecorator name="default">

<head>
    <title><fmt:message key="404.title"/></title>
    <meta name="heading" content="<fmt:message key='404.title'/>"/>
</head>

<p>
    <fmt:message key="404.message">
        <fmt:param><c:url value="/home"/></fmt:param>
    </fmt:message>
</p>
<p style="text-align: center">
    <a href="http://oak.dcs.shef.ac.uk/" title="The OAK Group">
      <img src="http://oak.dcs.shef.ac.uk/wp-content/uploads/2013/03/OAKGroup-2012f.jpg" width="640" height="426" 
      alt="The OAK Group" style="margin: 20px; border: 1px solid black"></a>
</p>
</page:applyDecorator>