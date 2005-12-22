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

import com.exedio.cope.DateAttribute;
import com.exedio.cope.DoubleAttribute;
import com.exedio.cope.EnumAttribute;
import com.exedio.cope.EnumValue;
import com.exedio.cope.IntegerAttribute;
import com.exedio.cope.LiteralCondition;
import com.exedio.cope.LongAttribute;
import com.exedio.cope.FunctionAttribute;
import com.exedio.cope.StringAttribute;

public class LessEqualCondition extends LiteralCondition
{
	/**
	 * Creates a new LessEqualCondition.
	 * Instead of using this constructor directly,
	 * you may want to use the more type-safe wrapper methods.
	 * @see StringAttribute#lessOrEqual(String)
	 * @see IntegerAttribute#lessOrEqual(int)
	 * @see LongAttribute#lessOrEqual(long)
	 * @see DoubleAttribute#lessOrEqual(double)
	 * @see DateAttribute#lessOrEqual(Date)
	 * @see EnumAttribute#lessOrEqual(EnumValue)
	 */
	public LessEqualCondition(final FunctionAttribute attribute, final Object value)
	{
		super("<=", attribute, value);
	}
	
}
