
package com.exedio.cope.lib;

import java.sql.SQLException;

/**
 * Signals, that an attempt to delete an item has been failed,
 * because some other item point to that item with some
 * {@link ItemAttribute item attribute}.
 * <p>
 * Also knows as foreign key constraint violation.
 * <p>
 * This exception is thrown by {@link Item#delete}.
 */
public class IntegrityViolationException extends ConstraintViolationException
{
	private final ItemAttribute attribute;

	/**
	 * Creates a new UniqueViolationException with the neccessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param attribute initializes, what is returned by {@link #getAttribute()}.
	 * @throws NullPointerException if <code>attribute</code> is null.
	 */
	IntegrityViolationException(final SQLException cause, final Item item, final ItemAttribute attribute)
	{
		super(cause);
		if(cause==null)
			throw new NullPointerException();
		if(attribute==null)
			throw new NullPointerException();
		this.attribute = attribute;
	}

	/**
	 * Returns the item that was attempted to be deleted.
	 */
	public final Item getItem()
	{
		return null;
	}

	/**
	 * Returns the item attribute, for which the integrity (foreign key) constraint has been violated.
	 */
	public ItemAttribute getAttribute()
	{
		return attribute;
	}
	
}
