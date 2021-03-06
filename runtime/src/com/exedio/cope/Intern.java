/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

package com.exedio.cope;

final class Intern
{
	private Intern()
	{
		// prevent instantiation
	}

	private static final boolean skip = Boolean.parseBoolean(System.getProperty("com.exedio.cope.skipIntern"));

	static
	{
		if(skip)
			System.out.println("COPE: skipping String#intern()");
	}

	static String intern(final String s)
	{
		if(skip)
			return s;

		@SuppressWarnings("UnnecessaryLocalVariable")
		final String result = s.intern();
		//System.out.println("Model.intern >" + s + "< " + (result!=s ? "NEW" : "OLD"));
		return result;
	}
}
