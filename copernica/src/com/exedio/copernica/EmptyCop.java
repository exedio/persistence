package com.exedio.copernica;

import com.exedio.cope.lib.Type;


final class EmptyCop extends CopernicaCop
{
	EmptyCop(final CopernicaProvider provider, final CopernicaLanguage language)
	{
		super(provider, language);
	}
	
	final  CopernicaCop switchLanguage(final CopernicaLanguage newLanguage)
	{
		return new EmptyCop(provider, newLanguage);
	}

	final boolean isType(final Type type)
	{
		return false;
	}
	
	final String getTitle()
	{
		return "Copernica";
	}

}
