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
	
}
