<%@
page isErrorPage="true"

%><%
response.addHeader("WWW-Authenticate", "Basic realm=\"Copernica\"");
response.setStatus(response.SC_UNAUTHORIZED);
final CopernicaAuthorizationFailedException cafe =
	(CopernicaAuthorizationFailedException)exception;

%><html>
	<head>
		<title>Copernica - Authorization Required</title>
	</head>
	<body>
		<h1>Authorization Required.</h1>
		<div>
			Copernica could not verify that you are authorized to access
			the application. Either you supplied the wrong credentials
			(e.g., bad password), or your browser doesn't understand how
			to supply the credentials required.
		</div>
		<div>
			If you think, this error is unjustified, please report the
			error id <code><%=cafe.getDisplayCode()%></code> with your
			complaint.
		</div>
	</body>
</html>
