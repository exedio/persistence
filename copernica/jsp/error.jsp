<%@
page isErrorPage="true" %><%@
page import="java.io.PrintWriter" %><%@
page import="java.util.Random"

%><%!

	private final Random random = new Random();

%><html>
	<head>
		<title>Copernica System Error</title>
	</head>
	<body>
		<b>Sorry, an internal error occurred.</b><br><%
		if("jo-man".equals(request.getParameter("display_error")))
		{
		%>
		<hr>
		<font color="#ff0000">
		<pre>
<%
			final PrintWriter outPrinter = new PrintWriter(out);
			exception.printStackTrace(outPrinter);
			if(exception instanceof ServletException)
				((ServletException)exception).getRootCause().printStackTrace(outPrinter);
		%>
		</pre>
		</font>
		<hr><%
		}
		else
		{
			final long idLong;
			synchronized(random)
			{
				idLong = random.nextLong();
			}
			final String id = String.valueOf(Math.abs(idLong));
			System.out.println("--------I"+id+"-----");
			exception.printStackTrace(System.out);
			if(exception instanceof ServletException)
				((ServletException)exception).getRootCause().printStackTrace(System.out);
			System.out.println("--------O"+id+"-----");
		%>
		Please report the error code <i><%=id%></i> to the webmaster.<%
		}
		%>
	</body>
</html>