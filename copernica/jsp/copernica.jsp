<%@
page import="com.exedio.cope.lib.Search" %><%@
page import="com.exedio.cope.lib.Database" %><%@
page import="com.exedio.cope.lib.Feature" %><%@
page import="com.exedio.cope.lib.Function" %><%@
page import="com.exedio.cope.lib.Attribute" %><%@
page import="com.exedio.cope.lib.ObjectAttribute" %><%@
page import="com.exedio.cope.lib.StringAttribute" %><%@
page import="com.exedio.cope.lib.BooleanAttribute" %><%@
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
					<%@ include file="copernica-typelist.inc" %>
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
						final ItemCop itemCop = (ItemCop)cop;
						%><%@ include file="copernica-item.inc" %><%
					}
					else if(cop instanceof EmptyCop)
					{
						%>
						<h1>Copernica</h1>
						<h2>Generic Backoffice for COPE</h2>
						<u>select a type on the left</u><%
					}
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
