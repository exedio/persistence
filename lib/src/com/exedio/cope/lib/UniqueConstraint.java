
package com.exedio.cope.lib;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class UniqueConstraint
{
	private static final HashMap uniqueConstraintsByName = new HashMap();
	
	static final UniqueConstraint getUniqueConstraint(final String name)
	{
		//System.out.println("UniqueConstraint.getUniqueConstraint:"+name);
		return (UniqueConstraint)uniqueConstraintsByName.get(name);
	}
	
	// TODO: create a speaking name from the attributes
	private static int runningNumber = 5;


	private final Attribute[] uniqueAttributes;
	private final List uniqueAttributeList;
	final String trimmedName;
	final String protectedName;

	public UniqueConstraint(final Attribute[] uniqueAttributes)
	{
		this.uniqueAttributes = uniqueAttributes;
		this.uniqueAttributeList = Collections.unmodifiableList(Arrays.asList(uniqueAttributes));
		this.trimmedName = "UC"+runningNumber++;
		this.protectedName = Database.theInstance.protectName(this.trimmedName);
		uniqueConstraintsByName.put(trimmedName, this);
	}
	
	public UniqueConstraint(final Attribute uniqueAttribute)
	{
		this(new Attribute[]{uniqueAttribute});
	}
	
	public final List getUniqueAttributes()
	{
		return uniqueAttributeList;
	}
	

	private String toStringCache = null;
	
	public final String toString()
	{
		if(toStringCache!=null)
			return toStringCache;
		
		final StringBuffer buf = new StringBuffer();
		
		buf.append("unique(");
		buf.append(uniqueAttributes[0].getName());
		for(int i = 1; i<uniqueAttributes.length; i++)
		{
			buf.append(',');
			buf.append(uniqueAttributes[i].getName());
		}
		buf.append(')');
		
		toStringCache = buf.toString();
		return toStringCache;
	}
	
}
