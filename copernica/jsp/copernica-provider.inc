<%!
	
	private CopernicaProvider providerLazilyInitialized;
	private final Object providerLock = new Object();
	
%><%
	
	synchronized(providerLock)
	{
		if(providerLazilyInitialized==null)
		{
			providerLazilyInitialized = Util.createProvider(
				getInitParameter("com.exedio.copernica.provider"));
		}
	}
	
	final CopernicaProvider provider = providerLazilyInitialized;
	
%>