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
import com.exedio.cope.Function;
import com.exedio.cope.IntegerFunction;
import com.exedio.cope.Item;
import com.exedio.cope.ItemAttribute;
import com.exedio.cope.LongAttribute;
import com.exedio.cope.ObjectAttribute;
import com.exedio.cope.Query;
import com.exedio.cope.Statement;
import com.exedio.cope.StringFunction;

public final class NotEqualCondition extends Condition
{
	public final Function function;
	public final Object value;

	public NotEqualCondition(final ObjectAttribute function)
	{
		this.function = function;
		this.value = null;
	}
	
	public NotEqualCondition(final StringFunction function, final String value)
	{
		this.function = function;
		this.value = value;
	}
	
	public NotEqualCondition(final IntegerFunction function, final Integer value)
	{
		this.function = function;
		this.value = value;
	}
	
	public NotEqualCondition(final LongAttribute attribute, final Long value)
	{
		this.function = attribute;
		this.value = value;
	}
	
	public NotEqualCondition(final BooleanAttribute attribute, final Boolean value)
	{
		this.function = attribute;
		this.value = value;
	}
	
	public NotEqualCondition(final DoubleAttribute attribute, final Double value)
	{
		this.function = attribute;
		this.value = value;
	}
	
	public NotEqualCondition(final DateAttribute attribute, final Date value)
	{
		this.function = attribute;
		this.value = value;
	}
	
	public NotEqualCondition(final ItemAttribute attribute, final Item value)
	{
		this.function = attribute;
		this.value = value;
	}
	
	public final void appendStatement(final Statement bf)
	{
		if(value!=null)
		{
			// IMPLEMENTATION NOTE
			// the "or is null" is needed since without this oracle
			// does not find results with null.
			bf.append("(").
				append(function).
				append("<>").
				appendValue(function, value).
				append(" or ").
				append(function).
				append(" is null)");
		}
		else
			bf.append(function).
				append(" is not null");
	}

	public final void check(final Query query)
	{
		check(function, query);
	}

	public final String toString()
	{
		return function.getName() + "!='" + value + '\'';
	}
	
}
