<%@
page import="com.exedio.copernica.CopernicaTestProvider" %><%

%><html>
	<head>
		<title>
			Copernica - Database Administration
		</title>
	</head>
	<body>
		<h1>Copernica</h1>
		<h2>Generic Backoffice for COPE</h2>
		<h3>Example Initialization for Test Model</h3>

		<form action="init.jsp" method="POST">
			Database:
			<br>
			<input type="submit" name="INIT" value="init" />
			<%
				if(request.getParameter("INIT")!=null)
				{
					CopernicaTestProvider.initializeExampleSystem();
					%>Example system successfully created!<%
				}
			%>
		</form>
	</body>
</html>
