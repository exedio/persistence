package com.exedio.copernica;

public class CopernicaAuthorizationFailedException extends Exception
{
	final String code;
	final String param;
	
	public CopernicaAuthorizationFailedException(final String code)
	{
		super("authorization failed ["+code+"]");
		this.code = code;
		this.param = null;
	}

	public CopernicaAuthorizationFailedException(final String code, final String param)
	{
		super("authorization failed ["+code+","+param+"]");
		this.code = code;
		this.param = param;
	}
	
	public String getDisplayCode()
	{
		return param==null ? code : (code+"*"+param);
	}

}
