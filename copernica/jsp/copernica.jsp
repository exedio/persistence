<%@ page import="com.exedio.cope.lib.Search" %>
<%@ page import="com.exedio.cope.lib.Database" %>
<%@ page import="com.exedio.cope.lib.Attribute" %>
<%@ page import="com.exedio.cope.lib.Type" %>
<%@ page import="com.exedio.cope.lib.Item" %>
<%@ page import="com.exedio.cope.lib.SystemException" %>
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
	final CopernicaProvider provider;
	try
	{
		final Class providerClass = Class.forName("com.exedio.demoshop.CopernicaProvider");
		provider = (CopernicaProvider)providerClass.newInstance();
	}
	catch(ClassNotFoundException e)
	{
		throw new SystemException(e);
	}

	for( final Iterator it = request.getParameterMap().entrySet().iterator(); it.hasNext(); )
	{
		Map.Entry entry = (Map.Entry)it.next();
		System.out.println( "param: " + entry.getKey() + " value: " + entry.getValue() );
	}
	
%>
		<form action="copernica.jsp" method="POST">
			Database:
			<br>
			<input type="submit" name="CREATE" value="create" />
			<input type="submit" name="TEARDOWN" value="tear down"/>
			<input type="submit" name="DROP" value="drop"/>
			<br>
			<%
				if(request.getParameter("CREATE")!=null)
				{
					Database.theInstance.createDatabase();
					provider.initializeExampleSystem();
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
		</form>
		
		<table border="1">
			<tr>
				<td valign="top">
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
								<a href="copernica.jsp?type=<%=type.getJavaClass().getName()%>">
									<%=type.getJavaClass().getName()%>
								</a>
							</td>
						</tr>
						<%
					}
					%>
					</table>
				</td>
				<td valign="top">
				<%
					final String typeName = request.getParameter("type");
					if(typeName!=null)
					{
						final Type type = Type.getType(typeName);
						if(type==null)
							throw new RuntimeException("type "+typeName+" not available");
						%>
						<u><%=type.getJavaClass().getName()%></u>
						<hr>
						<ul>
						<%
							for(Iterator i = type.getAttributes().iterator(); i.hasNext(); )
							{
								final Attribute attribute = (Attribute)i.next();
								%><li><%=attribute.getName()%></li><%
							}
						%>
						</ul>
						<hr>
						<table border="1">
						<tr>
							<td>Item</td>
						</tr>
						<%
							for(Iterator i = Search.search(type, null).iterator(); i.hasNext(); )
							{
								final Item item = (Item)i.next();
								%><tr><td><%=item.getID()%></td></tr><%
							}
						%>
						</table>
						<%
					}
					else
						%><u>select a type on the left</u>
				</td>
			</tr>
		</table>
	
	</body>
</html>
