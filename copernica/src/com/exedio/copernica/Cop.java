package com.exedio.copernica;


abstract class Cop
{
	private final String url;
	
	Cop(final String url)
	{
		this.url = url;
	}
	
	public final String toString()
	{
		return url;
	}
	
}
