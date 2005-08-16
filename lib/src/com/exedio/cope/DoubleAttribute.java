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

package com.exedio.cope;

import com.exedio.cope.search.EqualCondition;
import com.exedio.cope.search.GreaterCondition;
import com.exedio.cope.search.GreaterEqualCondition;
import com.exedio.cope.search.LessCondition;
import com.exedio.cope.search.LessEqualCondition;
import com.exedio.cope.search.NotEqualCondition;

public final class DoubleAttribute extends ObjectAttribute
{
	/**
	 * @see Item#doubleAttribute(Option)
	 */
	DoubleAttribute(final Option option)
	{
		super(option, Double.class, "double");
	}

	public ObjectAttribute copyAsTemplate()
	{
		return new DoubleAttribute(getTemplateOption());
	}
	
	protected Column createColumn(final Table table, final String name, final boolean notNull)
	{
		return new DoubleColumn(table, name, notNull, 30);
	}

	Object cacheToSurface(final Object cache)
	{
		return (Double)cache;
	}
	
	Object surfaceToCache(final Object surface)
	{
		return (Double)surface;
	}
	
	public final EqualCondition equal(final Double value)
	{
		return new EqualCondition(null, this, value);
	}
	
	public final EqualCondition equal(final double value)
	{
		return new EqualCondition(null, this, new Double(value));
	}
	
	public final NotEqualCondition notEqual(final Double value)
	{
		return new NotEqualCondition(this, value);
	}
	
	public final NotEqualCondition notEqual(final double value)
	{
		return new NotEqualCondition(this, new Double(value));
	}
	
	public final LessCondition less(final double value)
	{
		return new LessCondition(this, new Double(value));
	}
	
	public final LessEqualCondition lessOrEqual(final double value)
	{
		return new LessEqualCondition(this, new Double(value));
	}
	
	public final GreaterCondition greater(final double value)
	{
		return new GreaterCondition(this, new Double(value));
	}
	
	public final GreaterEqualCondition greaterOrEqual(final double value)
	{
		return new GreaterEqualCondition(this, new Double(value));
	}
	
}
