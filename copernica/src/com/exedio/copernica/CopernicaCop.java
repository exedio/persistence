package com.exedio.copernica;

import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.NoSuchIDException;
import com.exedio.cope.lib.Search;
import com.exedio.cope.lib.SystemException;
import com.exedio.cope.lib.Type;


abstract class CopernicaCop extends Cop
{
	final Language language;

	CopernicaCop(final Language language)
	{
		super("copernica.jsp");
		this.language = language;
		if(language!=null)
			addParameter("lang", language.getCopernicaID());
	}
	
	abstract CopernicaCop switchLanguage(Language newLanguage);
	
	final TypeCop toType(final Type newType)
	{
		return new TypeCop(language, newType);
	}
	
	final ItemCop toItem(final Item newItem)
	{
		return new ItemCop(language, newItem);
	}

	static final CopernicaCop getCop(
			final CopernicaProvider provider,
			final String typeID, final String itemID,
			final String langID,
			final String startString, final String countString)
	{	
		final Language language = (langID!=null) ? provider.findLanguageByUniqueID(langID) : null;
		if(typeID!=null)
		{
			final Type type = Type.findByID(typeID);
			if(type==null)
				throw new RuntimeException("type "+typeID+" not available");
			final int start = (startString==null) ?  0 : Integer.parseInt(startString);
			final int count = (countString==null) ? 10 : Integer.parseInt(countString);
			return new TypeCop(language, type, start, count);
		}
		else if(itemID!=null)
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
		else
			return new EmptyCop(language);
	}
	
	
}
