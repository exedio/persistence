
package com.exedio.cope.lib.search;

import com.exedio.cope.lib.EnumerationAttribute;
import com.exedio.cope.lib.EnumerationValue;
import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.StringAttribute;

public class GreaterCondition extends LiteralCondition
{
	private static final String OPERATOR = ">";

	public GreaterCondition(final StringAttribute attribute, final String value)
	{
		super(OPERATOR, attribute, value);
	}
	
	public GreaterCondition(final IntegerAttribute attribute, final int value)
	{
		super(OPERATOR, attribute, new Integer(value));
	}

	public GreaterCondition(final EnumerationAttribute attribute, final EnumerationValue value)
	{
		super(OPERATOR, attribute, value);
	}

}
