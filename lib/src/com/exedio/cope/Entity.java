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
		state = state.put( transaction, attribute, value );
	}
	
	final void put( AttributeValue[] attributeValues )
	{
		for ( int i=0; i<attributeValues.length; i++ )
		{
			final AttributeValue nextAttributeValue = attributeValues[i];
			put( nextAttributeValue.attribute, nextAttributeValue.value );
		}	
	}
	
	void write() throws UniqueViolationException, IntegrityViolationException
	{
		state = state.write( transaction );
	}
	
	void delete() throws IntegrityViolationException
	{
		state = state.delete( transaction );
	}
	
	Item getItem()
	{
		return state.item;
	}
	
	boolean exists()
	{
		return state.exists();
	}
	
	public String toString()
	{
		return "Entity["+(state==null?"no state":state.toString())+"]";
	}
}
