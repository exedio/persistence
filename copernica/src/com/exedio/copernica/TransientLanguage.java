package com.exedio.copernica;

import java.util.HashMap;

public class TransientLanguage
	extends TransientComponent
	implements CopernicaLanguage
{
	final String nullName;
	final String onName;
	final String offName;

	final HashMap enumerationValueNames = new HashMap();
	
	public TransientLanguage(final String id, final String nullName, final String onName, final String offName)
	{
		super(id);
		this.nullName = nullName;
		this.onName = onName;
		this.offName = offName;
	}
	
}
