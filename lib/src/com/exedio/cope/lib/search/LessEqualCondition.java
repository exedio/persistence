
package com.exedio.cope.lib.search;

import com.exedio.cope.lib.DoubleAttribute;
import com.exedio.cope.lib.EnumerationAttribute;
import com.exedio.cope.lib.EnumerationValue;
import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.LongAttribute;
import com.exedio.cope.lib.StringAttribute;

public class LessEqualCondition extends LiteralCondition
{
	private static final String OPERATOR = "<=";

	public LessEqualCondition(final StringAttribute attribute, final String value)
	{
		super(OPERATOR, attribute, value);
	}
	
	public LessEqualCondition(final IntegerAttribute attribute, final int value)
	{
		super(OPERATOR, attribute, new Integer(value));
	}

	public LessEqualCondition(final LongAttribute attribute, final long value)
	{
		super(OPERATOR, attribute, new Long(value));
	}

	public LessEqualCondition(final DoubleAttribute attribute, final double value)
	{
		super(OPERATOR, attribute, new Double(value));
	}

	public LessEqualCondition(final EnumerationAttribute attribute, final EnumerationValue value)
	{
		super(OPERATOR, attribute, value);
	}

}
