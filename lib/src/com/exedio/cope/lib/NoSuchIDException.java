
package com.exedio.cope.lib;

public class NoSuchIDException extends Exception
{
	NoSuchIDException(final String id, final String detail)
	{
		super("no such id <"+id+">, "+detail);
	}

	NoSuchIDException(final String id, final NumberFormatException cause)
	{
		super("no such id <"+id+">, wrong number format <"+cause.getMessage()+">");
	}

	NoSuchIDException(final long id, final String detail)
	{
		super("no such id number <"+id+">, "+detail);
	}
}
