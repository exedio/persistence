
package com.exedio.cope.lib;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Is thrown, when a persistent modification tries to violate a unique-constraint.
 */
public final class UniqueViolationException extends ConstraintViolationException
{
	private final SQLException cause;
	private final UniqueConstraint constraint;
	
	/**
	 * Creates a new UniqueViolationException with the neccessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param constraint initializes, what is returned by {@link #getConstraint()}.
	 * @throws NullPointerException if <code>constraint</code> is null.
	 */
	UniqueViolationException(final SQLException cause, final Item item, final UniqueConstraint constraint)
	{
		if(cause==null)
			throw new NullPointerException();
		if(constraint==null)
			throw new NullPointerException();
		this.cause = cause;
		this.constraint = constraint;
	}
	
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
		return constraint;
	}
	
	public void printStackTrace()
	{
		super.printStackTrace();
		cause.printStackTrace();
	}

	public void printStackTrace(final PrintStream s)
	{
		super.printStackTrace(s);
		cause.printStackTrace(s);
	}

	public void printStackTrace(final PrintWriter s)
	{
		super.printStackTrace(s);
		cause.printStackTrace(s);
	}

}
