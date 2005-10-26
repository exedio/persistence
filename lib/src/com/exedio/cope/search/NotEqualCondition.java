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

public final class NotEqualCondition extends Condition
{
	public final Function function;
	public final Object value;

	/**
	 * Creates a new NotEqualCondition.
	 * Instead of using this constructor directly,
	 * you may want to use the more type-safe wrapper methods.
	 * @see ObjectAttribute#isNotNull()
	 * @see StringFunction#notEqual(String)
	 * @see IntegerFunction#notEqual(int)
	 * @see IntegerFunction#notEqual(Integer)
	 * @see BooleanAttribute#notEqual(boolean)
	 * @see BooleanAttribute#notEqual(Boolean)
	 * @see LongAttribute#notEqual(long)
	 * @see LongAttribute#notEqual(Long)
	 * @see DoubleAttribute#notEqual(double)
	 * @see DoubleAttribute#notEqual(Double)
	 * @see DateAttribute#notEqual(Date)
	 * @see EnumAttribute#notEqual(EnumValue)
	 * @see ItemAttribute#notEqual(Item)
	 */
	public NotEqualCondition(final Function function, final Object value)
	{
		if(function==null)
			throw new NullPointerException("function must ot be null");

		this.function = function;
		this.value = value;
	}
	
	public final void appendStatement(final Statement bf, final boolean qualifyTable)
	{
		if(value!=null)
		{
			// IMPLEMENTATION NOTE
			// the "or is null" is needed since without this oracle
			// does not find results with null.
			bf.append("(").
				append(function, (Join)null, qualifyTable).
				append("<>").
				appendValue(function, value).
				append(" or ").
				append(function, (Join)null, qualifyTable).
				append(" is null)");
		}
		else
			bf.append(function, (Join)null, qualifyTable).
				append(" is not null");
	}

	public final void check(final Query query)
	{
		check(function, query);
	}

	public boolean equals(final Object other)
	{
		if(!(other instanceof NotEqualCondition))
			return false;
		
		final NotEqualCondition o = (NotEqualCondition)other;
		
		return function.equals(o.function) && equals(value, o.value);
	}
	
	public int hashCode()
	{
		return function.hashCode() ^ hashCode(value) ^ 1276534;
	}

	public final String toString()
	{
		return function.getName() + "!='" + value + '\'';
	}
	
}
