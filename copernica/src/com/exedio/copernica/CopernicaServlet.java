
package com.exedio.copernica;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CopernicaServlet extends HttpServlet
{
	CopernicaProvider provider = null;
	boolean checked;
	
	public void init() throws ServletException
	{
		if(this.provider!=null)
		{
			System.out.println("reinvokation of jspInit");
			return;
		}
		
		this.provider = Util.createProvider(getServletConfig());
		this.checked = false;
	}

	private void doRequest(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		PrintStream out = null;
		try
		{
			response.setContentType("text/html");
			out = new PrintStream(response.getOutputStream());

			if(!checked)
			{
				provider.getModel().checkDatabase();
				checked = true;
			}

			Copernica_Jspm.write(out, request, provider);

			out.close();
		}
		catch(CopernicaAuthorizationFailedException e)
		{
			response.addHeader("WWW-Authenticate", "Basic realm=\"Copernica\"");
			response.setStatus(response.SC_UNAUTHORIZED);
			Copernica_Jspm.writeAuthenticationError(out, e);
		}
	}

	public void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		doRequest(request, response);
	}

	public void doPost(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		doRequest(request, response);
	}

}
