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
		<style>
			#langlist
			{
				border-bottom: 1px solid #4181d5;
				border-top: 1px solid #4181d5;
				margin: 0px;
				margin-top: 30px;
				margin-bottom: 30px;
				padding: 0px;
				padding-left: 50px;
				background-color: #ffffff;
				font-family: Verdana, Geneva, Arial, Helvetica, sans-serif;
				padding-top: 10px;
				padding-bottom: 10px;
			}
			
			#langlist a, #langlist a:link, #langlist a:visited
			{
				border: 1px solid #4181d5;
				padding: 1px;
				padding-left: 0.5em;
				padding-right: 0.5em;
				color: #000000;
				font-weight: bold;
				text-decoration: none;
			}
			
			#langlist a:hover, #langlist a:active, #langlist a:focus
			{
				border: 1px solid #000000;
				padding: 1px;
				padding-left: 0.5em;
				padding-right: 0.5em;
				text-decoration: none;
			}
			
			#langlist li
			{
				padding-right: 1px;
				display: inline;
				font-size: 0.6em;
			}
			
			#langlist ul
			{
				margin: 0px;
				padding: 0px;
			}
			
			#langlist #active a { background-color: #61a1f5; }
		</style>
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
