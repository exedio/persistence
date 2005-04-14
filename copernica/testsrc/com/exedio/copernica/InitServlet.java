package com.exedio.copernica;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cops.CopsServlet;

public class InitServlet extends CopsServlet
{
	final static String ENCODING = "ISO-8859-1";

	protected void doRequest(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		response.setContentType("text/html; charset="+ENCODING);

		final boolean initialize = (request.getParameter("INIT")!=null);
		if(initialize)
			CopernicaTestProvider.initializeExampleSystem();

		final PrintStream out = new PrintStream(response.getOutputStream(), false, ENCODING);
		Init_Jspm.write(out, initialize);
		out.close();
	}

}