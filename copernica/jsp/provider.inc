<%@
page import="javax.servlet.ServletConfig" %><%@

page import="com.exedio.copernica.CopernicaProvider" %><%@
page import="com.exedio.cope.lib.Model"
%><%!
	
	CopernicaProvider provider = null;
	
	public final void jspInit()
	{
		if(this.provider!=null)
		{
			System.out.println("reinvokation of jspInit");
			return;
		}
		
		final ServletConfig config = getServletConfig();
		final CopernicaProvider provider = Util.createProvider(
			config.getInitParameter("com.exedio.copernica.provider"));
		
		final Model model = provider.getModel();
		if(!model.hasProperties())
		{
			model.setProperties(
				new com.exedio.cope.lib.Properties(
					config.getServletContext().getRealPath("WEB-INF/cope.properties")
				)
			);
		}
		this.provider = provider;
	}
%><%
	final CopernicaProvider provider = this.provider;
%>