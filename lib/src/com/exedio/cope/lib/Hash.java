
package com.exedio.cope.lib;

public abstract class Hash
{
	final StringAttribute storage;

	public Hash(final StringAttribute storage)
	{
		this.storage = storage;
	}
	
	public abstract String hash(String plainText);
	
	public void setHash(final Item item, final String plainText)
		throws
			UniqueViolationException,
			NotNullViolationException,
			LengthViolationException,
			ReadOnlyViolationException
	{
		item.setAttribute(storage, hash(plainText));
	}
	
	public boolean checkHash(final Item item, final String actualPlainText)
	{
		final String expectedHash = (String)item.getAttribute(storage);
		final String actualHash = hash(actualPlainText);
		if(expectedHash==null)
			return actualHash==null;
		else
			return expectedHash.equals(actualHash);
	}

}
