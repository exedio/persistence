<%@
page isErrorPage="true" %><%@
page import="java.io.PrintWriter" %><%@
page import="java.io.Writer" %><%@
page import="java.io.OutputStreamWriter" %><%@
page import="java.util.Random"

%><%!

	private final Random random = new Random();
	
	private void printException(final Throwable exception, final Writer out)
	{
		final PrintWriter outPrinter = new PrintWriter(out);
		exception.printStackTrace(outPrinter);
		if(exception instanceof ServletException)
		{
			final Throwable rootCause =
				((ServletException)exception).getRootCause();
			if(rootCause!=null)
				rootCause.printStackTrace(outPrinter);
		}
		outPrinter.flush();
	}

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
			printException(exception, out);
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
			printException(exception, new OutputStreamWriter(System.out));
			System.out.println("--------O"+id+"-----");
		%>
		Please report the error code <i><%=id%></i> to the webmaster.<%
		}
		%>
	</body>
</html>