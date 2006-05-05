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

package com.exedio.cope;

import com.exedio.cope.function.PlusView;
import com.exedio.cope.search.AndCondition;
import com.exedio.cope.search.OrCondition;

/**
 * Utility class for creating conditions.
 * May be subclassed to access methods without class qualifier.
 * 
 * @author Ralf Wiebicke
 */
public abstract class Cope
{
	Cope()
	{/* do not allow class to be subclasses by public */}

	public static final AndCondition and(final Condition condition1, final Condition condition2)
	{
		return new AndCondition(new Condition[]{condition1, condition2});
	}
	
	public static final AndCondition and(final Condition condition1, final Condition condition2, final Condition condition3)
	{
		return new AndCondition(new Condition[]{condition1, condition2, condition3});
	}
	
	public static final OrCondition or(final Condition condition1, final Condition condition2)
	{
		return new OrCondition(new Condition[]{condition1, condition2});
	}
	
	public static final OrCondition or(final Condition condition1, final Condition condition2, final Condition condition3)
	{
		return new OrCondition(new Condition[]{condition1, condition2, condition3});
	}
	
	public static final PlusView plus(final IntegerFunction addend1, final IntegerFunction addend2)
	{
		return new PlusView(new IntegerFunction[]{addend1, addend2});
	}

	public static final PlusView plus(final IntegerFunction addend1, final IntegerFunction addend2, final IntegerFunction addend3)
	{
		return new PlusView(new IntegerFunction[]{addend1, addend2, addend3});
	}

	/**
	 * @deprecated renamed to {@link #plus(IntegerFunction, IntegerFunction)}.
	 */
	@Deprecated
	public static final PlusView sum(final IntegerFunction addend1, final IntegerFunction addend2)
	{
		return plus(addend1, addend2);
	}

	/**
	 * @deprecated renamed to {@link #plus(IntegerFunction, IntegerFunction, IntegerFunction)}.
	 */
	@Deprecated
	public static final PlusView sum(final IntegerFunction addend1, final IntegerFunction addend2, final IntegerFunction addend3)
	{
		return plus(addend1, addend2, addend3);
	}
	
}
