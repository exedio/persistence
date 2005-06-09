/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.copernica;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.NestingRuntimeException;
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
 * 
 * @author Ralf Wiebicke
 */
public final class CopernicaServlet extends CopsServlet
{
	final static String ENCODING = "utf-8";

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
			request.setCharacterEncoding(ENCODING);
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

			Cop.rejectAuthorizeBasic(response, "Copernica");
			Copernica_Jspm.writeAuthenticationError(out, e);
		}
		catch(Exception e)
		{
			response.setStatus(response.SC_INTERNAL_SERVER_ERROR);
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
