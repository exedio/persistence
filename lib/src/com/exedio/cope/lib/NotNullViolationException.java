
package com.exedio.cope.lib;

/**
 * Signals, that an attempt to write an attribute has been failed,
 * because it cannot be written with a null value.
 *
 * This exception will be thrown by {@link Item#setAttribute Item.setAttribute} methods
 * and item constructors
 * if that attribute is {@link Attribute#isNotNull() not-null}.
 */
public final class NotNullViolationException extends ConstraintViolationException
{
	private final Item item;
	private final Attribute notNullAttribute;
	
	/**
	 * Creates a new NotNullViolationException with the neccessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param notNullAttribute initializes, what is returned by {@link #getNotNullAttribute()}.
	 */
	public NotNullViolationException(final Item item, final Attribute notNullAttribute)
	{
		this.item = item;
		this.notNullAttribute = notNullAttribute;
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
	public Attribute getNotNullAttribute()
	{
		return notNullAttribute;
	}

}
