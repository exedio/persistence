
package com.exedio.cope.lib;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class ItemAttribute extends Attribute
{
	private static final HashMap itemAttributesByIntegrityConstraintName = new HashMap();
	
	static final ItemAttribute getItemAttributeByIntegrityConstraintName(final String integrityConstraintName)
	{
		return (ItemAttribute)itemAttributesByIntegrityConstraintName.get(integrityConstraintName);
	}
	

	private final Class targetTypeClass;

	public ItemAttribute(final Search.Option option, final Class targetTypeClass)
	{
		super(option);
		this.targetTypeClass = targetTypeClass;
		if(targetTypeClass==null)
			throw new NullPointerException("target type class for attribute "+this+" must not be null");
		if(!Item.class.isAssignableFrom(targetTypeClass))
			throw new NullPointerException("target type class "+targetTypeClass+" for attribute "+this+" must be a sub class of item");
	}

	/**
	 * Returns the type of items, this attribute accepts instances of.
	 */
	public Type getTargetType()
	{
		final Type result = Type.getType(targetTypeClass.getName());
		if(result==null)
			throw new NullPointerException("there is no type for class "+targetTypeClass);
		return result;
	}
	
	protected List createColumns(final String name, final boolean notNull)
	{
		final String integrityConstraintName = name+"FK";
		if(itemAttributesByIntegrityConstraintName.put(integrityConstraintName, this)!=null)
			throw new RuntimeException("there is more than one integrity constraint with name "+integrityConstraintName);
		return Collections.singletonList(new ItemColumn(getType(), name, notNull, targetTypeClass, integrityConstraintName));
	}
	
	Object cacheToSurface(final Object cache)
	{
		return 
			cache==null ? 
				null : 
				getTargetType().getItem(((Integer)cache).intValue());
	}
		
	Object surfaceToCache(final Object surface)
	{
		return
			surface==null ? 
				null : 
				new Integer(((Item)surface).pk);
	}
	
}
