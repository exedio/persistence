<%@ page import="com.exedio.cope.lib.Search" %>
<%@ page import="com.exedio.cope.lib.Database" %>
<%@ page import="com.exedio.cope.lib.Attribute" %>
<%@ page import="com.exedio.cope.lib.MediaAttribute" %>
<%@ page import="com.exedio.cope.lib.EnumerationAttribute" %>
<%@ page import="com.exedio.cope.lib.EnumerationValue" %>
<%@ page import="com.exedio.cope.lib.ItemAttribute" %>
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
									<%=provider.getDisplayName(null, type)%>
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
					final String typeID = request.getParameter("type");
					final String itemID = request.getParameter("item");
					if(typeID!=null)
					{
						final Type type = Type.getType(typeID);
						if(type==null)
							throw new RuntimeException("type "+typeID+" not available");
						%>
						<u><%=provider.getDisplayName(null, type)%></u>
						<hr>
						<ul>
						<%
							for(Iterator i = type.getAttributes().iterator(); i.hasNext(); )
							{
								final Attribute attribute = (Attribute)i.next();
								%><li><%=provider.getDisplayName(null, attribute)%></li><%
							}
						%>
						</ul>
						<hr>
						<table border="1">
						<tr>
						<th></th><%
							for(Iterator i = type.getAttributes().iterator(); i.hasNext(); )
							{
								final Attribute attribute = (Attribute)i.next();
								%><th><%=provider.getDisplayName(null, attribute)%></th><%
							}
						%>
						</tr>
						<%
							for(Iterator i = Search.search(type, null).iterator(); i.hasNext(); )
							{
								final Item item = (Item)i.next();
								%><tr><td><a href="copernica.jsp?item=<%=item.getID()%>">[X]</a></td><%
								for(Iterator j = type.getAttributes().iterator(); j.hasNext(); )
								{
									final Attribute attribute = (Attribute)j.next();
									%><td><%
									if(attribute instanceof MediaAttribute)
									{
										%><%=item.getMediaURL((MediaAttribute)attribute)%><%
									}
									else if(attribute instanceof ItemAttribute)
									{
										final Item value = (Item)item.getAttribute(attribute);
										if(value==null)
										{
											%><%="leer"%><%
										}
										else
										{
											%><a href="copernica.jsp?item=<%=value.getID()%>">
												<%=provider.getDisplayName(null, value)%>
											</a><%
										}
									}
									else
									{
										%><%=item.getAttribute(attribute)%><%
									}
									%></td><%
								}
								%></tr><%
							}
						%>
						</table>
						<%
					}
					else if(itemID!=null)
					{
						final Item item = Search.findByID(itemID);
						final Type type = item.getType();
						boolean toSave = false;
						%>
						<u><%=provider.getDisplayName(null, type)%></u><br>
						<b><%=provider.getDisplayName(null, item)%></b><hr>
						<form action="copernica.jsp?item=<%=item.getID()%>" method="POST">
						<table border="1">
						<%
						for(Iterator j = type.getAttributes().iterator(); j.hasNext(); )
						{
							final Attribute attribute = (Attribute)j.next();
							%><tr><td><%=provider.getDisplayName(null, attribute)%></td><td><%
							if(attribute instanceof MediaAttribute)
							{
								%><%=item.getMediaURL((MediaAttribute)attribute)%><%
							}
							else if(attribute instanceof ItemAttribute)
							{
								final Item value = (Item)item.getAttribute(attribute);
								if(value==null)
								{
									%><%="leer"%><%
								}
								else
								{
									%><a href="copernica.jsp?item=<%=value.getID()%>">
										<%=provider.getDisplayName(null, value)%>
									</a><%
								}
							}
							else if(attribute instanceof EnumerationAttribute)
							{
								final EnumerationAttribute enumAttribute = (EnumerationAttribute)attribute;
								final EnumerationValue value = (EnumerationValue)item.getAttribute(attribute);
								for(Iterator k = enumAttribute.getValues().iterator(); k.hasNext(); )
								{
									final EnumerationValue currentValue = (EnumerationValue)k.next();
									%>
									<input
										type="radio" name="<%=attribute.getName()%>"
										value="currentValue.getCode()"
										<%
											if(value==currentValue)
											{
												%>checked="checked"<%
											}
										%>
										><%=currentValue.getCode()%><br><%
								}
							}
							else
							{
								%>
								<input
									type="text"
									name="<%=attribute.getName()%>"
									value="<%=item.getAttribute(attribute)%>" />
								<%
							}
							%></td></tr><%
						}
						%>
						</table>
						<input type="submit" name="SAVE" value="Save" />
						</form>
						<%
					}
					else
						%><u>select a type on the left</u>
				</td>
			</tr>
		</table>
	
	</body>
</html>
