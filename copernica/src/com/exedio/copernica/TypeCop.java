package com.exedio.copernica;

import com.exedio.cope.lib.Type;

final class TypeCop extends CopernicaCop
{
	final Type type;

	TypeCop(final Language language, final Type type)
	{
		super(language);
		this.type = type;
		addParameter("type", type.getJavaClass().getName());
	}
	
	final  CopernicaCop switchLanguage(final Language newLanguage)
	{
		return new TypeCop(newLanguage, type);
	}

}
