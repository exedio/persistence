
package com.exedio.cope.instrument;

import java.util.Collections;
import java.util.List;

public final class PersistentEnumerationAttribute extends PersistentAttribute
{
	public final List enumerationValues;

	public PersistentEnumerationAttribute(
			final JavaAttribute javaAttribute,
			final String persistentType,
			final boolean readOnly, final boolean notNull, final boolean mapped,
			final List qualifiers,
			final List enumerationValues)
	{
		super(javaAttribute, persistentType, PersistentAttribute.TYPE_ENUMERATION, readOnly, notNull, mapped, qualifiers);
		this.enumerationValues = Collections.unmodifiableList(enumerationValues);
	}

}
