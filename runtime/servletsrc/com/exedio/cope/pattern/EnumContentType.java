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

import static com.exedio.cope.pattern.Media.applyConstraints;
import static com.exedio.cope.util.Check.requireNonEmptyAndCopy;

import com.exedio.cope.Condition;
import com.exedio.cope.DataField;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

final class EnumContentType extends ContentType<Integer>
{
	private final IntegerField field;
	private final String[] types;
	private final HashMap<String, Integer> typeSet;

	EnumContentType(
			final String[] types,
			final DataField constraintSource)
	{
		this(
				applyConstraints(new IntegerField().range(0, types.length-1), constraintSource),
				requireNonEmptyAndCopy(types, "types"));
	}

	private EnumContentType(
			final IntegerField field,
			final String[] types)
	{
		super(field, "contentType");
		this.field = field;
		this.types = types;
		final HashMap<String, Integer> typeSet = new HashMap<>();
		for(int i = 0; i<types.length; i++)
		{
			final String type = types[i];
			if(typeSet.putIfAbsent(type, i)!=null)
				throw new IllegalArgumentException("duplicates are not allowed for content type enumeration: " + type);
		}
		this.typeSet = typeSet;
	}

	@Override
	EnumContentType copy()
	{
		return new EnumContentType(field.copy(), types);
	}

	@Override
	EnumContentType toFinal()
	{
		return new EnumContentType(field.toFinal(), types);
	}

	@Override
	EnumContentType optional()
	{
		return new EnumContentType(field.optional(), types);
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
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(final String t : types)
		{
			if(first)
				first = false;
			else
				sb.append(',');

			sb.append(t);
		}
		return sb.toString();
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
	Condition in(final String[] contentTypes, final DateField nullSensor)
	{
		final ArrayList<Integer> values = new ArrayList<>(contentTypes.length);
		for(final String contentType : contentTypes)
		{
			if(contentType==null)
				values.add(null);
			else
			{
				final Integer number = typeSet.get(contentType);
				if(number!=null)
					values.add(number);
			}
		}
		return field.in(values);
	}
}
