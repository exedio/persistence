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
		<h2>Generic Backoffice for COPE</h2>
<%
	final Type[] types = new Type[]{Language.TYPE, Country.TYPE, Product.TYPE, ProductGroup.TYPE, OrderConfirmationMail.TYPE};

	for( final Iterator it = request.getParameterMap().entrySet().iterator(); it.hasNext(); )
	{
		Map.Entry entry = (Map.Entry)it.next();
		System.out.println( "param: " + entry.getKey() + " value: " + entry.getValue() );
	}
	
%>
		<form action="index.jsp" method="POST">
			<table border="1">
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
				<tr>
					<td colspan="3">
					<%
						if(request.getParameter("CREATE")!=null)
						{
							Database.theInstance.createDatabase();
							%>Database successfully created!<%
						}
						else if(request.getParameter("TEARDOWN")!= null)
						{
							Database.theInstance.tearDownDatabase();
							%>Database successfully torn down!<%
						}
						else if(request.getParameter("DROP")!=null)
						{
							Database.theInstance.dropDatabase();
							%>Database successfully dropped!<%
						}
					%>
					</td>
				</tr>
			</table>
		</form>
		
			<table border="1">
			<%
			for(Iterator i = Type.getTypes().iterator(); i.hasNext(); )
			{
				%>
				<tr>
					<td>
						<%
						final Type type = (Type)i.next();
						%>
						<a href="type.jsp?type=<%=type.getJavaClass().getName()%>">
							<%=type.getJavaClass().getName()%>
						</a>
					</td>
				</tr>
				<%
			}
			%>
			</table>
	
	</body>
</html>