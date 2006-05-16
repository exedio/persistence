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

package com.exedio.cope.console;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Attribute;
import com.exedio.cope.Item;
import com.exedio.cope.ItemAttribute;
import com.exedio.cope.ItemFunction;
import com.exedio.cope.Model;
import com.exedio.cope.Type;

final class TypeColumnCop extends AdminCop
{
	final static String TEST = "TEST_TYPE_COLUMNS";

	TypeColumnCop()
	{
		super("type columns");
		addParameter(TAB, TAB_TYPE_COLUMNS);
	}

	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request) throws IOException
	{
		final ArrayList<ItemFunction> attributes = new ArrayList<ItemFunction>(); // TODO SOON rename
		
		for(final Type<Item> t : model.getTypes())
		{
			final ItemFunction<Item> tt = t.getThis();
			if(tt.needsCheckTypeColumn())
				attributes.add(tt);
			
			for(final Attribute a : t.getDeclaredAttributes())
				if(a instanceof ItemAttribute)
				{
					final ItemAttribute ia = (ItemAttribute)a;
					if(ia.needsCheckTypeColumn())
						attributes.add(ia);
				}
		}
		
		if(request.getParameter(TEST)!=null)
		{
			try
			{
				model.startTransaction();
				Admin_Jspm.write(out, this, attributes, true);
				model.commit();
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
		}
		else
			Admin_Jspm.write(out, this, attributes, false);
	}
	
}
