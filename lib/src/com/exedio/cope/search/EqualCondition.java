/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import java.util.Date;

import com.exedio.cope.BooleanAttribute;
import com.exedio.cope.DateAttribute;
import com.exedio.cope.DoubleAttribute;
import com.exedio.cope.EnumAttribute;
import com.exedio.cope.EnumValue;
import com.exedio.cope.Function;
import com.exedio.cope.IntegerFunction;
import com.exedio.cope.Item;
import com.exedio.cope.ItemAttribute;
import com.exedio.cope.Join;
import com.exedio.cope.LongAttribute;
import com.exedio.cope.ObjectAttribute;
import com.exedio.cope.Query;
import com.exedio.cope.Statement;
import com.exedio.cope.StringFunction;

public final class EqualCondition extends Condition
{
	public final Join join;
	public final Function function;
	public final Object value;

	/**
	 * Creates a new EqualCondition.
	 * Instead of using this constructor directly,
	 * you may want to use the more type-safe wrapper methods.
	 * @see ObjectAttribute#isNull()
	 * @see StringFunction#equal(String)
	 * @see IntegerFunction#equal(int)
	 * @see IntegerFunction#equal(Integer)
	 * @see BooleanAttribute#equal(boolean)
	 * @see BooleanAttribute#equal(Boolean)
	 * @see LongAttribute#equal(long)
	 * @see LongAttribute#equal(Long)
	 * @see DoubleAttribute#equal(double)
	 * @see DoubleAttribute#equal(Double)
	 * @see DateAttribute#equal(Date)
	 * @see EnumAttribute#equal(EnumValue)
	 * @see ItemAttribute#equal(Item)
	 */
	public EqualCondition(final Join join, final Function function, final Object value)
	{
		this.join = join;
		this.function = function;
		this.value = value;
	}
	
	public final void appendStatement(final Statement bf)
	{
		bf.append(function, join);
		if(value!=null)
			bf.append('=').
				appendValue(function, value);
		else
			bf.append(" is null");
	}

	public final void check(final Query query)
	{
		check(function, query);
	}

	public final String toString()
	{
		return function.getName() + "='" + value + '\'';
	}
	
}
