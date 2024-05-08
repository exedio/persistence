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
import static com.exedio.cope.util.Check.requireNonEmpty;

import com.exedio.cope.Condition;
import com.exedio.cope.DataField;
import com.exedio.cope.DateField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.util.CharSet;
import java.util.ArrayList;
import java.util.List;

final class SubContentType extends ContentType<String>
{
	private final StringField field;
	private final String prefix;
	private final int prefixLength;

	SubContentType(
			final String major,
			final DataField constraintSource)
	{
		this(
				applyConstraints(makeField(DEFAULT_LENGTH-requireNonEmpty(major, "majorContentType").length()-1, new CharSet('+', '+', '-', '.', '0', '9', 'a', 'z')), constraintSource),
				major + '/');
	}

	private SubContentType(
			final StringField field,
			final String prefix)
	{
		super(field, "minor");
		this.field = field;
		this.prefix = prefix;
		this.prefixLength = prefix.length();
	}

	@Override
	SubContentType copy()
	{
		return new SubContentType(field.copy(), prefix);
	}

	@Override
	SubContentType toFinal()
	{
		return new SubContentType(field.toFinal(), prefix);
	}

	@Override
	SubContentType optional()
	{
		return new SubContentType(field.optional(), prefix);
	}

	@Override
	SubContentType lengthMax(final int maximumLength)
	{
		return new SubContentType(field.lengthMax(maximumLength - prefixLength), prefix);
	}

	@Override
	boolean check(final String contentType)
	{
		return contentType.startsWith(prefix);
	}

	@Override
	int getMaximumLength()
	{
		return prefixLength + field.getMaximumLength();
	}

	@Override
	String describe()
	{
		return prefix + '*';
	}

	@Override
	List<String> getAllowed()
	{
		return null;
	}

	@Override
	String get(final Item item, final DateField nullSensor)
	{
		final String minor = field.get(item);
		return (minor!=null) ? (prefix + minor) : null;
	}

	@Override
	String set(final String contentType)
	{
		assert check(contentType);
		return contentType.substring(prefixLength);
	}

	@Override
	Condition in(final String[] contentTypes, final DateField nullSensor)
	{
		final ArrayList<String> values = new ArrayList<>(contentTypes.length);
		for(final String contentType : contentTypes)
		{
			if(contentType==null)
				values.add(null);
			else if(contentType.startsWith(prefix))
				values.add(contentType.substring(prefixLength));
		}
		return field.in(values);
	}
}
