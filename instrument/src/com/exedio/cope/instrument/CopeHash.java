package com.exedio.cope.instrument;


final class CopeHash
{
	final String name;
	final CopeAttribute storageAttribute;

	public CopeHash(final JavaAttribute javaAttribute, final CopeAttribute storageAttribute)
	{
		this.name = javaAttribute.name;
		this.storageAttribute = storageAttribute;
		this.storageAttribute.addHash(this);
	}

}
