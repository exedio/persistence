
package com.exedio.cope.lib.pattern;

import java.util.List;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.ItemAttribute;
import com.exedio.cope.lib.UniqueConstraint;

public final class Qualifier
{
	private final ItemAttribute parent;
	private final Attribute key;
	private final UniqueConstraint qualifyUnique;

	public Qualifier(final UniqueConstraint qualifyUnique)
	{
		final List attributes = qualifyUnique.getUniqueAttributes();
		if(attributes.size()!=2)
			throw new RuntimeException(attributes.toString());

		this.parent = (ItemAttribute)attributes.get(0);
		this.key = (ItemAttribute)attributes.get(1);
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
