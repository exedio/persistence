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

package com.exedio.cope.pattern;

import com.exedio.cope.Feature;
import com.exedio.cope.ItemField;
import com.exedio.cope.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

final class PartOfReverse
{
	private static final HashMap<Type<?>, List<PartOf<?>>> cache = new HashMap<>();
	private static final HashMap<Type<?>, List<PartOf<?>>> cacheDeclared = new HashMap<>();

	static List<PartOf<?>> get(final Type<?> type)
	{
		return get(false, cache, type);
	}

	static List<PartOf<?>> getDeclared(final Type<?> type)
	{
		return get(true, cacheDeclared, type);
	}

	private static List<PartOf<?>> get(final boolean declared, final HashMap<Type<?>, List<PartOf<?>>> cache, final Type<?> type)
	{
		//noinspection SynchronizationOnLocalVariableOrMethodParameter OK: parametersare not supplied from outside this class
		synchronized(cache)
		{
			{
				final List<PartOf<?>> cachedResult = cache.get(type);
				if(cachedResult!=null)
					return cachedResult;
			}

			final ArrayList<PartOf<?>> resultModifiable = new ArrayList<>();

			for(final ItemField<?> field : declared ? type.getDeclaredReferences() : type.getReferences())
			{
				for(final Feature feature : field.getType().getFeatures())
				{
					if(feature instanceof final PartOf<?> partOf)
					{
						if(partOf.getContainer()==field)
							resultModifiable.add(partOf);
					}
				}
			}

			final List<PartOf<?>> result = List.copyOf(resultModifiable);
			cache.put(type, result);
			return result;
		}
	}

	private PartOfReverse()
	{
		// prevent instantiation
	}
}
