package com.exedio.copernica;

import java.util.Map;

import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.Type;


abstract class CopernicaCop extends Cop
{
	static final String LANGUAGE = "l";

	static final String TYPE = "t";
	static final String ORDER = "o";
	static final String START = "st";
	static final String COUNT = "ct";
	
	final static String ITEM = "i";


	final Language language;

	CopernicaCop(final Language language)
	{
		super("copernica.jsp");
		this.language = language;
		if(language!=null)
			addParameter(LANGUAGE, language.getCopernicaID());
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

	static final CopernicaCop getCop(final CopernicaProvider provider, final Map parameterMap)
	{	
		final String typeID = getParameter(parameterMap, TYPE);
		final String itemID = getParameter(parameterMap, ITEM);
		final String langID = getParameter(parameterMap, LANGUAGE);
		
		final Language language = (langID!=null) ? provider.findLanguageByUniqueID(langID) : null;
		if(typeID!=null)
		{
			return TypeCop.getCop(language, typeID, parameterMap);
		}
		else if(itemID!=null)
		{
			return ItemCop.getCop(language, itemID);
		}
		else
			return new EmptyCop(language);
	}
	
	
}
