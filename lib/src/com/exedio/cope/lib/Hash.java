
package com.exedio.cope.lib;

public abstract class Hash
{
	final StringAttribute storage;

	public Hash(final StringAttribute storage)
	{
		this.storage = storage;
	}
	
	public abstract String hash(String plainText);
	
}
