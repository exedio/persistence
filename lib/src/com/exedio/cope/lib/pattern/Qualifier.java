
package com.exedio.cope.lib.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.AttributeValue;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.ItemAttribute;
import com.exedio.cope.lib.LengthViolationException;
import com.exedio.cope.lib.NestingRuntimeException;
import com.exedio.cope.lib.NotNullViolationException;
import com.exedio.cope.lib.ObjectAttribute;
import com.exedio.cope.lib.ReadOnlyViolationException;
import com.exedio.cope.lib.Type;
import com.exedio.cope.lib.UniqueConstraint;
import com.exedio.cope.lib.UniqueViolationException;

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

	public final Item getQualifier(final Object[] values)
	{
		return qualifyUnique.searchUnique(values);
	}
	
	public final Object getQualified(final Object[] values, final ObjectAttribute attribute)
	{
		final Item item = qualifyUnique.searchUnique(values);
		if(item!=null)
			return item.getAttribute(attribute);
		else
			return null;
	}
	
	public final void setQualified(final Object[] values, final ObjectAttribute attribute, Object value)
	throws
		NotNullViolationException,
		LengthViolationException,
		ReadOnlyViolationException,
		ClassCastException
	{
		Item item = qualifyUnique.searchUnique(values);
		if(item==null)
		{
			final AttributeValue[] initialAttributeValues = new AttributeValue[values.length];
			int j = 0;
			for(Iterator i = qualifyUnique.getUniqueAttributes().iterator(); i.hasNext(); j++)
			{
				final ObjectAttribute uniqueAttribute = (ObjectAttribute)i.next();
				initialAttributeValues[j] = new AttributeValue(uniqueAttribute, values[j]);
			}
			item = qualifyUnique.getType().newItem(initialAttributeValues);
		}

		try
		{
			item.setAttribute(attribute, value);
		}
		catch(UniqueViolationException e)
		{
			throw new NestingRuntimeException(e);
		}
	}
		
}
