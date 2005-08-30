package com.exedio.cope;

import java.util.HashMap;
import java.util.Map;

final class PersistentState extends State
{
	
	// TODO: use arrays for String/int/double instead of the HashMap
	private final Map cache;
	
	PersistentState( final Item item )
	{
		super( item );
		cache = new HashMap();
		type.getModel().getDatabase().load( this );
	}
	
	PersistentState( final State original )
	{
		super( original.item );
		cache = original.stealValues();
		if ( cache==null ) throw new RuntimeException( original.getClass().getName() );
	}
	
	Object get(ObjectAttribute attribute)
	{
		final Column column = attribute.getColumn();
		final Object cachedValue = cache.get(column);
		return attribute.cacheToSurface( cachedValue );
	}

	public final State put(final Transaction transaction, final ObjectAttribute attribute, final Object value)
	{
		return new ModifiedState( transaction, this ).put(transaction, attribute, value);
	}

	State write( final Transaction transaction ) throws UniqueViolationException
	{
		return this;
	}

	State delete(Transaction transaction)
	{
		return new DeletedState( transaction, this );
	}

	void load(final StringColumn column, final String value)
	{
		cache.put(column, value);
	}
	
	void load(final IntegerColumn column, final long value)
	{
		cache.put(column, column.longInsteadOfInt ? (Number)new Long(value) : new Integer((int)value));
	}
	
	void load(final DoubleColumn column, final double value)
	{
		cache.put(column, new Double(value));
	}
	
	void load(final TimestampColumn column, final long value)
	{
		cache.put(column, new Long(value));
	}
	
	Object store(final Column column)
	{
		throw new RuntimeException();
	}

	Map stealValues()
	{
		return new HashMap( cache );
	}

	boolean exists()
	{
		return true;
	}
}
