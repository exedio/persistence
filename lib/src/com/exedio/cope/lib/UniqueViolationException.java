
package com.exedio.cope.lib;

/**
 * Is thrown, when a persistent modification tries to violate a unique-constraint.
 */
public class UniqueViolationException extends ConstraintViolationException
{
	
	/**
	 * Creates a new UniqueViolationException with the neccessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param constraint initializes, what is returned by {@link #getConstraint()}.
	 */
	public UniqueViolationException(final Item item, final UniqueConstraint constraint)
	{}
	
	/**
	 * Returns the item that was tried to be modified.
	 * Is null, if the item has not yet been created,
	 * e.g. the collision occured in the constructor.
	 */
	public final Item getItem()
	{
		return null;
	}

	/**
	 * Returns the violated constraint.
	 */
	public UniqueConstraint getConstraint()
	{
		return null;
	}
	
}
