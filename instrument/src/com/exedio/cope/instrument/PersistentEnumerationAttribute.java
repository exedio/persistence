
package com.exedio.cope.instrument;

import java.util.List;

public final class PersistentEnumerationAttribute extends PersistentAttribute
{
	public PersistentEnumerationAttribute(
			final JavaAttribute javaAttribute,
			final String persistentType,
			final boolean readOnly, final boolean notNull, final boolean mapped,
			final List qualifiers)
	{
		super(javaAttribute, persistentType, readOnly, notNull, mapped, qualifiers);
	}

}
