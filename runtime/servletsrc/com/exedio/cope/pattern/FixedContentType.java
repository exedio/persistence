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

import static com.exedio.cope.util.Check.requireNonEmpty;

import com.exedio.cope.Condition;
import com.exedio.cope.DateField;
import com.exedio.cope.Item;
import java.util.List;

final class FixedContentType extends ContentType<Void>
{
	private final String contentType;

	FixedContentType(final String contentType)
	{
		this.contentType = requireNonEmpty(contentType, "contentType");
	}

	@Override
	FixedContentType copy()
	{
		return this; // class does not refer to a field, that needs to be copied
	}

	@Override
	FixedContentType toFinal()
	{
		return this; // class does not refer to a field, that needs to be copied
	}

	@Override
	FixedContentType optional()
	{
		return this; // class does not refer to a field, that needs to be copied
	}

	@Override
	boolean check(final String contentType)
	{
		return this.contentType.equals(contentType);
	}

	@Override
	int getMaximumLength()
	{
		return contentType.length();
	}

	@Override
	String describe()
	{
		return contentType;
	}

	@Override
	List<String> getAllowed()
	{
		return List.of(contentType);
	}

	@Override
	String get(final Item item, final DateField nullSensor)
	{
		return (nullSensor.get(item)!=null) ? contentType : null;
	}

	@Override
	Void set(final String contentType)
	{
		throw new RuntimeException();
	}

	@Override
	Condition in(final String[] contentTypes, final DateField nullSensor)
	{
		boolean foundNull = false;
		boolean foundMatch = false;
		for(final String contentType : contentTypes)
		{
			if(contentType==null)
				foundNull = true;
			else if(this.contentType.equals(contentType))
				foundMatch = true;

			// TODO loop could be terminated is both foundNull and foundMatch are true
		}

		return foundMatch
				? foundNull ? Condition.ofTrue()  : nullSensor.isNotNull()
				: foundNull ? nullSensor.isNull() : Condition.ofFalse();
	}
}
