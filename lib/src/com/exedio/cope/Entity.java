package com.exedio.cope;

final class Entity
{
	private final Transaction transaction;
	private State state;
	
	Entity( final Transaction transaction, final State state )
	{
		this.transaction = transaction;
		this.state = state;
	}
	
	Object get(final ObjectAttribute attribute)
	{
		return state.get( attribute );
	}
	
	void put(final ObjectAttribute attribute, final Object value)
	{
		state = state.put( attribute, value );
	}
	
	final void put( AttributeValue[] attributeValues )
	{
		for ( int i=0; i<attributeValues.length; i++ )
		{
			final AttributeValue nextAttributeValue = attributeValues[i];
			put( nextAttributeValue.attribute, nextAttributeValue.value );
		}	
	}
	
	void write() throws UniqueViolationException
	{
		state = state.write();
	}
	
	void delete() throws IntegrityViolationException
	{
		state = state.delete();
	}
	
	Item getItem()
	{
		return state.item;
	}
	
	boolean exists()
	{
		return !state.notExists;
	}
}
