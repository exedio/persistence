<%@
page import="com.exedio.copernica.CopernicaProvider"
%><%!
	
	CopernicaProvider provider = null;
	
	public final void jspInit()
	{
		if(this.provider!=null)
		{
			System.out.println("reinvokation of jspInit");
			return;
		}
		
		this.provider = Util.createProvider(getServletConfig());
	}
%><%
	final CopernicaProvider provider = this.provider;
%>