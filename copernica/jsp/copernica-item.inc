<%!
				static final String SAVE_BUTTON = ItemForm.SAVE_BUTTON;
				static final String VALUE_NULL = ItemForm.VALUE_NULL;
				static final String VALUE_ON = ItemForm.VALUE_ON;
				static final String VALUE_OFF = ItemForm.VALUE_OFF;
			%><%
				final Item item = itemCop.item;
				final Type type = item.getType();
				final ItemForm form = new ItemForm(item, request);
				
			%>
			
			<form action="<%=(cop.toItem(item))%>" method="POST"<%
				if(form.hasFiles)
				{
					%> enctype="multipart/form-data"<%
				}
				%>>
				<table border="0" width="100%">
					<tr>
						<td>
							<h1>
								<a href="<%=(cop.toType(type))%>"><%=provider.getDisplayName(language, type)%></a>
								<b><%=cop.getDisplayName(language, item)%></b>
							</h1>
						</td>
						<td align="right"><%
						if(form.toSave)
						{
							%>
							<input type="submit" name="<%=ItemForm.CHECK_BUTTON%>" value="Check" />
							<input type="submit" name="<%=SAVE_BUTTON%>" value="Save" /><%
						}
						%>
						</td>
					</tr>
				<%
				final Collection mainAttributes;
				final Collection sections = provider.getSections(type);
				if(sections!=null)
				{
					%>
					<tr><td><%
					for(Iterator i = sections.iterator(); i.hasNext(); )
					{
						final CopernicaSection section = (CopernicaSection)i.next();
						final String id = section.getCopernicaID();
						final String name = section.getCopernicaName(language);
						%><input type="submit" name="<%=id%>" value="<%=name%>" /><%
					}
					%>
					</td></tr><%
					mainAttributes = provider.getMainAttributes(type);
				}
				else
				{
					mainAttributes = null;
				}
				%></table>
				<table border="1"><%
				for(Iterator j = form.getFields().iterator(); j.hasNext(); )
				{
					final Form.Field field = (Form.Field)j.next();
					final Attribute attribute = (Attribute)field.key;
					%>
					<tr>
						<td><%=provider.getDisplayName(null, attribute)%></td>
						<td><%
						if(attribute instanceof MediaAttribute)
						{
							final MediaAttribute mediaAttribute = (MediaAttribute)attribute;
							final String attributeName = attribute.getName();
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
									%><img src="<%=url%>" width="250"><small>(<%=item.getMediaMimeMinor(mediaAttribute)%>)</small><%
								}
								else
								{
									%>download <small>(<%=item.getMediaMimeMajor(mediaAttribute)%>/<%=item.getMediaMimeMinor(mediaAttribute)%>)</small><%
								}
								%></a><%
							}
							%>
							<input type="file" name="<%=attributeName%>" size="20"><%
						}
						else if(attribute instanceof EnumAttribute)
						{
							final EnumAttribute enumAttribute = (EnumAttribute)attribute;
							final String attributeName = attribute.getName();
							final String value = field.getValue();
							
							if(!enumAttribute.isNotNull())
							{
								%>
								<input type="radio" name="<%=attributeName%>" value="<%=VALUE_NULL%>"<%
								if(value.equals(VALUE_NULL))
								{
									%> checked="checked"<%
								}
								%>><%=cop.getDisplayNameNull()%><br><%
							}
							for(Iterator k = enumAttribute.getValues().iterator(); k.hasNext(); )
							{
								final EnumValue currentValue = (EnumValue)k.next();
								final String currentCode = currentValue.getCode();
								final String currentName = cop.getDisplayName(currentValue);
								%>
								<input type="radio" name="<%=attributeName%>" value="<%=currentCode%>"<%
								if(value.equals(currentCode))
								{
									%> checked="checked"<%
								}
								%>><%=currentName%><br><%
							}
							%>
						<%
						}
						else if(attribute instanceof BooleanAttribute)
						{
							final String attributeName = attribute.getName();
							final String value = field.getValue();
							
							if(attribute.isNotNull())
							{
								%>
								<input type="checkbox" name="<%=attributeName%>" value="<%=VALUE_ON%>"<%
								if(VALUE_ON.equals(value))
								{
									%> checked="checked"<%
								}
								%>><br><%
							}
							else
							{
								%>
								<input type="radio" name="<%=attributeName%>" value="<%=VALUE_NULL%>"<%
								if(value.equals(VALUE_NULL))
								{
									%> checked="checked"<%
								}
								%>><%=cop.getDisplayNameNull()%><br>
								<input type="radio" name="<%=attributeName%>" value="<%=VALUE_ON%>"<%
								if(value.equals(VALUE_ON))
								{
									%> checked="checked"<%
								}
								%>><%=cop.getDisplayNameOn()%><br>
								<input type="radio" name="<%=attributeName%>" value="<%=VALUE_OFF%>"<%
								if(value.equals(VALUE_OFF))
								{
									%> checked="checked"<%
								}
								%>><%=cop.getDisplayNameOff()%><br><%
							}
							%>
						<%
						}
						else if(attribute instanceof StringAttribute
									|| attribute instanceof IntegerAttribute
									|| attribute instanceof LongAttribute
									|| attribute instanceof DoubleAttribute
									|| attribute instanceof DateAttribute
									|| attribute instanceof ItemAttribute)
						{
							final ObjectAttribute objectAttribute = (ObjectAttribute)attribute;
							final String value = field.getValue();
					
							if(field.isReadOnly())
							{
								%><b><%=value%></b><%
							}
							else
							{
								%><input type="text" name="<%=field.getName()%>" size="30" value="<%=value%>" /><%
								if(attribute instanceof ItemAttribute && value.length()>0)
								{
									try
									{
										final Item valueItem = provider.getModel().findByID(value);
										%> <a href="<%=(cop.toItem(valueItem))%>"><%=cop.getDisplayName(null, valueItem)%></a><%
									}
									catch(NoSuchIDException e)
									{
										%> !!!<%
									}
								}
								if(field.getError()!=null)
								{
									%> <b><%=field.getError()%></b><%
								}
							}
						}
						else
						{
							%><i><%=field.getValue()%></i><%
						}
						%></td>
					</tr><%
				}
				%>
				</table><%
			%>
			</form>
