/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
import com.exedio.cope.QueryInfo;
import com.exedio.cope.Transaction;
import com.exedio.cope.Type;
import com.exedio.cops.Pager;

final class TypeCop extends CopernicaCop
{
	private static final String ORDER_ASCENDING = "oa";
	private static final String ORDER_DESCENDING = "od";
	private static final int LIMIT_DEFAULT = 10;

	final Type type;
	final Function orderBy;
	final boolean orderAscending;
	final Pager pager;

	private Query.Result queryResult = null;
	private List<QueryInfo> queryInfos;

	TypeCop(final CopernicaProvider provider, final CopernicaLanguage language, final Type type)
	{
		this(provider, language, type, null, true, new Pager(LIMIT_DEFAULT));
	}
	
	TypeCop(final CopernicaProvider provider, final CopernicaLanguage language, final Type type,
					final Function orderBy, final boolean orderAscending,
					final Pager pager)
	{
		super("type", provider, language);
		
		this.type = type;
		this.orderBy = orderBy;
		this.orderAscending = orderAscending;
		this.pager = pager;

		addParameter(TYPE, type.getID());
		// orderBy must be a feature
		if(orderBy!=null)
			addParameter(orderAscending ? ORDER_ASCENDING : ORDER_DESCENDING, ((Feature)orderBy).getName());
		pager.addParameters(this);
	}
	
	@Override
	final CopernicaCop switchLanguage(final CopernicaLanguage newLanguage)
	{
		return new TypeCop(provider, newLanguage, type, orderBy, orderAscending, pager);
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
		return pager.isFirst() ? null : toPage(pager.previous());
	}

	@Override
	final CopernicaCop toNext()
	{
		return pager.isLast() ? null : toPage(pager.next());
	}
	
	final CopernicaCop toPage(final Pager pager)
	{
		return pager!=null ? new TypeCop(provider, language, type, orderBy, orderAscending, pager) : null;
	}
	
	final TypeCop orderBy(final Function newOrderBy, final boolean ascending)
	{
		return new TypeCop(provider, language, type, newOrderBy, ascending, pager);
	}

	final List getItems()
	{
		return queryResult.getData();
	}

	final List<QueryInfo> getQueryInfos()
	{
		return queryInfos;
	}

	@Override
	void init(final HttpServletRequest request)
	{
		super.init(request);
		assert queryResult==null;
		
		final Query query = type.newQuery(null);
		if(orderBy!=null)
			query.setOrderByAndThis(orderBy, orderAscending);
		else
			query.setOrderByThis(true);
		query.setLimit(pager.getOffset(), pager.getLimit());
		
		final Transaction transaction = type.getModel().getCurrentTransaction();
		transaction.setQueryInfoEnabled(true);

		queryResult = query.searchAndTotal();
		
		queryInfos = transaction.getQueryInfos();
		transaction.setQueryInfoEnabled(false);
		pager.init(queryResult.getData().size(), queryResult.getTotal());
	}
	
	@Override
	void writeBody(final PrintStream out)
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

		return new TypeCop(
				provider, language, type,
				orderBy, orderAscending,
				Pager.newPager(request, LIMIT_DEFAULT));
	}
}
