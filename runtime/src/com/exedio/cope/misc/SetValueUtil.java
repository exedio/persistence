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

package com.exedio.cope.misc;

import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;

public final class SetValueUtil
{
	@SuppressFBWarnings("PZLA_PREFER_ZERO_LENGTH_ARRAYS")
	public static SetValue<?>[] toArray(final List<? extends SetValue<?>> list)
	{
		return
			list!=null
			? list.toArray(list.toArray(new SetValue<?>[list.size()]))
			: null;
	}

	public static <E> E getFirst(final List<SetValue<?>> setValues, final Settable<E> settable)
	{
		for(final SetValue<?> setValue : setValues)
		{
			if(settable==setValue.settable)
			{
				@SuppressWarnings("unchecked")
				final E result = (E)setValue.value;
				return result;
			}
		}
		return null;
	}

	public static SetValue<?>[] add(final SetValue<?>[] setValues, final SetValue<?> value)
	{
		final SetValue<?>[] result = new SetValue<?>[setValues.length + 1];
		System.arraycopy(setValues, 0, result, 0, setValues.length);
		result[result.length - 1] = value;
		return result;
	}


	private SetValueUtil()
	{
		// prevent instantiation
	}
}
