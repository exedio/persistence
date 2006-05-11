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

import com.exedio.cope.Function;
import com.exedio.cope.LiteralCondition;

public class LessEqualCondition<E> extends LiteralCondition<E>
{
	/**
	 * Creates a new LessEqualCondition.
	 * Instead of using this constructor directly,
	 * you may want to use the more type-safe wrapper methods.
	 * @see com.exedio.cope.FunctionAttribute#lessOrEqual(Object)
	 */
	public LessEqualCondition(final Function<E> function, final E value)
	{
		super(Operator.LessEqual, function, value);
	}
	
}
