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
import com.exedio.cope.ItemField;
import com.exedio.cope.ItemFunction;
import com.exedio.cope.Model;
import com.exedio.cope.Type;

final class TypeColumnCop extends ConsoleCop
{
	final static String TEST = "TEST_TYPE_COLUMNS";

	TypeColumnCop()
	{
		super("type columns");
		addParameter(TAB, TAB_TYPE_COLUMNS);
	}

	@Override
	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request) throws IOException
	{
		final ArrayList<ItemFunction> functions = new ArrayList<ItemFunction>();
		
		for(final Type<?> t : model.getTypes())
		{
			final ItemFunction<?> tt = t.getThis();
			if(tt.needsCheckTypeColumn())
				functions.add(tt);
			
			for(final Attribute a : t.getDeclaredAttributes())
				if(a instanceof ItemField)
				{
					final ItemField ia = (ItemField)a;
					if(ia.needsCheckTypeColumn())
						functions.add(ia);
				}
		}
		
		if(request.getParameter(TEST)!=null)
		{
			try
			{
				model.startTransaction();
				Console_Jspm.writeBody(this, out, functions, true);
				model.commit();
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
		}
		else
			Console_Jspm.writeBody(this, out, functions, false);
	}
	
}
