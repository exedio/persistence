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
	<hr><%
	final boolean firstPage = typeCop.isFirstPage();
	final boolean lastPage = typeCop.count>items.size();
	if(!(firstPage&&lastPage))
	{
		if(firstPage)
		{
			%>
			[<b>&lt;&lt;</b>] | [<b>&lt;</b>]<%
		}
		else
		{
			%>
			[<a href="<%=typeCop.firstPage()%>">&lt;&lt;</a>] |
			[<a href="<%=typeCop.previousPage()%>">&lt;</a>]<%
		}
		%>
		| <%
		if(lastPage)
		{
			%>
			[<b>&gt;</b>]<%
		}
		else
		{
			%>
			[<a href="<%=typeCop.nextPage()%>">&gt;</a>]<%
		}
		%> <%=typeCop.start+1%> - <%=typeCop.start+items.size()
		%><br>Items per Page: <%
		for(int i = 10; i<=50; i+=10)
		{
			if(i==typeCop.count)
			{
				%> <b><%=i%></b><%
			}
			else
			{
				%> <a href="<%=typeCop.switchCount(i)%>"><%=i%></a><%
			}
		}
	}
	%>
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
