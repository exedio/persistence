package com.exedio.copernica;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.NestingRuntimeException;
import com.exedio.cope.lib.NoSuchIDException;
import com.exedio.cope.lib.Type;

final class ItemCop extends CopernicaCop
{
	final Item item;
	
	ItemCop(final CopernicaProvider provider, final CopernicaLanguage language, final Item item)
	{
		super(provider, language);
		this.item = item;
		addParameter(ITEM, item.getID());
	}
	
	final CopernicaCop switchLanguage(final CopernicaLanguage newLanguage)
	{
		return new ItemCop(provider, newLanguage, item);
	}

	final boolean isType(final Type type)
	{
		return item.getType() == type;
	}

	final String getTitle()
	{
		return provider.getDisplayName(this, language, item);
	}

	void writeBody(final PrintStream out, final HttpServletRequest request)
		throws IOException
	{
		ItemCop_Jspm.writeBody(out, this, request);
	}

	static final ItemCop getCop(final CopernicaProvider provider, final CopernicaLanguage language, final String itemID)
	{	
		try
		{
			final Item item = provider.getModel().findByID(itemID);
			return new ItemCop(provider, language, item);
		}
		catch(NoSuchIDException e)
		{
			throw new NestingRuntimeException(e);
		}
	}

}
