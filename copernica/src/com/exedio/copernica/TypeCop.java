package com.exedio.copernica;

import java.util.Collection;
import java.util.Map;

import com.exedio.cope.lib.ObjectAttribute;
import com.exedio.cope.lib.Query;
import com.exedio.cope.lib.Search;
import com.exedio.cope.lib.Type;

final class TypeCop extends CopernicaCop
{
	final Type type;
	final ObjectAttribute orderAttribute;
	final int start;
	final int count;

	TypeCop(final Language language, final Type type)
	{
		this(language, type, null, 0, 10);
	}
	
	TypeCop(final Language language, final Type type,
					final ObjectAttribute orderAttribute,
					final int start, final int count)
	{
		super(language);
		this.type = type;
		this.orderAttribute = orderAttribute;
		this.start = start;
		this.count = count;
		addParameter(TYPE, type.getID());
		if(orderAttribute!=null)
			addParameter(ORDER, orderAttribute.getName());
		if(start!=0)
			addParameter(START, String.valueOf(start));
		if(count!=10)
			addParameter(COUNT, String.valueOf(count));
	}
	
	final  CopernicaCop switchLanguage(final Language newLanguage)
	{
		return new TypeCop(newLanguage, type, orderAttribute, start, count);
	}
	
	final boolean isFirstPage()
	{
		return start == 0;
	}
	
	final TypeCop firstPage()
	{
		return new TypeCop(language, type, orderAttribute, 0, count);
	}
	
	final TypeCop previousPage()
	{
		int newStart = start - count;
		if(newStart<0)
			newStart = 0;
		return new TypeCop(language, type, orderAttribute, newStart, count);
	}
	
	final TypeCop nextPage()
	{
		int newStart = start + count;
		return new TypeCop(language, type, orderAttribute, newStart, count);
	}
	
	final TypeCop switchCount(final int newCount)
	{
		return new TypeCop(language, type, orderAttribute, start, newCount);
	}
	
	final TypeCop orderBy(final ObjectAttribute newOrderAttribute)
	{
		return new TypeCop(language, type, newOrderAttribute, start, count);
	}
	
	final Collection search()
	{
		final Query query = new Query(type, null);
		if(orderAttribute!=null)
			query.setOrderBy(orderAttribute);
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
		final ObjectAttribute orderAttribute = (orderID==null) ? null : (ObjectAttribute)type.getFeature(orderID);

		final String startString = getParameter(parameterMap, START);
		final String countString = getParameter(parameterMap, COUNT);
		final int start = (startString==null) ?  0 : Integer.parseInt(startString);
		final int count = (countString==null) ? 10 : Integer.parseInt(countString);

		return new TypeCop(language, type, orderAttribute, start, count);
	}
}
