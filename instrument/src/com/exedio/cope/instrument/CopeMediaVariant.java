package com.exedio.cope.instrument;


final class CopeMediaVariant
{
	final String name;
	final String shortName;
	final CopeMediaAttribute mediaAttribute;

	public CopeMediaVariant(final JavaAttribute javaAttribute, final CopeMediaAttribute mediaAttribute)
	{
		this.name = javaAttribute.name;
		this.mediaAttribute = mediaAttribute;
		final String prefix = mediaAttribute.getName();
		this.shortName = this.name.startsWith(prefix)
				? this.name.substring(prefix.length())
				: this.name;
		mediaAttribute.addVariant(this);
	}

}
