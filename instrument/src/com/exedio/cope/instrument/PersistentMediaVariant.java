package com.exedio.cope.instrument;


final class CopeMediaVariant
{
	final String name;
	final CopeMediaAttribute mediaAttribute;

	public CopeMediaVariant(final JavaAttribute javaAttribute, final CopeMediaAttribute mediaAttribute)
	{
		this.name = javaAttribute.name;
		this.mediaAttribute = mediaAttribute;
		mediaAttribute.addVariant(this);
	}

}
