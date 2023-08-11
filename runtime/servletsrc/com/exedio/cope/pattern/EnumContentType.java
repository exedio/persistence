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

import com.exedio.cope.Condition;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import java.util.HashMap;
import java.util.List;

final class EnumContentType extends ContentType<Integer>
{
	private final String[] types;
	private final HashMap<String, Integer> typeSet;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	EnumContentType(
			final String[] types,
			final boolean isfinal,
			final boolean optional)
	{
		super(new IntegerField().range(0, types.length-1), isfinal, optional, "contentType");
		this.types = types;
		final HashMap<String, Integer> typeSet = new HashMap<>();
		for(int i = 0; i<types.length; i++)
		{
			final String type = types[i];
			if(type==null)
				throw new IllegalArgumentException("null is not allowed in content type enumeration position " + i);
			if(typeSet.putIfAbsent(type, i)!=null)
				throw new IllegalArgumentException("duplicates are not allowed for content type enumeration: " + type);
		}
		this.typeSet = typeSet;
	}

	@Override
	EnumContentType copy()
	{
		return new EnumContentType(types, field.isFinal(), !field.isMandatory());
	}

	@Override
	EnumContentType toFinal()
	{
		return new EnumContentType(types, true, !field.isMandatory());
	}

	@Override
	EnumContentType optional()
	{
		return new EnumContentType(types, field.isFinal(), true);
	}

	@Override
	boolean check(final String contentType)
	{
		return typeSet.containsKey(contentType);
	}

	@Override
	int getMaximumLength()
	{
		int result = 0;
		for(final String type : types)
		{
			final int length = type.length();
			if(result<length)
				result = length;
		}
		return result;
	}

	@Override
	String describe()
	{
		final StringBuilder bf = new StringBuilder();
		boolean first = true;
		for(final String t : types)
		{
			if(first)
				first = false;
			else
				bf.append(',');

			bf.append(t);
		}
		return bf.toString();
	}

	@Override
	List<String> getAllowed()
	{
		return List.of(types);
	}

	@Override
	String get(final Item item, final DateField nullSensor)
	{
		final Integer number = field.get(item);
		return (number!=null) ? types[number] : null;
	}

	@Override
	Integer set(final String contentType)
	{
		final Integer result = typeSet.get(contentType);
		assert result!=null;
		return result;
	}

	@Override
	Condition equal(final String contentType)
	{
		final Integer number = typeSet.get(contentType);
		return
			number!=null
			? field.equal(number)
			: Condition.ofFalse();
	}
}
