<%
	final Item item = itemCop.item;
	final Type type = item.getType();
	boolean toSave = false;
	final boolean save = request.getParameter("SAVE")!=null;
%>
<a href="<%=(cop.toType(type))%>"><%=provider.getDisplayName(null, type)%></a>
<b><%=provider.getDisplayName(null, item)%></b><hr>
<form action="<%=(cop.toItem(item))%>" method="POST">
	<table border="1"><%
	final Form form = new Form(item, request.getParameterMap());
	if(save)
		form.save();
	
	for(Iterator j = type.getAttributes().iterator(); j.hasNext(); )
	{
		final Attribute attribute = (Attribute)j.next();
		%>
		<tr>
			<td><%=provider.getDisplayName(null, attribute)%></td>
			<td><%
			if(attribute instanceof MediaAttribute)
			{
				%><%=item.getMediaURL((MediaAttribute)attribute)%><%
			}
			else if(attribute instanceof ItemAttribute)
			{
				final Item value = (Item)item.getAttribute((ItemAttribute)attribute);
				if(value==null)
				{
					%><%="leer"%><%
				}
				else
				{
					%><a href="<%=(cop.toItem(value))%>"><%=provider.getDisplayName(null, value)%></a><%
				}
			}
			else if(attribute instanceof EnumerationAttribute)
			{
				final EnumerationAttribute enumAttribute = (EnumerationAttribute)attribute;
				final String attributeName = attribute.getName();
				
				final EnumerationValue value;
				if(save)
				{
					final String saveString = request.getParameter(attributeName);
					if(saveString==null)
						throw new NullPointerException(attributeName);
					if("NULL".equals(saveString))
						value = null;
					else
					{
						value = enumAttribute.getValue(saveString);
						if(value==null)
							throw new NullPointerException(attributeName);
					}
					item.setAttribute(enumAttribute, value);
				}
				else
					value = (EnumerationValue)item.getAttribute(enumAttribute);
				
				toSave = true;
				if(!enumAttribute.isNotNull())
				{
					%>
					<input type="radio" name="<%=attributeName%>" value="NULL"<%
					if(value==null)
					{
						%> checked="checked"<%
					}
					%>><%="leer"%><br><%
				}
				for(Iterator k = enumAttribute.getValues().iterator(); k.hasNext(); )
				{
					final EnumerationValue currentValue = (EnumerationValue)k.next();
					%>
					<input type="radio" name="<%=attributeName%>" value="<%=currentValue.getCode()%>"<%
					if(value==currentValue)
					{
						%> checked="checked"<%
					}
					%>><%=currentValue.getCode()%><br><%
				}
				%>
			<%
			}
			else if(attribute instanceof BooleanAttribute)
			{
				final BooleanAttribute boolAttribute = (BooleanAttribute)attribute;
				final String attributeName = attribute.getName();
				
				final Boolean value;
				if(save)
				{
					final String saveString = request.getParameter(attributeName);
					if(saveString==null)
						value = Boolean.FALSE;
					else
					{
						if("NULL".equals(saveString))
							value = null;
						else if("TRUE".equals(saveString))
							value = Boolean.TRUE;
						else if("FALSE".equals(saveString))
							value = Boolean.FALSE;
						else
							throw new RuntimeException(saveString);
					}
					item.setAttribute(boolAttribute, value);
				}
				else
					value = (Boolean)item.getAttribute(boolAttribute);
				
				toSave = true;
				if(boolAttribute.isNotNull())
				{
					%>
					<input type="checkbox" name="<%=attributeName%>" value="TRUE"<%
					if(Boolean.TRUE.equals(value))
					{
						%> checked="checked"<%
					}
					%>><br><%
				}
				else
				{
					%>
					<input type="radio" name="<%=attributeName%>" value="NULL"<%
					if(value==null)
					{
						%> checked="checked"<%
					}
					%>><%="leer"%><br>
					<input type="radio" name="<%=attributeName%>" value="TRUE"<%
					if(Boolean.TRUE.equals(value))
					{
						%> checked="checked"<%
					}
					%>><%="ein"%><br>
					<input type="radio" name="<%=attributeName%>" value="FALSE"<%
					if(Boolean.FALSE.equals(value))
					{
						%> checked="checked"<%
					}
					%>><%="aus"%><br><%
				}
				%>
			<%
			}
			else if(attribute instanceof StringAttribute)
			{
				final StringAttribute stringAttribute = (StringAttribute)attribute;
				final Form.Field field = form.getField(stringAttribute);
		
				if(field.isReadOnly())
				{
					%><b><%=field.getValue()%></b><%
				}
				else
				{
					%><input type="text" name="<%=field.getName()%>" value="<%=field.getValue()%>" /><%
				}
			}
			else if(attribute instanceof IntegerAttribute)
			{
				final IntegerAttribute intAttribute = (IntegerAttribute)attribute;
				final String attributeName = attribute.getName();
		
				if(attribute.isReadOnly())
				{
					%><b><%=(Integer)item.getAttribute(intAttribute)%></b><%
				}
				else
				{
					final Integer value;
					if(save)
					{
						final String valueString = request.getParameter(attributeName);
						if(valueString==null)
							throw new NullPointerException(attributeName);
						if(valueString.length()>0)
						{
							try
							{
								value = new Integer(Integer.parseInt(valueString));
							}
							catch(NumberFormatException e)
							{
								throw new SystemException(e);
							}
						}
						else
							value = null;

						item.setAttribute(intAttribute, value);
					}
					else
					{
						value = (Integer)item.getAttribute(intAttribute);
					}
					final String output = (value==null) ? "" : String.valueOf(value);
		
					toSave = true;
					%><input type="text" name="<%=attribute.getName()%>" value="<%=output%>" /><%
				}
			}
			else
			{
				%><%=item.getAttribute((ObjectAttribute)attribute)%><%
			}
			%></td>
		</tr><%
	}
	%>
	</table><%
if(toSave||form.toSave)
{
	%>
	<input type="submit" name="SAVE" value="Save" /><%
}
%>
</form>
