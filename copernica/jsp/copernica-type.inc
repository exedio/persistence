<%
	final Type type = typeCop.type;
	final Collection items = typeCop.search();
	
	%>
	<h1>
		<a href="<%=(cop.toType(type))%>"><%=provider.getDisplayName(null, type)%></a>
	</h1>
	<ul><%
	int numberOfSearchFeatures = 5;
	for(Iterator i = type.getFeatures().iterator();
			i.hasNext() && numberOfSearchFeatures>0;
			numberOfSearchFeatures--)
	{
		final Feature feature = (Feature)i.next();
		%>
		<li><%=provider.getDisplayName(null, feature)%></li><%
	}
	%>
	</ul>
	<%
	final boolean firstPage = typeCop.isFirstPage();
	final boolean lastPage = typeCop.count>items.size();
	%>
	<div id="pager">
		<a href="<%=typeCop.firstPage()%>"<%
			if(firstPage){%> id="disabled"<%}%>>&lt;&lt;</a>
		<a href="<%=typeCop.previousPage()%>"<%
			if(firstPage){%> id="disabled"<%}%>>&lt;</a>
		<a href="<%=typeCop.nextPage()%>"<%
			if(lastPage) {%> id="disabled"<%}%>>&gt;</a>
		<%=typeCop.start+1%> - <%=typeCop.start+items.size()%>
		(<%
		for(int i = 10; i<=50; i+=10)
		{
		%>
		<a href="<%=typeCop.switchCount(i)%>"<%
			if(i==typeCop.count){%> id="disabled"<%}%>><%=i%></a><%
		}%>
		)
	</div>
	<table border="1">
	<tr>
	<th></th><%
		for(Iterator i = type.getFeatures().iterator(); i.hasNext(); )
		{
			final Feature feature = (Feature)i.next();
			%>
			<th>
				<%=provider.getDisplayName(null, feature)%><%
			if(feature instanceof Function)
			{
				%><br>
				<%
				if(feature == typeCop.orderBy)
				{
					if(typeCop.orderAscending)
					{
						%><b>a</b>
						<a href="<%=typeCop.orderBy((Function)feature, false)%>">d</a><%
					}
					else
					{
						%><a href="<%=typeCop.orderBy((Function)feature, true)%>">a</a>
						<b>d</b><%
					}
				}
				else
				{
					%><a href="<%=typeCop.orderBy((Function)feature, true)%>">a</a>
					<a href="<%=typeCop.orderBy((Function)feature, false)%>">d</a><%
				}
			}
			%>
			</th><%
		}
	%>
	</tr><%
		for(Iterator i = items.iterator(); i.hasNext(); )
		{
			final Item item = (Item)i.next();
			%>
			<tr>
				<td><a href="<%=(cop.toItem(item))%>"><%=provider.getDisplayName(null, item)%></a></td><%
			for(Iterator j = type.getFeatures().iterator(); j.hasNext(); )
			{
				final Feature feature = (Feature)j.next();
				%>
				<td><%
				if(feature instanceof MediaAttribute)
				{
					%><%=item.getMediaURL((MediaAttribute)feature)%><%
				}
				else if(feature instanceof ItemAttribute)
				{
					final Item value = (Item)item.getAttribute((ItemAttribute)feature);
					if(value==null)
					{
						%><%="leer"%><%
					}
					else
					{
						%><a href="<%=(cop.toItem(value))%>"><%=provider.getDisplayName(null, value)%></a><%
					}
				}
				else
				{
					%><%=item.getFunction((Function)feature)%><%
				}
				%></td><%
			}
			%>
			</tr><%
		}
	%>
	</table>
