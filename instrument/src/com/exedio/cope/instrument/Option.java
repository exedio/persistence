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
package com.exedio.cope.instrument;

import java.util.HashMap;


class Option
{
	// never to be instantiated
	private Option()
	{}

	static final int getOption(final String optionString)
			throws InjectorParseException
	{
		if(optionString==null)
			return Option.AUTO;
		else
		{
			final Integer setterOptionObject = (Integer)options.get(optionString);
			if(setterOptionObject==null)
				throw new InjectorParseException("invalid @cope-setter value "+optionString);
			return setterOptionObject.intValue();
		}
	}

	private static final HashMap options = new HashMap();
	
	public static final int NONE = 0;
	public static final int AUTO = 1;
	public static final int PRIVATE = 2;
	public static final int PROTECTED = 3;
	public static final int PACKAGE = 4;
	public static final int PUBLIC = 5;
	
	static
	{
		options.put("none", new Integer(NONE));
		options.put("private", new Integer(PRIVATE));
		options.put("protected", new Integer(PROTECTED));
		options.put("package", new Integer(PACKAGE));
		options.put("public", new Integer(PUBLIC));
	}
	
}
