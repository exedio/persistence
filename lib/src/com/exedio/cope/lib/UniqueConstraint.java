
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
	private static int runningNumber = 0;

	
	private final Attribute[] uniqueAttributes;
	private final List uniqueAttributeList;
	final String trimmedName;
	final String protectedName;

	public UniqueConstraint(final Attribute[] uniqueAttributes)
	{
		this.uniqueAttributes = uniqueAttributes;
		this.uniqueAttributeList = Collections.unmodifiableList(Arrays.asList(uniqueAttributes));
		
		final StringBuffer nameBuffer = new StringBuffer();
		for(int i = 0; i<uniqueAttributes.length; i++)
			nameBuffer.append(uniqueAttributes[i].getName());
		nameBuffer.append(runningNumber++);
		
		this.trimmedName = Database.theInstance.trimName(nameBuffer.toString());
		this.protectedName = Database.theInstance.protectName(this.trimmedName);

		final Object collision = uniqueConstraintsByName.put(trimmedName, this);
		if(collision!=null)
			throw new SystemException(null, "ambiguous unique constraint "+this+" trimmed to >"+this.trimmedName+"< colliding with "+collision);
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
