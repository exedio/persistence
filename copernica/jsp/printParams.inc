<%@
page import="java.util.Iterator" %><%@
page import="java.util.Map"

%><%

for( final Iterator it = request.getParameterMap().entrySet().iterator(); it.hasNext(); )
{
	final Map.Entry entry = (Map.Entry)it.next();
	System.out.print( "param >" + entry.getKey() + "< :");
	final String[] values = (String[])entry.getValue();
	for(int jt=0; jt<values.length; jt++)
		System.out.print( " >" + values[jt] + "<" );
	System.out.println();
}
%>