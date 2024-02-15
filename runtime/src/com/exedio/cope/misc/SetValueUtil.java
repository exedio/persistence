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

package com.exedio.cope.misc;

import static java.util.Objects.requireNonNull;

import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import java.util.List;

public final class SetValueUtil
{
	public static SetValue<?>[] toArray(final List<SetValue<?>> list)
	{
		return
			list!=null
			? list.toArray(list.toArray(SetValue.EMPTY_ARRAY))
			: null;
	}

	public static <E> E getFirst(final SetValue<?>[] setValues, final Settable<E> settable)
	{
		final SetValue<E> sv = getFirstMapping(setValues, settable);
		return sv!=null ? sv.value : null;
	}

	public static <E> SetValue<E> getFirstMapping(final SetValue<?>[] setValues, final Settable<E> settable)
	{
		for(final SetValue<?> setValue : setValues)
		{
			if(settable==setValue.settable)
			{
				@SuppressWarnings("unchecked")
				final SetValue<E> result = (SetValue<E>)setValue;
				return result;
			}
		}
		return null;
	}

	public static <E> E getFirst(final List<SetValue<?>> setValues, final Settable<E> settable)
	{
		final SetValue<E> sv = getFirstMapping(setValues, settable);
		return sv!=null ? sv.value : null;
	}

	public static <E> SetValue<E> getFirstMapping(final List<SetValue<?>> setValues, final Settable<E> settable)
	{
		for(final SetValue<?> setValue : setValues)
		{
			if(settable==setValue.settable)
			{
				@SuppressWarnings("unchecked")
				final SetValue<E> result = (SetValue<E>)setValue;
				return result;
			}
		}
		return null;
	}

	public static SetValue<?>[] add(final SetValue<?>[] setValues, final SetValue<?> value)
	{
		requireNonNull(value, "value");

		return Arrays.append(setValues, value);
	}


	private SetValueUtil()
	{
		// prevent instantiation
	}
}
