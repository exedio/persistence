package com.exedio.copernica;

import com.exedio.cope.lib.Item;

final class ItemCop extends CopernicaCop
{
	final Item item;

	ItemCop(final Language language, final Item item)
	{
		super(language);
		this.item = item;
		addParameter("item", item.getID());
	}
	
	final  CopernicaCop switchLanguage(final Language newLanguage)
	{
		return new ItemCop(newLanguage, item);
	}

}
