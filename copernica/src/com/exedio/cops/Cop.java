package com.exedio.cops;

import javax.servlet.http.HttpServletRequest;

public abstract class Cop
{
	private final StringBuffer url;
	private boolean first = true;
	
	public Cop(final String jsp)
	{
		this.url = new StringBuffer(jsp);
	}
	
	protected void addParameter(final String key, final String value)
	{
		if(first)
		{
			url.append('?');
			first = false;
		}
		else
			url.append('&');
			
		url.append(key);
		url.append('=');
		url.append(value);
	}
	
	public final String toString()
	{
		return url.toString();
	}
	
	private static final String BASIC = "Basic ";
	private static final int BASIC_LENGTH = BASIC.length();
	
	public static final String[] authorizeBasic(final HttpServletRequest request)
	{
		final String authorization = request.getHeader("Authorization");
		//System.out.println("authorization:"+authorization);
		if(authorization==null||!authorization.startsWith(BASIC))
			return null;
		
		final String basicCookie = authorization.substring(BASIC_LENGTH);
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
		return new String[]{userid, password};
	}
	
}
