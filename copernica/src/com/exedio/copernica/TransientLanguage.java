package com.exedio.copernica;


public class TransientLanguage implements Language
{
	final String id;
	
	public TransientLanguage(final String id)
	{
		this.id = id;
	}
	
	public String getCopernicaID()
	{
		return id;
	}
	
	public String getCopernicaName(final Language displayLanguage)
	{
		return id;
	}

	public String getCopernicaIconURL()
	{
		return null;
	}

}
