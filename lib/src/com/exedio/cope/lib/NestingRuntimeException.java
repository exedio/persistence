
package com.exedio.cope.lib;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Is thrown, when a fatal unspecified error occurs.
 * Can nests an inner exception.
 * Is a replacement of the nesting functionality of RuntimeException
 * in JDK 1.4.
 */
public final class NestingRuntimeException extends RuntimeException
{
	private final Exception cause;
	private final String message;
	
	public NestingRuntimeException(final Exception cause)
	{
		this.cause = cause;
		this.message = null;
	}
	
	public NestingRuntimeException(final Exception cause, final String message)
	{
		this.cause = cause;
		this.message = message;
	}
	
	public Exception getNestedCause()
	{
		return cause;
	}
	
	public String getMessage()
	{
		if(message!=null)
		{
			if(cause!=null)
				return message + ":" + cause.getMessage();
			else
				return message;
		}
		else
		{
			if(cause!=null)
				return cause.getMessage();
			else
				return "";
		}
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
