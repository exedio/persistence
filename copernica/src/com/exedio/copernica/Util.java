package com.exedio.copernica;

import java.lang.reflect.Field;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.lib.Model;
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
	
	static final Model getModel(final ServletConfig config)
	{
		try
		{
			final String modelName = config.getInitParameter("com.exedio.copernica.model");
			if(modelName==null)
				throw new NullPointerException("init-param com.exedio.copernica.model missing");

			final int pos = modelName.indexOf('#');
			if(pos<=0)
				throw new RuntimeException("init-param com.exedio.copernica.model does not contain '#', but was "+modelName);
			final String modelClassName = modelName.substring(0, pos);
			final String modelAttributeName = modelName.substring(pos+1);

			final Class modelClass = Class.forName(modelClassName);
			final Field modelField = modelClass.getField(modelAttributeName);
			final Model model = (Model)modelField.get(null);
			TransientCopernicaProvider.initialize(model, config);
			return model;
		}
		catch(ClassNotFoundException e)
		{
			throw new NestingRuntimeException(e);
		}
		catch(NoSuchFieldException e)
		{
			throw new NestingRuntimeException(e);
		}
		catch(IllegalAccessException e)
		{
			throw new NestingRuntimeException(e);
		}
	}
	
	private static final String BASIC = "Basic ";
	
	static final CopernicaUser checkAccess( // TODO: put method into Cop
			final CopernicaProvider provider,
			final HttpServletRequest request)
		throws CopernicaAuthorizationFailedException
	{
		final String authorization = request.getHeader("Authorization");
		//System.out.println("authorization:"+authorization);
		if(authorization==null)
			throw new CopernicaAuthorizationFailedException("noauth");
		if(!authorization.startsWith(BASIC))
			throw new CopernicaAuthorizationFailedException("nonbasic", authorization);
		
		final String basicCookie = authorization.substring(BASIC.length()); // TODO: make a constant
		//System.out.println("basicCookie:"+basicCookie);
		
		final String basicCookiePlain = new String(Base64.decode(basicCookie));
		//System.out.println("basicCookiePlain:"+basicCookiePlain);
		
		final int colon = basicCookiePlain.indexOf(':');
		if(colon<=0 || colon+1>=basicCookiePlain.length())
			throw new CopernicaAuthorizationFailedException("badcol", basicCookiePlain);
		
		final String userid = basicCookiePlain.substring(0, colon);
		final String password = basicCookiePlain.substring(colon+1);
		//System.out.println("userid:"+userid);
		//System.out.println("password:"+password);

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
