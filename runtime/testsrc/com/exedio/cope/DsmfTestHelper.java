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

import com.exedio.dsmf.Dialect;

/**
 * Helper for accessing package private members by dsmf tests.
 */
public final class DsmfTestHelper
{
	public static Dialect dialect(final Model model)
	{
		return copeDialect(model).dsmfDialect;
	}

	public static String getIntegerType(final Model model, final long minimum, final long maximum)
	{
		return copeDialect(model).getIntegerType(minimum, maximum);
	}

	public static String getStringType(final Model model, final int maxChars)
	{
		return copeDialect(model).getStringType(maxChars, null);
	}

	private static com.exedio.cope.Dialect copeDialect(final Model model)
	{
		return model.connect().dialect;
	}


	private DsmfTestHelper()
	{
		// prevent instantiation
	}
}
