
package com.exedio.cope.lib;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class ItemAttribute extends ObjectAttribute
{
	private static final HashMap itemAttributesByIntegrityConstraintName = new HashMap();
	
	static final ItemAttribute getItemAttributeByIntegrityConstraintName(final String name, final SQLException e)
	{
		final ItemAttribute result =
			(ItemAttribute)itemAttributesByIntegrityConstraintName.get(name);

		if(result==null)
			throw new SystemException(e, "no item attribute found for >"+name
																	+"<, has only "+itemAttributesByIntegrityConstraintName.keySet());

		return result;
	}
	

	private final Class targetTypeClass;

	public ItemAttribute(final Option option, final Class targetTypeClass)
	{
		super(option);
		this.targetTypeClass = targetTypeClass;
		if(targetTypeClass==null)
			throw new InitializerRuntimeException("target type class for attribute "+this+" must not be null");
		if(!Item.class.isAssignableFrom(targetTypeClass))
			throw new InitializerRuntimeException("target type class "+targetTypeClass+" for attribute "+this+" must be a sub class of item");
	}

	/**
	 * Returns the type of items, this attribute accepts instances of.
	 */
	public Type getTargetType()
	{
		final Type result = Type.findByJavaClass(targetTypeClass);
		if(result==null)
			throw new NullPointerException("there is no type for class "+targetTypeClass);
		return result;
	}
	
	protected List createColumns(final String name, final boolean notNull)
	{
		final String integrityConstraintName = Database.theInstance.trimName(getType().id+"_"+name+"Fk");
		if(itemAttributesByIntegrityConstraintName.put(integrityConstraintName, this)!=null)
			throw new InitializerRuntimeException("there is more than one integrity constraint with name "+integrityConstraintName);
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
