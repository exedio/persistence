package com.exedio.copernica;

import java.util.HashMap;

public class TransientLanguage implements Language
{
	private final String id;
	private final HashMap names = new HashMap();
	final HashMap enumerationValueNames = new HashMap();
	
	public TransientLanguage(final String id)
	{
		this.id = id;
	}
	
	public String getCopernicaID()
	{
		return id;
	}
	
	public void putName(final TransientLanguage language, final String name)
	{
		names.put(language, name);
	}
	
	public String getCopernicaName(final Language displayLanguage)
	{
		{
			final String name = (String)names.get(displayLanguage);
			if(name!=null)
				return name;
		}
		{
			final String name = (String)names.get(this);
			if(name!=null)
				return name;
		}
		return id;
	}

	public String getCopernicaIconURL()
	{
		return null;
	}

}
