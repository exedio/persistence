package com.exedio.copernica;

import com.exedio.cope.lib.Item;

final class ItemCop extends Cop
{
	ItemCop(final Item item)
	{
		super("copernica.jsp?item="+item.getID());
	}
	
}
