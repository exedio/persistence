package com.exedio.cope;

import java.util.Map;

final class ModifiedState extends State
{
	
	// TODO: use arrays for String/int/double instead of the HashMap
	private Map cache;
	
	ModifiedState( final Transaction transaction, final State original )
	{
		super( original.item );
		cache = original.stealValues();
		transaction.addInvalidation( item.type, item.pk );
	}

	Object get(ObjectAttribute attribute)
	{
		return attribute.cacheToSurface(cache.get(attribute.getColumn()));
	}

	public final State put(Transaction transaction, ObjectAttribute attribute, Object value)
	{
		cache.put(attribute.getColumn(), attribute.surfaceToCache(value));
		return this;
	}

	public final State delete( Transaction transaction )
	{
		return new DeletedState( transaction, this );
	}

	State write(Transaction transaction) throws UniqueViolationException
	{
		try
		{
			type.getModel().getDatabase().store( this, true );
			return new PersistentState( this );
		}
		catch ( UniqueViolationException e )
		{
			discard( transaction );
			throw e;
		}
		catch ( RuntimeException e )
		{
			discard( transaction );
			throw e;
		}
		catch ( Error e )
		{
			discard( transaction );
			throw e;
		}
	}
	
	Object store(final Column column)
	{
		return cache.get(column);
	}

	Map stealValues()
	{
		Map result = cache;
		cache = null;
		return result;
	}

	boolean exists()
	{
		return true;
	}
}
