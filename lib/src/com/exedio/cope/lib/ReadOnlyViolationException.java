
package com.exedio.cope.lib;

/**
 * Signals, that an attempt to write an attribute has been failed,
 * because it cannot be written with any value.
 *
 * This exception will be thrown by {@link Item#setAttribute Item.setAttribute} methods
 * if that attribute is {@link Attribute#isReadOnly() read-only}
 * or a {@link AttributeMapping mapped attribute}.
 *
 * This exception will be thrown by item constructors
 * if that attribute is a {@link AttributeMapping mapped attribute}.
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
	 * Returns the item that was attempted to be modified.
	 */
	public final Item getItem()
	{
		return item;
	}

	/**
	 * Returns the attribute, that was attempted to be written.
	 */
	public Attribute getReadOnlyAttribute()
	{
		return readOnlyAttribute;
	}

}
