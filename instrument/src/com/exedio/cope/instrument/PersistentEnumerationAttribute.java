
package com.exedio.cope.instrument;

import java.util.List;

public final class PersistentEnumerationAttribute extends PersistentAttribute
{
	private final List enumerationValues;

	public PersistentEnumerationAttribute(
			final JavaAttribute javaAttribute,
			final String persistentType,
			final boolean readOnly, final boolean notNull, final boolean mapped,
			final List qualifiers,
			final List enumerationValues)
	{
		super(javaAttribute, persistentType, PersistentAttribute.TYPE_ENUMERATION, readOnly, notNull, mapped, qualifiers);
		this.enumerationValues = enumerationValues;
	}

	public final List getEnumerationValues()
	{
		return enumerationValues;
	}
	
}
