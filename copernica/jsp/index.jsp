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
	final Type[] types = new Type[]{Language.TYPE, Country.TYPE, Product.TYPE, ProductGroup.TYPE, OrderConfirmationMail.TYPE};

	for( final Iterator it = request.getParameterMap().entrySet().iterator(); it.hasNext(); )
	{
		Map.Entry entry = (Map.Entry)it.next();
		System.out.println( "param: " + entry.getKey() + " value: " + entry.getValue() );
	}
	
	final String createAction = request.getParameter( "CREATE" );
	final String tearDownAction = request.getParameter( "TEARDOWN" );
	final String dropAction = request.getParameter( "DROP" );
	
	if( createAction != null || tearDownAction != null || dropAction != null )
	{
%>
	<%
		if( createAction != null )
		{
			Database.theInstance.createDatabase();
		%>
		Database successfully created!
		<%
		}
		else if( tearDownAction != null )
		{
		Database.theInstance.tearDownDatabase();
		%>
		Database successfully torn down!
		<%
		}
		else if( dropAction != null )
		{
			Database.theInstance.dropDatabase();
		%>
		Database successfully dropped!
		<%
		}
	}
%>
		<form action="index.jsp" method="POST">
			<table>
				<tr>
					<td>
						<input type="submit" name="CREATE" value="create database" />
					</td>
				</tr>
				<tr>
					<td>
						<input type="submit" name="TEARDOWN" value="tear down database"/>
					</td>
				</tr>
				<tr>
					<td>
						<input type="submit" name="DROP" value="drop database"/>
					</td>
				</tr>
			</table>
		</form>
	</body>
</html>