
package com.exedio.copernica;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.lib.NestingRuntimeException;
import com.exedio.cops.Cop;

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
		
		this.provider = createProvider();
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

			final CopernicaUser user = checkAccess(request);
			final CopernicaCop cop = CopernicaCop.getCop(provider, request);
			Copernica_Jspm.write(out, request, user, cop);

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

	private final CopernicaProvider createProvider()
	{
		try
		{
			final ServletConfig config = getServletConfig();
			final String providerName = config.getInitParameter("provider");
			if(providerName==null)
				throw new NullPointerException("init-param 'provider' missing");
			final Class providerClass = Class.forName(providerName);
			final CopernicaProvider provider = (CopernicaProvider)providerClass.newInstance();
			provider.initialize(config);
			return provider;
		}
		catch(ClassNotFoundException e)
		{
			throw new NestingRuntimeException(e);
		}
		catch(InstantiationException e)
		{
			throw new NestingRuntimeException(e);
		}
		catch(IllegalAccessException e)
		{
			throw new NestingRuntimeException(e);
		}
	}
	
	private final CopernicaUser checkAccess(final HttpServletRequest request)
		throws CopernicaAuthorizationFailedException
	{
		final String[] authorization = Cop.authorizeBasic(request);
		if(authorization==null)
			throw new CopernicaAuthorizationFailedException("noauth");

		final String userid = authorization[0];
		final String password = authorization[1];

		final CopernicaUser user = provider.findUserByID(userid);
		//System.out.println("user:"+user);
		if(user==null)
			throw new CopernicaAuthorizationFailedException("nouser", userid);
		
		if(!user.checkCopernicaPassword(password))
			throw new CopernicaAuthorizationFailedException("badpass", userid);

		return user;
	}
}
