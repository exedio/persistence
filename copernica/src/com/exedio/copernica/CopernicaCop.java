package com.exedio.copernica;

import com.exedio.cope.lib.Item;
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
			return TypeCop.getCop(language, typeID, startString, countString);
		}
		else if(itemID!=null)
		{
			return ItemCop.getCop(language, itemID);
		}
		else
			return new EmptyCop(language);
	}
	
	
}
