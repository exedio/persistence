<%@
page import="java.io.File" %><%@
page import="java.util.Properties" %><%@

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
		this.provider = Util.createProvider(
			config.getInitParameter("com.exedio.copernica.provider"));
		
		final Model model = provider.getModel();
		if(!model.hasProperties())
		{
			final ServletContext context = config.getServletContext();
			
			final String propertiesString = config.getInitParameter("com.exedio.copernica.properties");
			if(propertiesString==null)
				throw new RuntimeException("servlet parameter com.exedio.copernica.properties must be set");
			
			final File propertyFile = new File(context.getRealPath(propertiesString));
			
			final Properties p = com.exedio.cope.lib.Properties.loadProperties(propertyFile);
			if("//WEB-APP//".equals(p.getProperty("media.directory")))
			{
				final String mediaUrl = p.getProperty("media.url");
				if(mediaUrl==null)
					throw new RuntimeException("parameter media.url must exist in "+propertyFile.getAbsolutePath());
				
				p.setProperty("media.directory", context.getRealPath(mediaUrl));
			}
			
			model.setProperties(
				new com.exedio.cope.lib.Properties(p, propertyFile.getAbsolutePath()));
		}
	}
%><%
	final CopernicaProvider provider = this.provider;
%>