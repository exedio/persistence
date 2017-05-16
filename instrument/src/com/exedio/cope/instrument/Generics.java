/*
 * Copyright (C) 2000  Ralf Wiebicke
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class Generics
{
	@SuppressWarnings("ConstantConditions") // too complex to analyze
	static String remove(final String s)
	{
		boolean inStringLiteral = false;
		boolean inCharLiteral = false;
		boolean backslash = false;
		int posLessThan = -1;
		int posGreatherThan = -1;
		for (int i=0; i<s.length(); i++)
		{
			if (backslash)
			{
				backslash=false;
				continue;
			}
			switch (s.charAt(i))
			{
				case '\\':
					backslash=true;
					break;
				case '\'':
					if (inStringLiteral) break;
					inCharLiteral=!inCharLiteral;
					break;
				case '\"':
					if (inCharLiteral) break;
					inStringLiteral=!inStringLiteral;
					break;
				case '<':
					if (inStringLiteral||inCharLiteral) break;
					if (posLessThan!=-1) throw new RuntimeException("failed to remove generics from "+s);
					posLessThan=i;
					break;
				case '>':
					if (inStringLiteral||inCharLiteral) break;
					if (posLessThan==-1) throw new RuntimeException("failed to remove generics from "+s);
					posGreatherThan=i;
					break;
				default:
					// ignore
					break;
			}
		}
		if (inStringLiteral) throw new RuntimeException("failed to remove generics from "+s);
		if (inCharLiteral) throw new RuntimeException("failed to remove generics from "+s);
		if (posLessThan>=0)
		{
			if (posGreatherThan==-1) throw new RuntimeException("failed to remove generics from "+s);
			return s.substring(0, posLessThan)+s.substring(posGreatherThan+1);
		}
		else
		{
			return s;
		}
	}

	static String strip(final String s)
	{
		if(s==null)
			return null;

		if(!s.endsWith(">"))
			return s;

		return s.substring(0, s.indexOf('<'));
	}

	@SuppressFBWarnings("SF_SWITCH_NO_DEFAULT")
	static List<String> get(final String s)
	{
		final int lt = s.indexOf('<');
		if(lt<0)
			return Collections.emptyList();

		final int gt = s.lastIndexOf('>');
		if(gt<0)
			throw new RuntimeException(s);

		final ArrayList<String> result = new ArrayList<>();
		int beginOfPart = lt + 1;
		int level = 0;
		for(int i = beginOfPart; i<gt; i++)
		{
			switch(s.charAt(i))
			{
				case '<':
					level++;
					break;
				case '>':
					level--;
					break;
				case ',':
					if(level==0)
					{
						result.add(s.substring(beginOfPart, i).trim());
						beginOfPart = i + 1;
					}
					break;
			}
		}
		if(level!=0)
			throw new RuntimeException(s);

		result.add(s.substring(beginOfPart, gt).trim());
		return result;
	}

	@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
	static Type[] getTypes(final String s)
	{
		final List<String> x = get(s);
		final Type[] result = new Type[x.size()];
		//noinspection Java8ArraySetAll OK: performance
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
