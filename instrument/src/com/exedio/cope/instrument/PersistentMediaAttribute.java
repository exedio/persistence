
package com.exedio.cope.instrument;

import java.util.Collections;
import java.util.List;

public final class PersistentMediaAttribute extends PersistentAttribute
{
	public final List mediaVariants;
	public final String mimeMajor;
	public final String mimeMinor;

	public PersistentMediaAttribute(
			final JavaAttribute javaAttribute,
			final boolean readOnly, final boolean notNull, final boolean mapped,
			final List qualifiers, final List mediaVariants,
			final String mimeMajor, final String mimeMinor)
	{
		super(javaAttribute, MEDIA_TYPE, TYPE_MEDIA, readOnly, notNull, mapped, qualifiers);
		this.mediaVariants = Collections.unmodifiableList(mediaVariants);
		this.mimeMajor = mimeMajor;
		this.mimeMinor = mimeMinor;
	}

}
