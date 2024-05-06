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
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.util.CharSet;
import java.util.List;

final class DefaultContentType extends ContentType<String>
{
	private final StringField field;

	DefaultContentType(
			final int maxLength)
	{
		this(makeField(maxLength, new CharSet('+', '+', '-', '9', 'a', 'z')));
	}

	private DefaultContentType(
			final StringField field)
	{
		super(field, "contentType");
		this.field = field;
	}

	@Override
	DefaultContentType copy()
	{
		return new DefaultContentType(field.copy());
	}

	@Override
	DefaultContentType toFinal()
	{
		return new DefaultContentType(field.toFinal());
	}

	@Override
	DefaultContentType optional()
	{
		return new DefaultContentType(field.optional());
	}

	@Override
	DefaultContentType lengthMax(final int maximumLength)
	{
		return new DefaultContentType(field.lengthMax(maximumLength));
	}

	@Override
	boolean check(final String contentType)
	{
		return contentType.indexOf('/')>=0;
	}

	@Override
	int getMaximumLength()
	{
		return field.getMaximumLength();
	}

	@Override
	String describe()
	{
		return "*/*";
	}

	@Override
	List<String> getAllowed()
	{
		return null;
	}

	@Override
	String get(final Item item, final DateField nullSensor)
	{
		return field.get(item);
	}

	@Override
	String set(final String contentType)
	{
		return contentType;
	}

	@Override
	Condition in(final String[] contentTypes, final DateField nullSensor)
	{
		return field.in(contentTypes);
	}
}
