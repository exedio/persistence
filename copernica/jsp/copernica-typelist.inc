
<table border="1"><%
for(Iterator i = Type.getTypes().iterator(); i.hasNext(); )
{
	%>
	<tr>
		<td><%
			final Type type = (Type)i.next();
			if(cop.isType(type))
			{
				%><b><%=provider.getDisplayName(null, type)%></b><%
			}
			else
			{
				%><a href="<%=(cop.toType(type))%>"><%=provider.getDisplayName(null, type)%></a><%
			}
		%></td>
	</tr><%
}
%>
</table>
