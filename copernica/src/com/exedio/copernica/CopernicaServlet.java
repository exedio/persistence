
package com.exedio.copernica;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.lib.NestingRuntimeException;
import com.exedio.cops.Cop;
import com.exedio.cops.CopsServlet;

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
public final class CopernicaServlet extends CopsServlet
{
	private final static String ENCODING = "ISO-8859-1";

	CopernicaProvider provider = null;
	boolean checked;

	
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

	protected void doRequest(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		PrintStream out = null;
		try
		{
			response.setContentType("text/html; charset="+ENCODING);

			if(!checked)
			{
				provider.getModel().checkDatabase();
				checked = true;
			}

			final CopernicaUser user = checkAccess(request);
			final CopernicaCop cop = CopernicaCop.getCop(provider, request);
			cop.init(request);

			out = new PrintStream(response.getOutputStream(), false, ENCODING);
			Copernica_Jspm.write(out, request, user, cop);
			out.close();
		}
		catch(CopernicaAuthorizationFailedException e)
		{
			if(out==null)
				out = new PrintStream(response.getOutputStream(), false, ENCODING);

			response.addHeader("WWW-Authenticate", "Basic realm=\"Copernica\"");
			response.setStatus(response.SC_UNAUTHORIZED);
			Copernica_Jspm.writeAuthenticationError(out, e);
		}
		catch(Exception e)
		{
			if(out==null)
				out = new PrintStream(response.getOutputStream(), false, ENCODING);

			provider.handleException(out, this, request, e);
		}
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
