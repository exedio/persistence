package com.exedio.cope.instrument;


final class PersistentMediaVariant
{
	final String name;
	final CopeMediaAttribute mediaAttribute;

	public PersistentMediaVariant(final JavaAttribute javaAttribute, final CopeMediaAttribute mediaAttribute)
	{
		this.name = javaAttribute.name;
		this.mediaAttribute = mediaAttribute;
		mediaAttribute.addVariant(this);
	}

}
