
package com.exedio.cope.lib;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class UniqueConstraint
{
	private static final HashMap uniqueConstraintsByID = new HashMap();
	
	static final UniqueConstraint findByID(final String id, final SQLException e)
	{
		//System.out.println("UniqueConstraint.getUniqueConstraint:"+name);
		final UniqueConstraint result =
			(UniqueConstraint)uniqueConstraintsByID.get(id);

		if(result==null)
			throw new SystemException(e, "no unique constraint found for >"+id
																	+"<, has only "+uniqueConstraintsByID.keySet());

		return result;
	}
	
	
	private final Attribute[] uniqueAttributes;
	private final List uniqueAttributeList;
	private String id;
	private String protectedID;

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

		this.id = Database.theInstance.trimName(type.id+"_"+name+"Un");

		final Object collision = uniqueConstraintsByID.put(id, this);
		if(collision!=null)
			throw new InitializerRuntimeException(null, "ambiguous unique constraint "+this+" trimmed to >"+this.id+"< colliding with "+collision);
	}

	public final String getID()
	{
		if(id==null)
			throw new RuntimeException();
			
		return id;
	}
	
	final String getProtectedID()
	{
		if(protectedID!=null)
			return protectedID;

		this.protectedID = Database.theInstance.protectName(getID());
		return protectedID;
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
