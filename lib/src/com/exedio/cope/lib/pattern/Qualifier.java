
package com.exedio.cope.lib.pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.ItemAttribute;
import com.exedio.cope.lib.Type;
import com.exedio.cope.lib.UniqueConstraint;

public final class Qualifier
{
	private final ItemAttribute parent;
	private final Attribute key;
	private final UniqueConstraint qualifyUnique;
	private List attributes;

	public Qualifier(final UniqueConstraint qualifyUnique)
	{
		if(qualifyUnique==null)
			throw new RuntimeException(
				"argument of qualifier constructor is null, " +
				"may happen due to bad class intialization order.");
		
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
	
	public void initialize()
	{
		if(this.attributes!=null)
			throw new RuntimeException();

		final Type type = qualifyUnique.getType();
		final ArrayList attributesModifiyable = new ArrayList(type.getAttributes().size());
		for(Iterator i = type.getAttributes().iterator(); i.hasNext(); )
		{
			final Attribute attribute = (Attribute)i.next();
			if(attribute!=parent && attribute!=key)
				attributesModifiyable.add(attribute);
		}
		this.attributes = Collections.unmodifiableList(attributesModifiyable);
	}

	public final List getAttributes()
	{
		if(this.attributes==null)
			throw new RuntimeException();

		return attributes;
	}

}
