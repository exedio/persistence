package com.exedio.copernica;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cops.Cop;

// TODO: move this class into CopernicaServlet
// TODO: should not be public
public final class Util
{
		
	static final CopernicaUser checkAccess(
			final CopernicaProvider provider,
			final HttpServletRequest request)
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
	
	private Util()
	{}

}
