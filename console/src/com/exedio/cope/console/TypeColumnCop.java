/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.util.ArrayList;
import java.util.List;

import com.exedio.cope.Field;
import com.exedio.cope.ItemField;
import com.exedio.cope.ItemFunction;
import com.exedio.cope.Model;
import com.exedio.cope.This;
import com.exedio.cope.Type;

final class TypeColumnCop extends TestCop<ItemFunction>
{
	TypeColumnCop(final Args args)
	{
		super(TAB_TYPE_COLUMNS, "type columns", args);
	}

	@Override
	protected TypeColumnCop newArgs(final Args args)
	{
		return new TypeColumnCop(args);
	}

	@Override
	List<ItemFunction> getItems(final Model model)
	{
		final ArrayList<ItemFunction> functions = new ArrayList<ItemFunction>();
		
		for(final Type<?> t : model.getTypes())
		{
			final This<?> tt = t.getThis();
			if(tt.needsCheckTypeColumn())
				functions.add(tt);
			
			for(final Field f : t.getDeclaredFields())
				if(f instanceof ItemField)
				{
					final ItemField itf = (ItemField)f;
					if(itf.needsCheckTypeColumn())
						functions.add(itf);
				}
		}
		
		return functions;
	}
	
	@Override
	String getCaption()
	{
		return "Type Columns";
	}
	
	@Override
	String[] getHeadings()
	{
		return new String[]{"Function", "Value"};
	}
	
	@Override
	void writeValue(final Out out, final ItemFunction function, final int h)
	{
		switch(h)
		{
			case 0: out.write(function.toString()); break;
			case 1: out.write(function.getValueType().getID()); break;
			default:
				throw new RuntimeException(String.valueOf(h));
		};
	}
	
	@Override
	int test(final ItemFunction function)
	{
		return function.checkTypeColumn();
	}
}
