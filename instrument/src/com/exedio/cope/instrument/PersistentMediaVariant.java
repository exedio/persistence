package com.exedio.cope.instrument;


public final class PersistentMediaVariant
{
	final String name;
	final PersistentMediaAttribute mediaAttribute;

	public PersistentMediaVariant(final JavaAttribute javaAttribute, final PersistentMediaAttribute mediaAttribute)
	{
		this.name = javaAttribute.name;
		this.mediaAttribute = mediaAttribute;
		mediaAttribute.addVariant(this);
	}

}
