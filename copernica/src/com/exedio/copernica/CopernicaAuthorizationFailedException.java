package com.exedio.copernica;

final class CopernicaAuthorizationFailedException extends Exception
{
	final String code;
	final String param;
	
	CopernicaAuthorizationFailedException(final String code)
	{
		super("authorization failed ["+code+"]");
		this.code = code;
		this.param = null;
	}

	CopernicaAuthorizationFailedException(final String code, final String param)
	{
		super("authorization failed ["+code+","+param+"]");
		this.code = code;
		this.param = param;
	}
	
	String getDisplayCode()
	{
		return param==null ? code : (code+"*"+param);
	}

}
