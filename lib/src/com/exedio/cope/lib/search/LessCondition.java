
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
