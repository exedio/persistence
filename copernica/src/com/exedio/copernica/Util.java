package com.exedio.copernica;

import com.exedio.cope.lib.NestingRuntimeException;

public class Util
{
		
	static final CopernicaProvider createProvider(final String providerName)
	{
		try
		{
			if(providerName==null)
				throw new NullPointerException("init-param com.exedio.copernica.provider missing");
			final Class providerClass = Class.forName(providerName);
			return (CopernicaProvider)providerClass.newInstance();
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
	
	private static final String BASIC = "Basic ";
	
	static final User checkAccess(final CopernicaProvider provider, final String authorization)
	{
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

		final User user = provider.findUserByCopernicaID(userid);
		//System.out.println("user:"+user);
		if(user==null)
			return null;
		
		return user.checkCopernicaPassword(password) ? user : null;
	}

	private Util()
	{}

}
