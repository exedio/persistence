
package com.exedio.cope.lib.pattern;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.ItemAttribute;
import com.exedio.cope.lib.UniqueConstraint;

public final class Qualifier
{
	private final ItemAttribute parent;
	private final Attribute key;
	private final UniqueConstraint qualifyUnique;

	public Qualifier(ItemAttribute parent, Attribute key, UniqueConstraint qualifyUnique)
	{
		this.parent = parent;
		this.key = key;
		this.qualifyUnique = qualifyUnique;
	}
	
	public final ItemAttribute getParent()
	{
		return parent;
	}

	public final Attribute getKey()
	{
		return key;
	}

	public final UniqueConstraint getQualifyUnique()
	{
		return qualifyUnique;
	}

}
