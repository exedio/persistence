<%@
page import="java.util.Iterator" %><%@
page import="java.util.Enumeration" %><%@
page import="java.util.Map" %><%@
page import="java.io.BufferedReader"

%><%

for( final Iterator it = request.getParameterMap().entrySet().iterator(); it.hasNext(); )
{
	final Map.Entry entry = (Map.Entry)it.next();
	%>
	<small>param</small> &gt;<%=entry.getKey()%>&lt; :<%
	final String[] values = (String[])entry.getValue();
	for(int jt=0; jt<values.length; jt++)
	{
		%> &gt;<%=values[jt]%>&lt;<%
	}
	%><br><%
}
for( final Enumeration it = request.getHeaderNames(); it.hasMoreElements(); )
{
	final String name = (String)it.nextElement();
	%>
	<small>header</small> &gt;<%=name%>&lt; :<%
	%> &gt;<%=request.getHeader(name)%>&lt;<%
	%><br><%
	//System.out.println("header <"+name+">: <"+request.getHeader(name)+">");
}
final BufferedReader body = new BufferedReader(request.getReader());
int i = 0;
for(String line = body.readLine(); line!=null; line = body.readLine())
{
	%>
	<small><%=i%>:</small> &gt;<%=line%>&lt;<%
	%><br><%
}
%>