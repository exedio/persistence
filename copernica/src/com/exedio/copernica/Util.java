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

	private Util()
	{}

}
