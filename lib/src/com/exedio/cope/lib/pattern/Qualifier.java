
package com.exedio.cope.lib.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.ItemAttribute;
import com.exedio.cope.lib.ObjectAttribute;
import com.exedio.cope.lib.Type;
import com.exedio.cope.lib.UniqueConstraint;

public final class Qualifier
{
	private final ItemAttribute parent;
	private final ObjectAttribute[] keys;
	private final List keyList;
	private final UniqueConstraint qualifyUnique;
	private List attributes;

	public Qualifier(final UniqueConstraint qualifyUnique)
	{
		if(qualifyUnique==null)
			throw new RuntimeException(
				"argument of qualifier constructor is null, " +
				"may happen due to bad class intialization order.");
		
		final List attributes = qualifyUnique.getUniqueAttributes();
		if(attributes.size()<2)
			throw new RuntimeException(attributes.toString());

		this.parent = (ItemAttribute)attributes.get(0);
		this.keys = new ObjectAttribute[attributes.size()-1];
		for(int i = 0; i<this.keys.length; i++)
			this.keys[i] = (ObjectAttribute)attributes.get(i+1);
		this.keyList = Collections.unmodifiableList(Arrays.asList(this.keys));
		this.qualifyUnique = qualifyUnique;
	}
	
	public final ItemAttribute getParent()
	{
		return parent;
	}

	/**
	 * @return a list of {@link ObjectAttribute}s.
	 */
	public final List getKeys()
	{
		return keyList;
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
			if(attribute!=parent && !keyList.contains(attribute))
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
