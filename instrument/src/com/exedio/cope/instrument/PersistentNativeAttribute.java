
package com.exedio.cope.instrument;

import java.util.List;

import com.exedio.cope.lib.BooleanAttribute;
import com.exedio.cope.lib.DoubleAttribute;
import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.StringAttribute;

public class PersistentNativeAttribute extends PersistentAttribute
{
	public PersistentNativeAttribute(
			final JavaAttribute javaAttribute,
			final Class typeClass,
			final List initializerArguments, final boolean mapped,
			final List qualifiers)
	{
		super(javaAttribute, getPersistentType(typeClass), initializerArguments, mapped, qualifiers);
	}

	private static final String getPersistentType(final Class typeClass)
	{
		if(IntegerAttribute.class.equals(typeClass))
			return "Integer";
		else if(DoubleAttribute.class.equals(typeClass))
			return "Double";
		else if(BooleanAttribute.class.equals(typeClass))
			return "Boolean";
		else if(StringAttribute.class.equals(typeClass))
			return "String";
		else
			throw new RuntimeException(typeClass.toString());
	}

}
