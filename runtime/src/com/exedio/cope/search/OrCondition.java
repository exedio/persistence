/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.search;

import java.util.Collection;

import com.exedio.cope.CompositeCondition;
import com.exedio.cope.Condition;
import com.exedio.cope.EqualCondition;
import com.exedio.cope.Function;

public final class OrCondition extends CompositeCondition
{
	/**
	 * @throws NullPointerException if <tt>conditions==null</tt>
	 * @throws RuntimeException if <tt>conditions.length==0</tt>
	 */
	public OrCondition(final Condition[] conditions)
	{
		super(" or ", conditions);
	}
	
	public static final <E> OrCondition in(final Function<E> function, final Collection<E> values)
	{
		final EqualCondition[] result = new EqualCondition[values.size()];

		int i = 0;
		for(E value : values)
			result[i++] = function.equal(value);
		
		return new OrCondition(result);
	}
}
