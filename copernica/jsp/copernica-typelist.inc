
		<ul id="typelist"><%
		for(Iterator i = provider.getModel().getTypes().iterator(); i.hasNext(); )
		{
			final Type type = (Type)i.next();
			final String name = provider.getDisplayName(null, type);
			final boolean active = cop.isType(type);
			%>
			<li<%
			if(active)
			{
				%> id="active"<%
			}
			%>><a href="<%=(cop.toType(type))
			%>"<%
			if(active)
			{
				%> id="current"<%
			}
			%>><%=name%></a></li><%
		}
		%>
		</ul>
