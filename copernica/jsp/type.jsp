<%@ page import="com.exedio.demoshop.*" %>
<%@ page import="com.exedio.cope.lib.Database" %>
<%@ page import="com.exedio.cope.lib.Type" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>

<html>
	<head>	
		<title>
			Copernica
		</title>
	</head>
	<body>
		<h1>Copernica</h1>
<%
	for( final Iterator it = request.getParameterMap().entrySet().iterator(); it.hasNext(); )
	{
		Map.Entry entry = (Map.Entry)it.next();
		System.out.println( "param: " + entry.getKey() + " value: " + entry.getValue() );
	}
	
	final String typeName = request.getParameter("type");
	final Type type = Type.getType(typeName);
	if(type==null)
		throw new RuntimeException("type "+typeName+" not available");
%>
		<h2><%=type.getJavaClass().getName()%></h2>
	</body>
</html>