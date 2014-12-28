/*
 * Copyright (C) 2000  Ralf Wiebicke
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.instrument;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class Generics
{
	static String remove(final String s)
	{
		final int lt = s.indexOf('<');
		//System.out.println("--------evaluate("+s+")"+lt);
		if(lt>=0)
		{
			final int gt = s.indexOf('>', lt);
			if(gt<0)
				throw new RuntimeException(s);

			//System.out.println("--------evaluate("+s+")"+gt);
			if(gt<s.length())
				return s.substring(0, lt) + s.substring(gt+1);
			else
				return s.substring(0, lt);
		}
		else
			return s;
	}

	static List<String> get(final String s)
	{
		final int lt = s.indexOf('<');
		if(lt>=0)
		{
			final ArrayList<String> result = new ArrayList<>();

			final int gt = s.indexOf('>', lt);
			if(gt<0)
				throw new RuntimeException(s);

			int lastcomma = lt;
			for(int comma = s.indexOf(',', lt); comma>=0&&comma<gt; comma = s.indexOf(',', comma+1))
			{
				result.add(s.substring(lastcomma+1, comma).trim());
				lastcomma = comma;
			}
			result.add(s.substring(lastcomma+1, gt).trim());

			return result;
		}
		else
			return Collections.emptyList();
	}

	static Type[] getTypes(final String s)
	{
		final List<String> x = get(s);
		final Type[] result = new Type[x.size()];
		for(int i = 0; i<result.length; i++)
			result[i] = new SourceType(x.get(i));
		return result;
	}

	static class SourceType implements Type
	{
		final String name;

		SourceType(final String name)
		{
			this.name = name;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}

	private Generics()
	{
		// prevent instantiation
	}
}
