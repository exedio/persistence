package com.exedio.copernica;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

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

	void writeBody(final JspWriter out, final HttpServletRequest request)
		throws IOException
	{
		Copernica_Jspm.writeEmptyBody(out);
	}

}
