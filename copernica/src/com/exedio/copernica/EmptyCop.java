package com.exedio.copernica;

import com.exedio.cope.lib.Type;


final class EmptyCop extends CopernicaCop
{
	EmptyCop(final Language language)
	{
		super(language);
	}
	
	final  CopernicaCop switchLanguage(final Language newLanguage)
	{
		return new EmptyCop(newLanguage);
	}

	final boolean isType(final Type type)
	{
		return false;
	}

}
