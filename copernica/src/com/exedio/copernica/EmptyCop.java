package com.exedio.copernica;


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

}
