package com.exedio.copernica;

import java.util.Collection;
import java.util.Map;

import com.exedio.cope.lib.Function;
import com.exedio.cope.lib.Query;
import com.exedio.cope.lib.Search;
import com.exedio.cope.lib.Type;

final class TypeCop extends CopernicaCop
{
	final Type type;
	final Function orderBy; 
	final int start;
	final int count;

	TypeCop(final Language language, final Type type)
	{
		this(language, type, null, 0, 10);
	}
	
	TypeCop(final Language language, final Type type,
					final Function orderBy,
					final int start, final int count)
	{
		super(language);
		this.type = type;
		this.orderBy = orderBy;
		this.start = start;
		this.count = count;
		addParameter(TYPE, type.getID());
		if(orderBy!=null)
			addParameter(ORDER, orderBy.getName());
		if(start!=0)
			addParameter(START, String.valueOf(start));
		if(count!=10)
			addParameter(COUNT, String.valueOf(count));
	}
	
	final  CopernicaCop switchLanguage(final Language newLanguage)
	{
		return new TypeCop(newLanguage, type, orderBy, start, count);
	}
	
	final boolean isFirstPage()
	{
		return start == 0;
	}
	
	final TypeCop firstPage()
	{
		return new TypeCop(language, type, orderBy, 0, count);
	}
	
	final TypeCop previousPage()
	{
		int newStart = start - count;
		if(newStart<0)
			newStart = 0;
		return new TypeCop(language, type, orderBy, newStart, count);
	}
	
	final TypeCop nextPage()
	{
		int newStart = start + count;
		return new TypeCop(language, type, orderBy, newStart, count);
	}
	
	final TypeCop switchCount(final int newCount)
	{
		return new TypeCop(language, type, orderBy, start, newCount);
	}
	
	final TypeCop orderBy(final Function newOrderBy) 
	{
		return new TypeCop(language, type, newOrderBy, start, count);
	}
	
	final Collection search()
	{
		final Query query = new Query(type, null);
		if(orderBy!=null)
			query.setOrderBy(orderBy);
		query.setRange(start, count);
		return Search.search(query);
	}

	static final TypeCop getCop(
			final Language language,
			final String typeID,
			final Map parameterMap)
	{
		final Type type = Type.findByID(typeID);
		if(type==null)
			throw new RuntimeException("type "+typeID+" not available");

		final String orderID = getParameter(parameterMap, ORDER);
		final Function orderAttribute = (orderID==null) ? null : (Function)type.getFeature(orderID);

		final String startString = getParameter(parameterMap, START);
		final String countString = getParameter(parameterMap, COUNT);
		final int start = (startString==null) ?  0 : Integer.parseInt(startString);
		final int count = (countString==null) ? 10 : Integer.parseInt(countString);

		return new TypeCop(language, type, orderAttribute, start, count);
	}
}
