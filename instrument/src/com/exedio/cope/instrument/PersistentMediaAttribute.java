
package com.exedio.cope.instrument;

import java.util.List;

public class PersistentMediaAttribute extends PersistentAttribute
{

	private final List mediaVariants;
	private final String mimeMajor;
	private final String mimeMinor;

	public PersistentMediaAttribute(
			final JavaAttribute javaAttribute,
			final boolean readOnly, final boolean notNull, final boolean mapped,
			final List qualifiers, final List mediaVariants,
			final String mimeMajor, final String mimeMinor)
	{
		super(javaAttribute, MEDIA_TYPE, TYPE_MEDIA, readOnly, notNull, mapped, qualifiers, null);
		this.mediaVariants = mediaVariants;
		this.mimeMajor = mimeMajor;
		this.mimeMinor = mimeMinor;
	}

	public List getMediaVariants()
	{
		return mediaVariants;
	}
	
	public String getMimeMajor()
	{
		return mimeMajor;
	}
	
	public String getMimeMinor()
	{
		return mimeMinor;
	}
	
}
