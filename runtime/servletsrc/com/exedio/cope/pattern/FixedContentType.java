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
import java.util.Collections;
import java.util.List;

final class FixedContentType extends ContentType<Void>
{
	private final String full;

	FixedContentType(final String full)
	{
		this.full = full;
	}

	@Override
	FixedContentType copy()
	{
		return new FixedContentType(full);
	}

	@Override
	FixedContentType toFinal()
	{
		return copy();
	}

	@Override
	FixedContentType optional()
	{
		return copy();
	}

	@Override
	boolean check(final String contentType)
	{
		return full.equals(contentType);
	}

	@Override
	int getMaximumLength()
	{
		return full.length();
	}

	@Override
	String describe()
	{
		return full;
	}

	@Override
	List<String> getAllowed()
	{
		return Collections.singletonList(full);
	}

	@Override
	String get(final Item item, final DateField nullSensor)
	{
		return (nullSensor.get(item)!=null) ? full : null;
	}

	@Override
	Void set(final String contentType)
	{
		throw new RuntimeException();
	}

	@Override
	Condition equal(final String contentType)
	{
		return
			full.equals(contentType)
			? Condition.TRUE
			: Condition.FALSE;
	}
}
