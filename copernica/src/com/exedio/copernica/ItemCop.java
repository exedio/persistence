package com.exedio.copernica;

import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.Model;
import com.exedio.cope.lib.NestingRuntimeException;
import com.exedio.cope.lib.NoSuchIDException;
import com.exedio.cope.lib.Type;

final class ItemCop extends CopernicaCop
{
	final Item item;
	
	ItemCop(final Language language, final Item item)
	{
		super(language);
		this.item = item;
		addParameter(ITEM, item.getID());
	}
	
	final  CopernicaCop switchLanguage(final Language newLanguage)
	{
		return new ItemCop(newLanguage, item);
	}

	final boolean isType(final Type type)
	{
		return item.getType() == type;
	}

	final String getTitle(final CopernicaProvider provider)
	{
		return provider.getDisplayName(language, item);
	}

	static final ItemCop getCop(final Model model, final Language language, final String itemID)
	{	
		try
		{
			final Item item = model.findByID(itemID);
			return new ItemCop(language, item);
		}
		catch(NoSuchIDException e)
		{
			throw new NestingRuntimeException(e);
		}
	}

}
