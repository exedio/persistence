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

import com.exedio.cope.Model;
import com.exedio.cope.Type;

final class ModificationCounterCop extends TestCop<Type>
{
	ModificationCounterCop(final Args args)
	{
		super(TAB_MODIFICATION_COUNTERS, "modification counters", args);
	}

	@Override
	protected ModificationCounterCop newArgs(final Args args)
	{
		return new ModificationCounterCop(args);
	}

	@Override
	List<Type> getItems(final Model model)
	{
		final ArrayList<Type> result = new ArrayList<Type>();
		
		for(final Type t : model.getTypes())
			if(t.needsCheckModificationCounter())
				result.add(t);
		
		return result;
	}
	
	@Override
	String getCaption()
	{
		return "Modification Counters";
	}
	
	@Override
	String[] getHeadings()
	{
		return new String[]{"Supertype", "Type"};
	}
	
	@Override
	void writeValue(final Out out, final Type type, final int h)
	{
		switch(h)
		{
			case 1: out.write(type.getSupertype().getID()); break;
			case 0: out.write(type.getID()); break;
			default:
				throw new RuntimeException(String.valueOf(h));
		};
	}
	
	@Override
	int check(final Type type)
	{
		return type.checkModificationCounter();
	}
}
