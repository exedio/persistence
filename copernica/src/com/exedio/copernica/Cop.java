package com.exedio.copernica;



abstract class Cop
{
	private final StringBuffer url;
	private boolean first = true;
	
	Cop(final String jsp)
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
	
}
