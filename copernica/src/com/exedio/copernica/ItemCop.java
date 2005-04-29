/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.NestingRuntimeException;
import com.exedio.cope.lib.NoSuchIDException;
import com.exedio.cope.lib.Type;

final class ItemCop extends CopernicaCop
{
	final Item item;
	
	ItemCop(final CopernicaProvider provider, final CopernicaLanguage language, final Item item)
	{
		super(provider, language);
		this.item = item;
		addParameter(ITEM, item.getCopeID());
	}
	
	ItemForm form;

	final void init(final HttpServletRequest request)
	{
		super.init(request);
		this.form = new ItemForm(this, request);
	}
	
	final CopernicaCop switchLanguage(final CopernicaLanguage newLanguage)
	{
		return new ItemCop(provider, newLanguage, item);
	}

	final boolean isType(final Type type)
	{
		return item.getItemType() == type;
	}

	final String getTitle()
	{
		return provider.getDisplayName(this, language, item);
	}

	void writeBody(final PrintStream out)
		throws IOException
	{
		ItemCop_Jspm.writeBody(out, this);
	}

	static final ItemCop getCop(
			final CopernicaProvider provider, final CopernicaLanguage language,
			final String itemID)
	{	
		try
		{
			final Item item = provider.getModel().findByID(itemID);
			return new ItemCop(provider, language, item);
		}
		catch(NoSuchIDException e)
		{
			throw new NestingRuntimeException(e);
		}
	}

}
