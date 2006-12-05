/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.copernica;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Feature;
import com.exedio.cope.Function;
import com.exedio.cope.Query;
import com.exedio.cope.StatementInfo;
import com.exedio.cope.Type;

final class TypeCop extends CopernicaCop
{
	final Type type;
	final Function orderBy;
	final boolean orderAscending;
	final int limitStart;
	final int limitCount;

	private Query.Result queryResult = null;
	private StatementInfo statementInfo;

	TypeCop(final CopernicaProvider provider, final CopernicaLanguage language, final Type type)
	{
		this(provider, language, type, null, true, 0, 10);
	}
	
	TypeCop(final CopernicaProvider provider, final CopernicaLanguage language, final Type type,
					final Function orderBy, final boolean orderAscending,
					final int limitStart, int limitCount)
	{
		super(provider, language);
		
		final int limitCountCeiling = provider.getLimitCountCeiling(type);
		if(limitCount>limitCountCeiling)
			limitCount = limitCountCeiling;

		this.type = type;
		this.orderBy = orderBy;
		this.orderAscending = orderAscending;
		this.limitStart = limitStart;
		this.limitCount = limitCount;

		addParameter(TYPE, type.getID());
		// orderBy must be a feature
		if(orderBy!=null)
			addParameter(orderAscending ? ORDER_ASCENDING : ORDER_DESCENDING, ((Feature)orderBy).getName());
		if(limitStart!=0)
			addParameter(START, String.valueOf(limitStart));
		if(limitCount!=10)
			addParameter(COUNT, String.valueOf(limitCount));
	}
	
	@Override
	final CopernicaCop switchLanguage(final CopernicaLanguage newLanguage)
	{
		return new TypeCop(provider, newLanguage, type, orderBy, orderAscending, limitStart, limitCount);
	}
	
	@Override
	final boolean isType(final Type type)
	{
		return this.type == type;
	}

	@Override
	final String getTitle()
	{
		return provider.getDisplayName(language, type);
	}

	@Override
	final CopernicaCop toPrev()
	{
		return limitStart==0 ? null : previousPage();
	}
	
	@Override
	final CopernicaCop toNext()
	{
		computeItems();
		return isLastPage() ? null : nextPage();
	}
	
	final boolean isFirstPage()
	{
		return limitStart == 0;
	}
	
	final boolean isLastPage()
	{
		computeItems();
		return (limitStart+limitCount)>=queryResult.getCountWithoutLimit();
	}
	
	final TypeCop firstPage()
	{
		return new TypeCop(provider, language, type, orderBy, orderAscending, 0, limitCount);
	}
	
	final TypeCop lastPage()
	{
		computeItems();
		return new TypeCop(provider, language, type, orderBy, orderAscending, ((queryResult.getCountWithoutLimit()-1)/limitCount)*limitCount, limitCount);
	}
	
	final TypeCop previousPage()
	{
		int newStart = limitStart - limitCount;
		if(newStart<0)
			newStart = 0;
		return new TypeCop(provider, language, type, orderBy, orderAscending, newStart, limitCount);
	}
	
	final TypeCop nextPage()
	{
		int newStart = limitStart + limitCount;
		return new TypeCop(provider, language, type, orderBy, orderAscending, newStart, limitCount);
	}
	
	final TypeCop switchCount(final int newCount)
	{
		return new TypeCop(provider, language, type, orderBy, orderAscending, limitStart, newCount);
	}
	
	final TypeCop orderBy(final Function newOrderBy, final boolean ascending)
	{
		return new TypeCop(provider, language, type, newOrderBy, ascending, limitStart, limitCount);
	}
	
	final List getItems()
	{
		computeItems();
		return queryResult.getData();
	}

	final int getTotal()
	{
		computeItems();
		return queryResult.getCountWithoutLimit();
	}

	final StatementInfo getStatementInfo()
	{
		computeItems();
		return statementInfo;
	}

	private final void computeItems()
	{
		if(queryResult!=null)
			return;
		
		final Query query = type.newQuery(null);
		if(orderBy!=null)
			query.setOrderByAndThis(orderBy, orderAscending);
		else
			query.setOrderByThis(true);
		query.setLimit(limitStart, limitCount);
		query.enableMakeStatementInfo();
		
		queryResult = query.searchAndCountWithoutLimit();
		statementInfo = query.getStatementInfo();
	}
	
	@Override
	void writeBody(final HttpServletRequest request, final PrintStream out)
		throws IOException
	{
		TypeCop_Jspm.writeBody(out, this);
	}

	static final TypeCop getCop(
			final CopernicaProvider provider,
			final CopernicaLanguage language,
			final String typeID,
			final HttpServletRequest request)
	{
		final Type type = provider.getModel().findTypeByID(typeID);
		if(type==null)
			throw new RuntimeException("type "+typeID+" not available");

		final String orderAscendingID = request.getParameter(ORDER_ASCENDING);
		final String orderDescendingID = request.getParameter(ORDER_DESCENDING);
		final boolean orderAscending = orderAscendingID!=null;
		final String orderID = orderAscending ? orderAscendingID : orderDescendingID;
		final Function orderBy = (orderID==null) ? null : (Function)type.getFeature(orderID);

		final String startString = request.getParameter(START);
		final String countString = request.getParameter(COUNT);
		final int start = (startString==null) ?  0 : Integer.parseInt(startString);
		final int count = (countString==null) ? 10 : Integer.parseInt(countString);

		return new TypeCop(provider, language, type, orderBy, orderAscending, start, count);
	}
}
