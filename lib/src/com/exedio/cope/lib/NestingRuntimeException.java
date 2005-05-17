/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.lib;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Is thrown, when a fatal unspecified error occurs.
 * Can nests an inner exception.
 * Is a replacement of the nesting functionality of RuntimeException
 * in JDK 1.4.
 * 
 * @author Ralf Wiebicke
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
