<%@
page import="com.exedio.cope.lib.Model"
%><%!
	
	private CopernicaProvider providerLazilyInitialized;
	private final Object providerLock = new Object();
	
%><%
	
	synchronized(providerLock)
	{
		if(providerLazilyInitialized==null)
		{
			providerLazilyInitialized = Util.createProvider(
				getInitParameter("com.exedio.copernica.provider"));
			
			final Model model = providerLazilyInitialized.getModel();
			if(!model.hasProperties())
			{
				model.setProperties(
					new com.exedio.cope.lib.Properties(
						application.getRealPath("WEB-INF/cope.properties")
					)
				);
			}
		}
	}
	
	final CopernicaProvider provider = providerLazilyInitialized;
	
%>