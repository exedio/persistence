package com.exedio.cope;

import java.util.Map;


final class DeletedState extends State
{
	DeletedState(final Transaction transaction, final State original)
	{
		super( original.item );
		transaction.addInvalidation( item.type, item.pk );
	}
	
	Object get(final ObjectAttribute attribute)
	{
		throw new NoSuchItemException(item);		
	}

	State put(Transaction transaction, ObjectAttribute attribute, Object value)
	{
		throw new NoSuchItemException(item);
	}
	
	State write( Transaction transaction ) throws IntegrityViolationException
	{
		try
		{
			type.getModel().getDatabase().delete( item );
		}
		finally
		{
			discard( transaction );
		}
		return null;
	}

	State delete( Transaction transaction )
	{
		throw new NoSuchItemException(item);
	}

	Object store(Column column)
	{
		throw new RuntimeException();
	}

	Map stealValues()
	{
		throw new RuntimeException();
	}

	boolean exists()
	{
		return false;
	}
}
