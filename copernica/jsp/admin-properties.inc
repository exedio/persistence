
		<table border="1"><%
		final Properties properties = provider.getModel().getProperties().toProperties();
		for(Iterator i = properties.keySet().iterator(); i.hasNext(); )
		{
			final String key = (String)i.next();
			%>
			<tr>
				<td><small><%=key%></small></td>
				<td><small><%=properties.getProperty(key)%></small></td>
			</tr><%
		}
		%>
		</table>
