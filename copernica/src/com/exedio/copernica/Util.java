package com.exedio.copernica;

import java.lang.reflect.Field;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.lib.Model;
import com.exedio.cope.lib.NestingRuntimeException;
import com.exedio.cops.Cop;

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
