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

import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.Function;
import java.util.ArrayList;
import java.util.List;

public final class Conditions
{
	public static Condition equal(
			final Condition left,
			final Condition right)
	{
		return Cope.or(
			left      .and(right      ),
			left.not().and(right.not())
		);
	}

	public static Condition implies(
			final Condition premises,
			final Condition conclusion)
	{
		return premises.not().or(conclusion);
	}

	public static Condition unisonNull(final List<? extends Function<?>> functions)
	{
		if(functions.size()<=1)
			return Condition.ofTrue();

		final int size = functions.size();
		final ArrayList<Condition> isNull    = new ArrayList<>(size);
		final ArrayList<Condition> isNotNull = new ArrayList<>(size);
		for(final Function<?> function : functions)
		{
			isNull   .add(function.isNull   ());
			isNotNull.add(function.isNotNull());
		}
		return Cope.and(isNull).or(Cope.and(isNotNull));
	}


	private Conditions()
	{
		// prevent instantiation
	}
}
