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

package com.exedio.cope.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.exedio.cope.Model;

/**
 * A filter for starting/closing cope transactions.
 *
 * In order to use it, you have to deploy the filter in your <tt>web.xml</tt>,
 * providing the name of the cope model via an init-parameter.
 * Typically, your <tt>web.xml</tt> would contain a snippet like this:
 * <pre>
 * &lt;filter&gt;
 *    &lt;filter-name&gt;TransactionFilter&lt;/filter-name&gt;
 *    &lt;filter-class&gt;com.exedio.cope.util.TransactionFilter&lt;/filter-class&gt;
 *    &lt;init-param&gt;
 *       &lt;param-name&gt;model&lt;/param-name&gt;
 *       &lt;param-value&gt;{@link com.exedio.cope.Model com.bigbusiness.shop.Main#model}&lt;/param-value&gt;
 *    &lt;/init-param&gt;
 * &lt;/filter&gt;
 * &lt;filter-mapping&gt;
 *    &lt;filter-name&gt;TransactionFilter&lt;/filter-name&gt;
 *    &lt;url-pattern&gt;*.do&lt;/url-pattern&gt;
 *    &lt;dispatcher&gt;REQUEST&lt;/dispatcher&gt;
 * &lt;/filter-mapping&gt;
 * </pre>
 *
 * @author Stephan Frisch, exedio GmbH
 */
public final class TransactionFilter implements Filter
{
	private Model model;
	private String transactionName = null;
	
	public void init(FilterConfig config) throws ServletException
	{
		model = ServletUtil.getModel(config);
		final String transactionNameParameter = config.getInitParameter("transactionName");
		transactionName = (transactionNameParameter!=null) ? transactionNameParameter : getClass().getName();
	}

	public void doFilter(
			final ServletRequest request,
			final ServletResponse response,
			final FilterChain chain) throws IOException, ServletException
	{
		try
		{
			model.startTransaction(transactionName);
			chain.doFilter(request, response);
			model.commit();
		}		
		finally
		{
			model.rollbackIfNotCommitted();
		}
	}
	
	public void destroy()
	{
		// empty implementation
	}
	
}
