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

package com.exedio.cope.lib.search;

import java.util.Date;

import com.exedio.cope.lib.DateAttribute;
import com.exedio.cope.lib.DoubleAttribute;
import com.exedio.cope.lib.EnumAttribute;
import com.exedio.cope.lib.EnumValue;
import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.LongAttribute;
import com.exedio.cope.lib.StringAttribute;

public class LessCondition extends LiteralCondition
{
	private static final String OPERATOR = "<";

	public LessCondition(final StringAttribute attribute, final String value)
	{
		super(OPERATOR, attribute, value);
	}
	
	public LessCondition(final IntegerAttribute attribute, final int value)
	{
		super(OPERATOR, attribute, new Integer(value));
	}

	public LessCondition(final LongAttribute attribute, final long value)
	{
		super(OPERATOR, attribute, new Long(value));
	}

	public LessCondition(final DoubleAttribute attribute, final double value)
	{
		super(OPERATOR, attribute, new Double(value));
	}

	public LessCondition(final DateAttribute attribute, final Date value)
	{
		super(OPERATOR, attribute, value);
	}

	public LessCondition(final EnumAttribute attribute, final EnumValue value)
	{
		super(OPERATOR, attribute, value);
	}

}
