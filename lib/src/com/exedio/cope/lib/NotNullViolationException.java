
package com.exedio.cope.lib;

/**
 * Is thrown, when a persistent modification tries to set a not-null attribute to null.
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
	 * Returns the item that was tried to be modified.
	 */
	public final Item getItem()
	{
		return item;
	}

	/**
	 * Returns the attribute, that was tried to be written.
	 */
	public Attribute getNotNullAttribute()
	{
		return notNullAttribute;
	}

}
