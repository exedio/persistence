
package com.exedio.cope.lib;

/**
 * Is thrown, when a fatal unspecified error occurs.
 */
public class SystemException extends RuntimeException
{
	private final Exception cause;
	
	public SystemException(final Exception cause)
	{
		this.cause = cause;
	}
	
}
