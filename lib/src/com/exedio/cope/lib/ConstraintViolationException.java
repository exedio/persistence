
package com.exedio.cope.lib;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Is thrown, when a persistent modification violates a constraint.
 */
public abstract class ConstraintViolationException extends Exception
{
	private final SQLException cause;

	public ConstraintViolationException(final SQLException cause)
	{
		this.cause = cause;
	}
	
	public void printStackTrace()
	{
		super.printStackTrace();
		if(cause!=null)
			cause.printStackTrace();
	}

	public void printStackTrace(final PrintStream s)
	{
		super.printStackTrace(s);
		if(cause!=null)
			cause.printStackTrace(s);
	}

	public void printStackTrace(final PrintWriter s)
	{
		super.printStackTrace(s);
		if(cause!=null)
			cause.printStackTrace(s);
	}

}
