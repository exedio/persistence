<%!
	static final String SAVE_BUTTON = "SAVE";
	static final String VALUE_NULL = "null";
	static final String VALUE_ON = "on";
	static final String VALUE_OFF = "off";
%><%
	final Item item = itemCop.item;
	final Type type = item.getType();
	
	final boolean save = request.getParameter(SAVE_BUTTON)!=null;
	final ItemForm form = new ItemForm(item, request.getParameterMap(), save);
	if(save)
		form.save();
%>
<form action="<%=(cop.toItem(item))%>" method="POST">
	<table border="0" width="100%">
		<tr>
			<td>
				<a href="<%=(cop.toType(type))%>"><%=provider.getDisplayName(null, type)%></a>
				<b><%=provider.getDisplayName(null, item)%></b><hr>
			</td>
			<td align="right"><%
			if(form.toSave)
			{
				%>
				<input type="submit" name="<%=SAVE_BUTTON%>" value="Save" /><%
			}
			%>
			</td>
		</tr>
	</table>
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
				%><%=item.getMediaURL((MediaAttribute)attribute)%><%
			}
			else if(attribute instanceof EnumerationAttribute)
			{
				final EnumerationAttribute enumAttribute = (EnumerationAttribute)attribute;
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
					%>><%="leer"%><br><%
				}
				for(Iterator k = enumAttribute.getValues().iterator(); k.hasNext(); )
				{
					final EnumerationValue currentValue = (EnumerationValue)k.next();
					final String currentCode = currentValue.getCode();
					%>
					<input type="radio" name="<%=attributeName%>" value="<%=currentCode%>"<%
					if(value.equals(currentCode))
					{
						%> checked="checked"<%
					}
					%>><%=currentCode%><br><%
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
					%>><%="leer"%><br>
					<input type="radio" name="<%=attributeName%>" value="<%=VALUE_ON%>"<%
					if(value.equals(VALUE_ON))
					{
						%> checked="checked"<%
					}
					%>><%="ein"%><br>
					<input type="radio" name="<%=attributeName%>" value="<%=VALUE_OFF%>"<%
					if(value.equals(VALUE_OFF))
					{
						%> checked="checked"<%
					}
					%>><%="aus"%><br><%
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
							%> <a href="<%=(cop.toItem(valueItem))%>"><%=provider.getDisplayName(null, valueItem)%></a><%
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
