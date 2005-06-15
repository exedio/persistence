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

package com.exedio.dsmf;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * A wrapper for an unexpected SQLException.
 * 
 * @author Ralf Wiebicke
 */
public final class SQLRuntimeException extends RuntimeException
{
	private final SQLException cause;
	private final String statement;
	
	public SQLRuntimeException(final SQLException cause, final String statement)
	{
		if(cause==null)
			throw new NullPointerException();
		if(statement==null)
			throw new NullPointerException();

		this.cause = cause;
		this.statement = statement;
	}
	
	public SQLException getNestedCause()
	{
		return cause;
	}
	
	public String getMessage()
	{
		return statement + ":" + cause.getMessage();
	}
	
	public void printStackTrace()
	{
		super.printStackTrace();
		System.out.println(statement);
		cause.printStackTrace();
	}

	public void printStackTrace(final PrintStream s)
	{
		super.printStackTrace(s);
		s.println(statement);
		cause.printStackTrace(s);
	}

	public void printStackTrace(final PrintWriter s)
	{
		super.printStackTrace(s);
		s.println(statement);
		cause.printStackTrace(s);
	}

}
