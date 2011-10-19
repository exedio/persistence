/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.misc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import com.exedio.cope.Model;
import com.exedio.cope.Type;

public final class SerializationCheck
{
	public static List<Field> check(final Model model)
	{
		final LinkedHashSet<Class> classes = new LinkedHashSet<Class>();
		for(final Type type : model.getTypesSortedByHierarchy())
			classes.add(type.getJavaClass());

		ArrayList<Field> result = null;
		for(final Class clazz : classes)
		{
			for(final Field field : clazz.getDeclaredFields())
			{
				final int modifiers = field.getModifiers();
				if(!Modifier.isStatic(modifiers) &&
					!Modifier.isTransient(modifiers))
				{
					if(result==null)
						result = new ArrayList<Field>();
					result.add(field);
				}
			}
		}

		return
			result!=null
			? Collections.unmodifiableList(result)
			: Collections.<Field>emptyList();
	}

	private SerializationCheck()
	{
		// prevent instantiation
	}
}
