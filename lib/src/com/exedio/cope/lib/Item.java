
package persistence;

public class Item
{
	
	/**
	 * Du sollst mich nicht in handgeschriebenem Code aufrufen.
	 */
	protected Item(final double doNotCallMe)
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
	throws UniqueViolationException
	{
	}
	
	protected final void setAttribute(final Attribute attribute, final Object[] qualifiers, final Object value)
	throws UniqueViolationException
	{
	}
	
}
