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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.Condition;
import com.exedio.cope.DateField;
import com.exedio.cope.Item;
import com.exedio.cope.util.CharSet;
import java.util.ArrayList;
import java.util.List;

final class SubContentType extends ContentType<String>
{
	private final String major;
	private final String prefix;
	private final int prefixLength;
	private final int minorMaxLength;

	static final int DEFAULT_LENGTH = 30;

	SubContentType(
			final String major,
			final boolean isfinal,
			final boolean optional,
			final int minorMaxLength)
	{
		super(makeField(minorMaxLength, new CharSet('+', '+', '-', '.', '0', '9', 'a', 'z')), isfinal, optional, "minor");
		this.major = requireNonNull(major, "fixedMimeMajor");
		this.prefix = major + '/';
		this.prefixLength = this.prefix.length();
		this.minorMaxLength = minorMaxLength;
	}

	@Override
	SubContentType copy()
	{
		return new SubContentType(major, field.isFinal(), !field.isMandatory(), minorMaxLength);
	}

	@Override
	SubContentType toFinal()
	{
		return new SubContentType(major, true, !field.isMandatory(), minorMaxLength);
	}

	@Override
	SubContentType optional()
	{
		return new SubContentType(major, field.isFinal(), true, minorMaxLength);
	}

	@Override
	SubContentType lengthMax(final int maximumLength)
	{
		return new SubContentType(major, field.isFinal(), !field.isMandatory(), maximumLength - prefixLength);
	}

	@Override
	boolean check(final String contentType)
	{
		return contentType.startsWith(prefix);
	}

	@Override
	int getMaximumLength()
	{
		return prefixLength + minorMaxLength;
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
