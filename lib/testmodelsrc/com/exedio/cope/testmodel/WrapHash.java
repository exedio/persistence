
package com.exedio.cope.testmodel;

import com.exedio.cope.lib.Hash;
import com.exedio.cope.lib.StringAttribute;


/**
 * A nonsense test hash for unit-testing the hashing mechanism.
 */
public class WrapHash extends Hash
{
	public WrapHash(final StringAttribute storage)
	{
		super(storage);
	}

	public String hash(final String plainText)
	{
		if(plainText == null)
			return null;
		else
			return '[' + plainText + ']';
	}

}
