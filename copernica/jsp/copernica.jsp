<%@
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
page import="com.exedio.cope.lib.EnumAttribute" %><%@
page import="com.exedio.cope.lib.EnumValue" %><%@
page import="com.exedio.cope.lib.ItemAttribute" %><%@
page import="com.exedio.cope.lib.Type" %><%@
page import="com.exedio.cope.lib.Item" %><%@
page import="com.exedio.cope.lib.StatementInfo" %><%@
page import="com.exedio.cope.lib.NestingRuntimeException" %><%@
page import="com.exedio.cope.lib.NoSuchIDException" %><%@
page import="com.exedio.copernica.CopernicaProvider" %><%@
page import="java.io.PrintWriter" %><%@
page import="java.io.IOException" %><%@
page import="java.util.Date" %><%@
page import="java.util.Iterator" %><%@
page import="java.util.Collection" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.text.SimpleDateFormat" %><%!
	
	CopernicaProvider provider = null;
	boolean checked;
	
	public final void jspInit()
	{
		if(this.provider!=null)
		{
			System.out.println("reinvokation of jspInit");
			return;
		}
		
		this.provider = Util.createProvider(getServletConfig());
		this.checked = false;
	}
%><%
	final CopernicaProvider provider = this.provider;
	
	if(!this.checked)
	{
		provider.getModel().checkDatabase();
		this.checked = true;
	}
	
	final CopernicaUser user = Util.checkAccess(provider, request);
	final CopernicaCop cop = CopernicaCop.getCop(provider, request);
	final CopernicaLanguage language = cop.language;
	
%><html>
	<head>
		<title><%=cop.getTitle()%></title><%
		
		final CopernicaCop home = cop.toHome();
		%>
		<link rel="home" href="<%=home%>"><%
		final CopernicaCop prev = cop.toPrev();
		if(prev!=null)
		{
		%>
		<link rel="prev" href="<%=prev%>"><%
		}
		final CopernicaCop next = cop.toNext();
		if(next!=null)
		{
		%>
		<link rel="next" href="<%=next%>"><%
		}
		
		%>
		<link rel="STYLESHEET" type="text/css" href="copernica.css">
	</head>
	<body>
		<div id="langcontainer">
			<ul id="langlist"><%
			for(Iterator l = provider.getDisplayLanguages().iterator(); l.hasNext(); )
			{
				final CopernicaLanguage currentLanguage = (CopernicaLanguage)l.next();
				final String name = currentLanguage.getCopernicaName(language);
				final boolean active = currentLanguage.equals(language);
				%>
				<li<%if(active){%> id="active"<%}%>>
					<a href="<%=cop.switchLanguage(currentLanguage)%>"<%
						if(active){%> id="current"<%}%>><%=name%></a>
				</li><%
			}
			%>
			<li>
				<%=user.getCopernicaName()%>
			</li>
			</ul>
		</div><%
		Copernica_Jspm.writeTypeList(out, cop);
		%>
		<div id="main"><%
		if(cop instanceof TypeCop)
		{
			final TypeCop typeCop = ((TypeCop)cop);
			TypeCop_Jspm.writeBody(out, typeCop);
		}
		else if(cop instanceof ItemCop)
		{
			final ItemCop itemCop = (ItemCop)cop;
			ItemCop_Jspm.writeBody(out, itemCop, request);
		}
		else if(cop instanceof EmptyCop)
		{
			%>
			<h1>Copernica</h1>
			<h2>Generic Backoffice for COPE</h2>
			<img src="exedio.png" width="200" height="66"><%
		}
		else
			throw new RuntimeException();
		//cop.log();
		%>
		</div>
	</body>
</html>
