/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.misc;

public final class ComputedInstance
{
	@Computed
	@SuppressWarnings("unused")
	private static final int source = 0;
	private static final java.lang.reflect.Field instance;
	
	static
	{
		try
		{
			instance = ComputedInstance.class.getDeclaredField("source");
		}
		catch(NoSuchFieldException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static final java.lang.reflect.Field get()
	{
		return instance;
	}
	
	private ComputedInstance()
	{
		// prevent instantiation
	}
}
