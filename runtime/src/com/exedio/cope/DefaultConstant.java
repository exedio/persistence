/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

package com.exedio.cope;

final class DefaultConstant<E> extends DefaultSource<E>
{
	private static final long NO_CREATED_TIME_MILLIS = Long.MIN_VALUE;

	static <E> DefaultConstant<E> wrap(final E value)
	{
		return value!=null ? new DefaultConstant<E>(value, NO_CREATED_TIME_MILLIS) : null;
	}

	static <E> DefaultConstant<E> wrapWithCreatedTime(final E value)
	{
		return value!=null ? new DefaultConstant<E>(value, System.currentTimeMillis()) : null;
	}

	final E value;
	private final long createdTimeMillis;

	private DefaultConstant(final E value, final long createdTimeMillis)
	{
		this.value = value;
		this.createdTimeMillis = createdTimeMillis;

		assert value!=null;
	}

	@Override
	E make(final long now)
	{
		return value;
	}

	long createdTimeMillis()
	{
		assert createdTimeMillis!=NO_CREATED_TIME_MILLIS;
		return createdTimeMillis;
	}
}
