
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

/**
 * The servlet providing Copernica, the Generic Backoffice for COPE.
 * 
 * In order to use it, you have to deploy the servlet in your <code>web.xml</code>,
 * providing the name of the copernica provider via an init-parameter.
 * Typically, your <code>web.xml</code> would contain a snippet like this:  
 *  
 * <pre>
 * &lt;servlet&gt;
 *    &lt;servlet-name&gt;copernica&lt;/servlet-name&gt;
 *    &lt;servlet-class&gt;com.exedio.copernica.CopernicaServlet&lt;/servlet-class&gt;
 *    &lt;init-param&gt;
 *       &lt;param-name&gt;provider&lt;/param-name&gt;
 *       &lt;param-value&gt;{@link CopernicaProvider com.bigbusiness.shop.ShopProvider}&lt;/param-value&gt;
 *    &lt;/init-param&gt;
 * &lt;/servlet&gt;
 * &lt;servlet-mapping&gt;
 *    &lt;servlet-name&gt;copernica&lt;/servlet-name&gt;
 *    &lt;url-pattern&gt;/copernica.jsp&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre>
 */
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
			cop.init(request);
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

	protected final void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		doRequest(request, response);
	}

	protected final void doPost(
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
