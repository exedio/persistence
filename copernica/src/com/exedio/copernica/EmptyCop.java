package com.exedio.copernica;

import java.io.IOException;
import java.io.PrintStream;

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

	void writeBody(final PrintStream out)
		throws IOException
	{
		Copernica_Jspm.writeEmptyBody(out);
	}

}
