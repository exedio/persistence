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
import java.util.Comparator;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Model;
import com.exedio.cope.Type;

final class PrimaryKeysCop extends ConsoleCop
{

	PrimaryKeysCop()
	{
		super("pk");
		addParameter(TAB, TAB_PRIMARY_KEY);
	}
	
	private static final Comparator<Type> TYPE_COMPARATOR = new Comparator<Type>()
	{
		public int compare(final Type t1, final Type t2)
		{
			return t1.getID().compareTo(t2.getID());
		}
	};

	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request) throws IOException
	{
		final TreeMap<Type, int[]> primaryKeys = new TreeMap<Type, int[]>(TYPE_COMPARATOR);
		for(final Type t : model.getTypes())
		{
			if(t.getSupertype()==null)
				primaryKeys.put(t, t.getPrimaryKeyInfo());
		}
		Console_Jspm.writeBody(this, out, primaryKeys);
	}
	
}
