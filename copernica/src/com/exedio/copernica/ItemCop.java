package com.exedio.copernica;

import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.NoSuchIDException;
import com.exedio.cope.lib.Search;
import com.exedio.cope.lib.SystemException;

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

	static final ItemCop getCop(final Language language, final String itemID)
	{	
		try
		{
			final Item item = Search.findByID(itemID);
			return new ItemCop(language, item);
		}
		catch(NoSuchIDException e)
		{
			throw new SystemException(e);
		}
	}

}
