
package com.exedio.copernica;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class CopernicaServlet extends HttpServlet
{
	CopernicaProvider provider = null;
	boolean checked;

	private final Random random = new Random();
	
	final void printException(final PrintStream out, final Exception exception)
	{
		exception.printStackTrace(out);
		if(exception instanceof ServletException)
		{
			final Throwable rootCause =
				((ServletException)exception).getRootCause();
			if(rootCause!=null)
			{
				out.println("root cause for ServletException:");
				rootCause.printStackTrace(out);
			}
			else
			{
				out.println("no root cause for ServletException");
			}
		}
		out.flush();
	}
	
	/**
	 * Returns the id under with the exception has been reported in the log.
	 */
	final String reportException(final Exception exception)
	{
		final long idLong;
		synchronized(random)
		{
			idLong = random.nextLong();
		}
		final String id = String.valueOf(Math.abs(idLong));
		System.out.println("--------I"+id+"-----");
		printException(System.out, exception);
		System.out.println("--------O"+id+"-----");
		return id;
	}

	
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
		catch(Exception e)
		{
			final boolean onPage = "jo-man".equals(request.getParameter("display_error"));
			Copernica_Jspm.writeException(out, this, e, onPage);
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
