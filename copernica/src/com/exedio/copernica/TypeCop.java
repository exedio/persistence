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
		return new TypeCop(newLanguage, type, start, count);
	}
	
	final boolean isFirstPage()
	{
		return start == 0;
	}
	
	final TypeCop firstPage()
	{
		return new TypeCop(language, type, 0, count);
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
	
	final TypeCop switchCount(final int newCount)
	{
		return new TypeCop(language, type, start, newCount);
	}
	
	final Collection search()
	{
		final Query query = new Query(type, null);
		query.setRange(start, count);
		return Search.search(query);
	}

	static final TypeCop getCop(
			final Language language,
			final String typeID,
			final String startString, final String countString)
	{
		final Type type = Type.findByID(typeID);
		if(type==null)
			throw new RuntimeException("type "+typeID+" not available");
		final int start = (startString==null) ?  0 : Integer.parseInt(startString);
		final int count = (countString==null) ? 10 : Integer.parseInt(countString);
		return new TypeCop(language, type, start, count);
	}
}
