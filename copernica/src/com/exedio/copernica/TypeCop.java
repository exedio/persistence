package com.exedio.copernica;

import java.util.Collection;

import com.exedio.cope.lib.Query;
import com.exedio.cope.lib.Search;
import com.exedio.cope.lib.Type;

final class TypeCop extends CopernicaCop
{
	final Type type;
	final int start;
	final int count;

	TypeCop(final Language language, final Type type)
	{
		this(language, type, 0, 10);
	}
	
	TypeCop(final Language language, final Type type, final int start, final int count)
	{
		super(language);
		this.type = type;
		this.start = start;
		this.count = count;
		addParameter("type", type.getID());
		if(start!=0)
			addParameter("start", String.valueOf(start));
		if(count!=10)
			addParameter("count", String.valueOf(count));
	}
	
	final  CopernicaCop switchLanguage(final Language newLanguage)
	{
		return new TypeCop(newLanguage, type);
	}
	
	final TypeCop previousPage()
	{
		int newStart = start - count;
		if(newStart<0)
			newStart = 0;
		return new TypeCop(language, type, newStart, count);
	}
	
	final TypeCop nextPage()
	{
		int newStart = start + count;
		return new TypeCop(language, type, newStart, count);
	}
	
	final Collection search()
	{
		final Query query = new Query(type, null);
		query.setRange(start, count);
		return Search.search(query);
	}

}
