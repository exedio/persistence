
package com.exedio.cope.lib;

/**
 * Is thrown, when a persistent modification tries to write a read-only attribute.
 */
public final class ReadOnlyViolationException extends ConstraintViolationException
{

	private final Item item;
	private final Attribute readOnlyAttribute;
	
	/**
	 * Creates a new ReadOnlyViolationException with the neccessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param readOnlyAttribute initializes, what is returned by {@link #getReadOnlyAttribute()}.
	 */
	public ReadOnlyViolationException(final Item item, final Attribute readOnlyAttribute)
	{
		this.item = item;
		this.readOnlyAttribute = readOnlyAttribute;
	}
	
	/**
	 * Returns the item that was tried to be modified.
	 */
	public final Item getItem()
	{
		return item;
	}

	/**
	 * Returns the attribute, that was tried to be written.
	 */
	public Attribute getReadOnlyAttribute()
	{
		return readOnlyAttribute;
	}

}
