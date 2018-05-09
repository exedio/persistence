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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class Generics
{
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

	private Generics()
	{
		// prevent instantiation
	}
}
