
package com.exedio.cope.lib;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Is thrown, when a fatal unspecified error occurs.
 */
public class SystemException extends RuntimeException
{
	private final Exception cause;
	private final String message;
	
	public SystemException(final Exception cause)
	{
		this.cause = cause;
		this.message = null;
	}
	
	public SystemException(final Exception cause, final String message)
	{
		this.cause = cause;
		this.message = message;
	}
	
	public void printStackTrace()
	{
		super.printStackTrace();
		if(message!=null)
			System.out.println(message);
		cause.printStackTrace();
	}

	public void printStackTrace(final PrintStream s)
	{
		super.printStackTrace(s);
		if(message!=null)
			s.println(message);
		cause.printStackTrace(s);
	}

	public void printStackTrace(final PrintWriter s)
	{
		super.printStackTrace(s);
		if(message!=null)
			s.println(message);
		cause.printStackTrace(s);
	}

}
