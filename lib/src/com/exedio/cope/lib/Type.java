
package com.exedio.cope.lib;

import com.exedio.cope.lib.Database;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class Type
{
	private static final ArrayList typesModifyable = new ArrayList();
	private static final List types = Collections.unmodifiableList(typesModifyable);
	
	private final Class javaClass;
	
	private final Attribute[] attributes;
	private final List attributeList;

	private final UniqueConstraint[] uniqueConstraints;
	private final List uniqueConstraintList;
	
	private final String persistentQualifier;

	public static final List getTypes()
	{
		return types;
	}
	
	public Type(final Class javaClass, final Attribute[] attributes, final UniqueConstraint[] uniqueConstraints, final Runnable initializer)
	{
		this.javaClass = javaClass;
		this.attributes = attributes;
		this.attributeList = Collections.unmodifiableList(Arrays.asList(attributes));
		this.uniqueConstraints = uniqueConstraints;
		this.uniqueConstraintList = Collections.unmodifiableList(Arrays.asList(uniqueConstraints));
		initializer.run();
		typesModifyable.add(this);
		this.persistentQualifier = Database.theInstance.makePersistentQualifier(this);
	}
	
	public final Class getJavaClass()
	{
		return javaClass;
	}
	
	public final List getAttributes()
	{
		return attributeList;
	}
	
	public final List getUniqueConstraints()
	{
		return uniqueConstraintList;
	}
	
	public String getPersistentQualifier()
	{
		return persistentQualifier;
	}


	private String toStringCache = null;
	
	public final String toString()
	{
		if(toStringCache!=null)
			return toStringCache;
		
		final StringBuffer buf = new StringBuffer();
		
		buf.append(javaClass.getName());
		for(int i = 0; i<uniqueConstraints.length; i++)
		{
			buf.append(' ');
			buf.append(uniqueConstraints[i].toString());
		}
		
		toStringCache = buf.toString();
		return toStringCache;
	}
	
	// active items of this type ---------------------------------------------
	
	/**
	 * TODO: use something more efficient for integer keys.
	 */
	private HashMap activeItems = new HashMap();
	
	/**
	 * Returns an item of this type and the given pk, if it's already active.
	 * Returns null, if either there is no such item with the given pk, or
	 * such an item is not active.
	 */
	Item getActiveItem(final int pk)
	{
		return (Item)activeItems.get(new Integer(pk));
	}
	
	void putActiveItem(final Item item)
	{
		activeItems.put(new Integer(item.pk), item);
	}
	
}
