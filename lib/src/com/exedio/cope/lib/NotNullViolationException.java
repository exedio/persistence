
package com.exedio.cope.lib;

/**
 * Is thrown, when a persistent modification tries to set a not-null attribute to null.
 */
public class NotNullViolationException extends ConstraintViolationException
{
	
	/**
	 * Creates a new NotNullViolationException with the neccessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param notNullAttribute initializes, what is returned by {@link #getNotNullAttribute()}.
	 */
	public NotNullViolationException(final Item item, final Attribute notNullAttribute)
	{
	}
	
	/**
	 * Returns the item that was tried to be modified.
	 */
	public final Item getItem()
	{
		return null;
	}

	/**
	 * Returns the attribute, that was tried to be written.
	 */
	public Attribute getNotNullAttribute()
	{
		return null;
	}

}
