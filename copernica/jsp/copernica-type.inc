<%
			final Type type = typeCop.type;
			final Collection items = typeCop.getItems();
			
			%>
			<h1>
				<a href="<%=(cop.toType(type))%>"><%=provider.getDisplayName(language, type)%></a>
			</h1>
			<ul><%
			int numberOfSearchFeatures = 5;
			for(Iterator i = type.getFeatures().iterator();
					i.hasNext() && numberOfSearchFeatures>0;
					numberOfSearchFeatures--)
			{
				final Feature feature = (Feature)i.next();
				%>
				<li><%=provider.getDisplayName(language, feature)%></li><%
			}
			%>
			</ul><%
			final int itemsSize = items.size();
			final boolean firstPage = typeCop.isFirstPage();
			final boolean lastPage = typeCop.isLastPage();
			if(typeCop.start>0 || itemsSize>0)
			{
			%>
			<div id="pager">
				<a href="<%=typeCop.firstPage()%>"<%
					if(firstPage){%> id="disabled"<%}%>>&lt;&lt;</a>
				<a href="<%=typeCop.previousPage()%>"<%
					if(firstPage){%> id="disabled"<%}%>>&lt;</a>
				<a href="<%=typeCop.nextPage()%>"<%
					if(lastPage) {%> id="disabled"<%}%>>&gt;</a>
				<%=typeCop.start+1%> - <%=typeCop.start+itemsSize%>
				(<%
				for(int i = 10; i<=50; i+=10)
				{
				%>
				<a href="<%=typeCop.switchCount(i)%>"<%
					if(i==typeCop.count){%> id="disabled"<%}%>><%=i%></a><%
				}%>
				)
			</div><%
			}
			%>
			<table id="item">
				<tr>
					<th></th><%
				final List features = type.getFeatures();
				for(Iterator i = features.iterator(); i.hasNext(); )
				{
					final Feature feature = (Feature)i.next();
					%>
					<th><%=provider.getDisplayName(language, feature)%></th><%
				}
			%>
				</tr>
				<tr>
					<th></th><%
				for(Iterator i = features.iterator(); i.hasNext(); )
				{
					final Feature feature = (Feature)i.next();
					%>
					<th class="orderby"><%
					if(feature instanceof Function)
					{
						final Function function = (Function)feature;
						%>
						<a href="<%=typeCop.orderBy(function, true)%>"<%
						if(feature == typeCop.orderBy && typeCop.orderAscending)
						{
							%> class="disabled"<%
						}
						%>>a</a>
						<a href="<%=typeCop.orderBy(function, false)%>"<%
						if(feature == typeCop.orderBy && !typeCop.orderAscending)
						{
							%> class="disabled"<%
						}
						%>>d</a>
					<%
					}
					%></th><%
				}
			%>
				</tr><%
				SimpleDateFormat df = null;
				
				for(Iterator i = items.iterator(); i.hasNext(); )
				{
					final Item item = (Item)i.next();
					%>
				<tr>
					<td><a href="<%=(cop.toItem(item))%>"><%=cop.getDisplayName(language, item)%></a></td><%
					for(Iterator j = features.iterator(); j.hasNext(); )
					{
						final Feature feature = (Feature)j.next();
						%>
					<td><%
						if(feature instanceof MediaAttribute)
						{
							final MediaAttribute mediaAttribute = (MediaAttribute)feature;
							final String url = item.getMediaURL(mediaAttribute);
							if(url==null)
							{
								%><%=cop.getDisplayNameNull()%><%
							}
							else
							{
								%><a href="<%=url%>"><%
								if("image".equals(item.getMediaMimeMajor(mediaAttribute)))
								{
									%><img src="<%=url%>" width="50"><%
								}
								else
								{
									%>download<%
								}
								%></a><%
							}
						}
						else if(feature instanceof ItemAttribute)
						{
							final Item value = (Item)item.getAttribute((ItemAttribute)feature);
							if(value==null)
							{
								%><%=cop.getDisplayNameNull()%><%
							}
							else
							{
								%><a href="<%=(cop.toItem(value))%>"><%=cop.getDisplayName(language, value)%></a><%
							}
						}
						else if(feature instanceof DateAttribute)
						{
							final Date value = (Date)item.getAttribute((DateAttribute)feature);
							if(value==null)
							{
								%><%=cop.getDisplayNameNull()%><%
							}
							else
							{
								if(df==null)
									df = new SimpleDateFormat(ItemForm.DATE_FORMAT_FULL);
								%><%=df.format(value)%><%
							}
						}
						else if(feature instanceof EnumAttribute)
						{
							final EnumValue value = (EnumValue)item.getAttribute((EnumAttribute)feature);
							if(value==null)
							{
								%><%=cop.getDisplayNameNull()%><%
							}
							else
							{
								%><%=cop.getDisplayName(value)%><%
							}
						}
						else if(feature instanceof BooleanAttribute)
						{
							final Boolean value = (Boolean)item.getAttribute((BooleanAttribute)feature);
							if(value==null)
							{
								%><%=cop.getDisplayNameNull()%><%
							}
							else
							{
								%><%=value.booleanValue()?cop.getDisplayNameOn():cop.getDisplayNameOff()%><%
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
