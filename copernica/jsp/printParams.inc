<%@
page import="java.util.Iterator" %><%@
page import="java.util.Map"

%><%

for( final Iterator it = request.getParameterMap().entrySet().iterator(); it.hasNext(); )
{
	final Map.Entry entry = (Map.Entry)it.next();
	%>
	param &gt;<%=entry.getKey()%>&lt; :<%
	final String[] values = (String[])entry.getValue();
	for(int jt=0; jt<values.length; jt++)
	{
		%> &gt;<%=values[jt]%>&lt;<%
	}
	%><br><%
}
%>