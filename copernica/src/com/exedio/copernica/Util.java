package com.exedio.copernica;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.lib.NestingRuntimeException;

final class Util
{
		
	static final CopernicaProvider createProvider(final ServletConfig config)
	{
		try
		{
			final String providerName = config.getInitParameter("com.exedio.copernica.provider");
			if(providerName==null)
				throw new NullPointerException("init-param com.exedio.copernica.provider missing");
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
	
	static final CopernicaUser checkAccess(
			final CopernicaProvider provider,
			final HttpServletRequest request,
			final HttpServletResponse response)
	{
		final CopernicaUser result = checkAccessPrivate(provider, request);
		if(result==null)
		{
			response.addHeader("WWW-Authenticate", "Basic realm=\"Copernica\"");
			response.setStatus(response.SC_UNAUTHORIZED);
		}
		return result;
	}

	private static final String BASIC = "Basic ";
	
	private static final CopernicaUser checkAccessPrivate(
			final CopernicaProvider provider,
			final HttpServletRequest request)
	{
		final String authorization = request.getHeader("Authorization");
		//System.out.println("authorization:"+authorization);
		if(authorization==null || !authorization.startsWith(BASIC))
			return null;
		
		final String basicCookie = authorization.substring(BASIC.length());
		//System.out.println("basicCookie:"+basicCookie);
		
		final String basicCookiePlain = new String(Base64.decode(basicCookie));
		//System.out.println("basicCookiePlain:"+basicCookiePlain);
		
		final int colon = basicCookiePlain.indexOf(':');
		if(colon<=0 || colon+1>=basicCookiePlain.length())
			return null;
		
		final String userid = basicCookiePlain.substring(0, colon);
		final String password = basicCookiePlain.substring(colon+1);
		//System.out.println("userid:"+userid);
		//System.out.println("password:"+password);

		final CopernicaUser user = provider.findUserByID(userid);
		//System.out.println("user:"+user);
		if(user==null)
			return null;
		
		return user.checkCopernicaPassword(password) ? user : null;
	}
	
	private Util()
	{}

}
