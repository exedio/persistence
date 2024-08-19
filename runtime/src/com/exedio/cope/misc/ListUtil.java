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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ListUtil
{
	/**
	 * @deprecated
	 * Use {@link List#copyOf(Collection)} instead
	 * BUT ONLY if {@code list} does not contain null elements
	 */
	@Deprecated
	public static <F> List<F> trimUnmodifiable(final ArrayList<F> list)
	{
		final int size = list.size();
		switch(size)
		{
		case 0:
			return List.of();
		case 1:
			return Collections.singletonList(list.get(0));
		default:
			final List<Object> result = Arrays.asList(list.toArray(new Object[size]));
			@SuppressWarnings("unchecked")
			final List<F> resultChecked = (List<F>)result;
			return Collections.unmodifiableList(resultChecked);
		}
	}

	private ListUtil()
	{
		// prevent instantiation
	}
}
