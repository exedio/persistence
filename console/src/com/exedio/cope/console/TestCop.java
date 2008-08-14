/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.console;

import java.io.PrintStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Model;

abstract class TestCop<I> extends ConsoleCop
{
	final static String TEST = "TEST";

	TestCop(final String tab, final String name)
	{
		super(tab, name);
	}

	@Override
	final void writeBody(
			final PrintStream out,
			final Model model,
			final HttpServletRequest request,
			final History history,
			final boolean historyModelShown)
	{
		final List<I> items = getItems(model);
		
		if(request.getParameter(TEST)!=null)
		{
			try
			{
				model.startTransaction();
				Test_Jspm.writeBody(this, out, getHeadings(), items, true);
				model.commit();
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
		}
		else
			Test_Jspm.writeBody(this, out, getHeadings(), items, false);
	}
	
	abstract List<I> getItems(Model model);
	abstract String[] getHeadings();
	abstract String[] getValues(final I item);
	abstract int test(final I item);
}
