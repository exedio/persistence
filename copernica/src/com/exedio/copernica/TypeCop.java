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
	final boolean orderAscending;
	final int start;
	final int count;

	TypeCop(final Language language, final Type type)
	{
		this(language, type, null, true, 0, 10);
	}
	
	TypeCop(final Language language, final Type type,
					final Function orderBy, final boolean orderAscending,
					final int start, final int count)
	{
		super(language);
		this.type = type;
		this.orderBy = orderBy;
		this.orderAscending = orderAscending;
		this.start = start;
		this.count = count;
		addParameter(TYPE, type.getID());
		if(orderBy!=null)
			addParameter(orderAscending ? ORDER_ASCENDING : ORDER_DESCENDING, orderBy.getName());
		if(start!=0)
			addParameter(START, String.valueOf(start));
		if(count!=10)
			addParameter(COUNT, String.valueOf(count));
	}
	
	final  CopernicaCop switchLanguage(final Language newLanguage)
	{
		return new TypeCop(newLanguage, type, orderBy, orderAscending, start, count);
	}
	
	final boolean isType(final Type type)
	{
		return this.type == type;
	}

	final String getTitle(final CopernicaProvider provider)
	{
		return provider.getDisplayName(language, type);
	}

	final boolean isFirstPage()
	{
		return start == 0;
	}
	
	final TypeCop firstPage()
	{
		return new TypeCop(language, type, orderBy, orderAscending, 0, count);
	}
	
	final TypeCop previousPage()
	{
		int newStart = start - count;
		if(newStart<0)
			newStart = 0;
		return new TypeCop(language, type, orderBy, orderAscending, newStart, count);
	}
	
	final TypeCop nextPage()
	{
		int newStart = start + count;
		return new TypeCop(language, type, orderBy, orderAscending, newStart, count);
	}
	
	final TypeCop switchCount(final int newCount)
	{
		return new TypeCop(language, type, orderBy, orderAscending, start, newCount);
	}
	
	final TypeCop orderBy(final Function newOrderBy, final boolean ascending) 
	{
		return new TypeCop(language, type, newOrderBy, ascending, start, count);
	}
	
	final Collection search()
	{
		final Query query = new Query(type, null);
		if(orderBy!=null)
			query.setOrderBy(orderBy, orderAscending);
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

		final String orderAscendingID = getParameter(parameterMap, ORDER_ASCENDING);
		final String orderDescendingID = getParameter(parameterMap, ORDER_DESCENDING);
		final boolean orderAscending = orderAscendingID!=null;
		final String orderID = orderAscending ? orderAscendingID : orderDescendingID;
		final Function orderBy = (orderID==null) ? null : (Function)type.getFeature(orderID);

		final String startString = getParameter(parameterMap, START);
		final String countString = getParameter(parameterMap, COUNT);
		final int start = (startString==null) ?  0 : Integer.parseInt(startString);
		final int count = (countString==null) ? 10 : Integer.parseInt(countString);

		return new TypeCop(language, type, orderBy, orderAscending, start, count);
	}
}
