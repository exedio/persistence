<%@
page import="com.exedio.cope.lib.Search" %><%@
page import="com.exedio.cope.lib.Feature" %><%@
page import="com.exedio.cope.lib.Function" %><%@
page import="com.exedio.cope.lib.Attribute" %><%@
page import="com.exedio.cope.lib.ObjectAttribute" %><%@
page import="com.exedio.cope.lib.StringAttribute" %><%@
page import="com.exedio.cope.lib.BooleanAttribute" %><%@
page import="com.exedio.cope.lib.IntegerAttribute" %><%@
page import="com.exedio.cope.lib.LongAttribute" %><%@
page import="com.exedio.cope.lib.DoubleAttribute" %><%@
page import="com.exedio.cope.lib.DateAttribute" %><%@
page import="com.exedio.cope.lib.MediaAttribute" %><%@
page import="com.exedio.cope.lib.EnumerationAttribute" %><%@
page import="com.exedio.cope.lib.EnumerationValue" %><%@
page import="com.exedio.cope.lib.ItemAttribute" %><%@
page import="com.exedio.cope.lib.Type" %><%@
page import="com.exedio.cope.lib.Item" %><%@
page import="com.exedio.cope.lib.NestingRuntimeException" %><%@
page import="com.exedio.cope.lib.NoSuchIDException" %><%@
page import="java.io.PrintWriter" %><%@
page import="java.util.Iterator" %><%@
page import="java.util.Collection" %><%@
page import="java.util.Map" %><%@

include file="provider.inc"

%><%
	final CopernicaCop cop = CopernicaCop.getCop(
		provider, request.getParameterMap()
	);
%>
<html>
	<head>
		<title><%=cop.getTitle(provider)%></title>
		<link rel="STYLESHEET" type="text/css" href="copernica.css">
	</head>
	<body><%
	
	boolean database = false;
	try
	{
		provider.getModel().checkDatabase();
		database = true;
	}
	catch(NestingRuntimeException e)
	{
		%><b>Database not initialized.</b><br>
		<a href="admin.jsp">Administration</a>
		<hr>
		<pre><%e.printStackTrace(new PrintWriter(out));%></pre><%
	}
	
	if(database)
	{
		final Language language = cop.language;
		%>
		<div id="langcontainer">
			<ul id="langlist"><%
			for(Iterator l = provider.getDisplayLanguages().iterator(); l.hasNext(); )
			{
				final Language currentLanguage = (Language)l.next();
				final String name = currentLanguage.getCopernicaName(language);
				final boolean active = currentLanguage.equals(language);
				%>
				<li<%if(active){%> id="active"<%}%>>
					<a href="<%=cop.switchLanguage(currentLanguage)%>"<%
						if(active){%> id="current"<%}%>><%=name%></a>
				</li><%
			}
			%>
			</ul>
		</div>

		<table border="0">
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
