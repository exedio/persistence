package com.exedio.cope;

import java.util.HashMap;
import java.util.Map;

final class CreatedState extends State
{
	
	// TODO: use arrays for String/int/double instead of the HashMap
	private HashMap cache = new HashMap();
	
	CreatedState(final Transaction transaction, final Item item)
	{
		super( item );
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

	State delete(Transaction transaction)
	{
		discard( transaction );
		return null;
	}

	State write(Transaction transaction) throws UniqueViolationException
	{
		try
		{
			type.getModel().getDatabase().store( this, false );
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
