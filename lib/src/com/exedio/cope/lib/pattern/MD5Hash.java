package com.exedio.cope.lib.pattern;

import com.exedio.cope.lib.StringAttribute;

public final class MD5Hash extends JavaHash
{
	public MD5Hash(final StringAttribute storage)
	{
		super(storage, "MD5");
	}

	public MD5Hash(final StringAttribute storage, final String encoding)
	{
		super(storage, "MD5", encoding);
	}

}