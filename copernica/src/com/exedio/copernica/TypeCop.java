package com.exedio.copernica;

import java.util.Collection;

import com.exedio.cope.lib.Search;
import com.exedio.cope.lib.Type;

final class TypeCop extends CopernicaCop
{
	final Type type;

	TypeCop(final Language language, final Type type)
	{
		super(language);
		this.type = type;
		addParameter("type", type.getID());
	}
	
	final  CopernicaCop switchLanguage(final Language newLanguage)
	{
		return new TypeCop(newLanguage, type);
	}
	
	final Collection search()
	{
		return Search.search(type, null);
	}

}
