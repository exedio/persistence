/*
 * Copyright (C) 2000  Ralf Wiebicke
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.instrument;

/**
 * Thrown by the java parser, if the input stream is not valid
 * java language. Should never be thrown on java code, which passes
 * javac sucessfully (otherwise it's a bug.)
 * @see Injector
 *
 * @author Ralf Wiebicke
 */
class InjectorParseException extends RuntimeException
{
	private static final long serialVersionUID = 3874562896345l;
	
	protected InjectorParseException(String message)
	{
		super(message);
	}

	protected InjectorParseException(final RuntimeException cause)
	{
		super(cause);
	}
}
