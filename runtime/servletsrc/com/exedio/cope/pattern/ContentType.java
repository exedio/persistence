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
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.util.CharSet;
import java.util.List;

abstract class ContentType<B>
{
	static final int DEFAULT_LENGTH = 61;

	final FunctionField<B> field;
	final String name;

	ContentType()
	{
		this.field = null;
		this.name = null;
	}

	ContentType(
			final FunctionField<B> field,
			final String name)
	{
		this.field = field;
		this.name = name;

		assert field!=null;
		assert name!=null;
	}

	abstract ContentType<B> copy();
	abstract ContentType<B> toFinal();
	abstract ContentType<B> optional();

	/**
	 * @param maximumLength used in subclasses
	 */
	ContentType<?> lengthMax(final int maximumLength)
	{
		throw new IllegalArgumentException("not allowed for " + describe());
	}

	abstract boolean check(String contentType);
	abstract int getMaximumLength();
	abstract String describe();
	abstract List<String> getAllowed();
	abstract String get(Item item, DateField nullSensor);
	abstract B set(String contentType);
	abstract Condition in(String[] contentTypes, DateField nullSensor);

	final SetValue<B> map(final String contentType)
	{
		return SetValue.map(field, set(contentType));
	}

	protected static final StringField makeField(final int maxLength, final CharSet charSet)
	{
		return new StringField().lengthRange(1, maxLength).charSet(charSet);
	}
}
