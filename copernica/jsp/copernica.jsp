<%@
page import="com.exedio.cope.lib.Search" %><%@
page import="com.exedio.cope.lib.Database" %><%@
page import="com.exedio.cope.lib.Feature" %><%@
page import="com.exedio.cope.lib.Function" %><%@
page import="com.exedio.cope.lib.Attribute" %><%@
page import="com.exedio.cope.lib.ObjectAttribute" %><%@
page import="com.exedio.cope.lib.StringAttribute" %><%@
page import="com.exedio.cope.lib.MediaAttribute" %><%@
page import="com.exedio.cope.lib.EnumerationAttribute" %><%@
page import="com.exedio.cope.lib.EnumerationValue" %><%@
page import="com.exedio.cope.lib.ItemAttribute" %><%@
page import="com.exedio.cope.lib.Type" %><%@
page import="com.exedio.cope.lib.Item" %><%@
page import="com.exedio.cope.lib.SystemException" %><%@
page import="java.io.PrintWriter" %><%@
page import="java.util.Iterator" %><%@
page import="java.util.Collection" %><%@
page import="java.util.Map"
%>
<html>
	<head>
		<title>
			Copernica
		</title>
	</head>
	<body>
<%
	final CopernicaProvider provider;
	try
	{
		final String providerName = getInitParameter("com.exedio.copernica.provider");
		if(providerName==null)
			throw new NullPointerException("init-param com.exedio.copernica.provider missing");
		final Class providerClass = Class.forName(providerName);
		provider = (CopernicaProvider)providerClass.newInstance();
	}
	catch(ClassNotFoundException e)
	{
		throw new SystemException(e);
	}

	for( final Iterator it = request.getParameterMap().entrySet().iterator(); it.hasNext(); )
	{
		Map.Entry entry = (Map.Entry)it.next();
		System.out.println( "param: " + entry.getKey() + " value: " + entry.getValue() );
	}
	
	boolean database = false;
	try
	{
		Database.theInstance.checkDatabase();
		database = true;
	}
	catch(SystemException e)
	{
		%><b>Database not initialized.</b><br>
		<a href="admin.jsp">Administration</a>
		<hr>
		<pre><%e.printStackTrace(new PrintWriter(out));%></pre><%
	}
	
	if(database)
	{
		final CopernicaCop cop = CopernicaCop.getCop(
			provider, request.getParameterMap()
		);
		final Language language = cop.language;
			
		for(Iterator l = provider.getDisplayLanguages().iterator(); l.hasNext(); )
		{
			final Language currentLanguage = (Language)l.next();
			final String languageName = currentLanguage.getCopernicaName(language);
			if(!currentLanguage.equals(language))
			{
				%>[<a href="<%=cop.switchLanguage(currentLanguage)%>"><%=languageName%></a>]<%
			}
			else
			{
				%>[<b><%=languageName%></b>]<%
			}
		}
		%>
		
		<table border="1">
			<tr>
				<td valign="top">
					<table border="1">
					<%
					for(Iterator i = Type.getTypes().iterator(); i.hasNext(); )
					{
						%>
						<tr>
							<td>
								<%
								final Type type = (Type)i.next();
								if(cop.isType(type))
								{
									%><b><%=provider.getDisplayName(null, type)%></b><%
								}
								else
								{
									%>
									<a href="<%=(cop.toType(type))%>">
										<%=provider.getDisplayName(null, type)%>
									</a>
									<%
								}
								%>
							</td>
						</tr>
						<%
					}
					%>
					</table>
				</td>
				<td valign="top">
				<%
					if(cop instanceof TypeCop)
					{
						final TypeCop typeCop = ((TypeCop)cop);
						%><%@ include file="copernica-type.inc" %><%
					}
					else if(cop instanceof ItemCop)
					{
						final Item item = ((ItemCop)cop).item;
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
						<%
					}
					else if(cop instanceof EmptyCop)
						%>
						<h1>Copernica</h1>
						<h2>Generic Backoffice for COPE</h2>
						<u>select a type on the left</u><%
					else
						throw new RuntimeException();
					%>
				</td>
			</tr>
		</table><%
		}
	%>
	</body>
</html>
