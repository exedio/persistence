
package com.exedio.cope.lib;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class UniqueConstraint
{
	private static final HashMap uniqueConstraintsByName = new HashMap();
	
	static final UniqueConstraint getUniqueConstraint(final String name, final SQLException e)
	{
		//System.out.println("UniqueConstraint.getUniqueConstraint:"+name);
		final UniqueConstraint result =
			(UniqueConstraint)uniqueConstraintsByName.get(name);

		if(result==null)
			throw new SystemException(e, "no unique constraint found for >"+name
																	+"<, has only "+uniqueConstraintsByName.keySet());

		return result;
	}
	
	
	private final Attribute[] uniqueAttributes;
	private final List uniqueAttributeList;
	private String trimmedName;
	private String protectedName;

	private UniqueConstraint(final Attribute[] uniqueAttributes)
	{
		this.uniqueAttributes = uniqueAttributes;
		this.uniqueAttributeList = Collections.unmodifiableList(Arrays.asList(uniqueAttributes));
		for(int i = 0; i<uniqueAttributes.length; i++)
			if(uniqueAttributes[i]==null)
				throw new InitializerRuntimeException(String.valueOf(i));
	}
	
	UniqueConstraint(final Attribute uniqueAttribute)
	{
		this(new Attribute[]{uniqueAttribute});
	}
	
	public UniqueConstraint(final Attribute uniqueAttribute1, final Attribute uniqueAttribute2)
	{
		this(new Attribute[]{uniqueAttribute1, uniqueAttribute2});
	}
	
	public final List getUniqueAttributes()
	{
		return uniqueAttributeList;
	}
	
	final void initialize(final Type type, final String name)
	{
		if(type==null)
			throw new RuntimeException();
		if(name==null)
			throw new RuntimeException();

		this.trimmedName = Database.theInstance.trimName(type.trimmedName+"_"+name+"Un");

		final Object collision = uniqueConstraintsByName.put(trimmedName, this);
		if(collision!=null)
			throw new InitializerRuntimeException(null, "ambiguous unique constraint "+this+" trimmed to >"+this.trimmedName+"< colliding with "+collision);
	}

	final String getTrimmedName()
	{
		if(trimmedName==null)
			throw new RuntimeException();
			
		return trimmedName;
	}
	
	final String getProtectedName()
	{
		if(protectedName!=null)
			return protectedName;

		this.protectedName = Database.theInstance.protectName(getTrimmedName());
		return protectedName;
	}
	

	private String toStringCache = null;
	
	public final String toString()
	{
		if(toStringCache!=null)
			return toStringCache;
		
		final StringBuffer buf = new StringBuffer();
		
		//buf.append(super.toString());
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
