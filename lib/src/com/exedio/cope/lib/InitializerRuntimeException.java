
package com.exedio.cope.lib;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Replacement for {@link RuntimeException},
 * but prints the stack trace at constructtion time.
 * Useful for exceptions in static initializers,
 * because stacktrace of such exception are often hidden
 * by the JVM.  
 */
public final class InitializerRuntimeException extends RuntimeException
{
	private final Exception cause;
	private final String message;
	
	public InitializerRuntimeException(final Exception cause)
	{
		this.cause = cause;
		this.message = null;
		printStackTrace();
	}
	
	public InitializerRuntimeException(final String message)
	{
		this.cause = null;
		this.message = message;
		printStackTrace();
	}
	
	public InitializerRuntimeException(final Exception cause, final String message)
	{
		this.cause = cause;
		this.message = message;
		printStackTrace();
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
