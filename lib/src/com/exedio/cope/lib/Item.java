
package persistence;

public class Item extends Search
{
	
	protected Item(final AttributeValue[] initialAttributesValues)
	throws UniqueViolationException, NotNullViolationException
	{}
	
	protected final Object getAttribute(final Attribute attribute)
	{
		return null;
	}
	
	protected final Object getAttribute(final Attribute attribute, final Object[] qualifiers)
	{
		return null;
	}
	
	protected final void setAttribute(final Attribute attribute, final Object value)
	throws UniqueViolationException, NotNullViolationException, ReadOnlyViolationException
	{
		if(value == null)
			throw new NotNullViolationException();
	}
	
	protected final void setAttribute(final Attribute attribute, final Object[] qualifiers, final Object value)
	throws UniqueViolationException
	{
	}
	
}
