<%
	final Item item = itemCop.item;
	final Type type = item.getType();
	boolean toSave = false;
	final boolean save = request.getParameter("SAVE")!=null;
%>
<a href="<%=(cop.toType(type))%>"><%=provider.getDisplayName(null, type)%></a>
<b><%=provider.getDisplayName(null, item)%></b><hr>
<form action="<%=(cop.toItem(item))%>" method="POST">
<table border="1">
<%
for(Iterator j = type.getAttributes().iterator(); j.hasNext(); )
{
	final Attribute attribute = (Attribute)j.next();
	%><tr><td><%=provider.getDisplayName(null, attribute)%></td><td><%
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
			%><a href="<%=(cop.toItem(value))%>">
				<%=provider.getDisplayName(null, value)%>
			</a><%
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
			value = enumAttribute.getValue(saveString);
			if(value==null)
				throw new NullPointerException(attributeName);
			item.setAttribute(enumAttribute, value);
		}
		else
			value = (EnumerationValue)item.getAttribute(enumAttribute);
		
		toSave = true;
		for(Iterator k = enumAttribute.getValues().iterator(); k.hasNext(); )
		{
			final EnumerationValue currentValue = (EnumerationValue)k.next();
			%>
			<input
				type="radio" name="<%=attributeName%>"
				value="<%=currentValue.getCode()%>"
				<%
					if(value==currentValue)
					{
						%>checked="checked"<%
					}
				%>
				><%=currentValue.getCode()%><br><%
		}
	}
	else if(attribute instanceof StringAttribute)
	{
		final StringAttribute stringAttribute = (StringAttribute)attribute;
		final String attributeName = attribute.getName();

		if(attribute.isReadOnly())
		{
			%><b><%=(String)item.getAttribute(stringAttribute)%></b><%
		}
		else
		{
			final String value;
			if(save)
			{
				value = request.getParameter(attributeName);
				if(value==null)
					throw new NullPointerException(attributeName);
				item.setAttribute(stringAttribute, value);
			}
			else
				value = (String)item.getAttribute(stringAttribute);

			toSave = true;
			%><input type="text" name="<%=attribute.getName()%>" value="<%=value%>" /><%
		}
	}
	else
	{
		%><%=item.getAttribute((ObjectAttribute)attribute)%><%
	}
	%></td></tr><%
}
%>
</table>
<%
if(toSave)
{
	%><input type="submit" name="SAVE" value="Save" /><%
}
%>
</form>
