package com.exedio.copernica;

import com.exedio.cope.lib.Item;

final class ItemCop
{
	private final String url;
	
	ItemCop(final Item item)
	{
		this.url = "copernica.jsp?item="+item.getID();
	}
	
	public final String toString()
	{
		return url;
	}
	
}
