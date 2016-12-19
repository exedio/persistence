/*
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

package com.exedio.cope.tojunit;

import static com.exedio.cope.util.Sources.checkKey;

import com.exedio.cope.util.Properties.Source;
import java.util.Collection;

// TODO move to com.exedio.cope.util.Sources
public final class TestSources
{
	public static Source describe(final String description, final Source source)
	{
		return new Source(){
			@Override
			public String get(final String key)
			{
				checkKey(key);
				return source.get(key);
			}

			@Override
			public Collection<String> keySet()
			{
				return source.keySet();
			}

			@Override
			public String getDescription()
			{
				return description;
			}

			@Override
			public String toString()
			{
				return description;
			}
		};
	}


	private TestSources()
	{
		// prevent instantiation
	}
}
