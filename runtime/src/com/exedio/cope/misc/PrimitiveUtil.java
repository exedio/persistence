/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

public final class PrimitiveUtil
{
	public static Class<?> toPrimitive(final Class<?> clazz)
	{
		     if(clazz==Boolean  .class) return boolean.class;
		else if(clazz==Character.class) return char   .class;
		else if(clazz==Byte     .class) return byte   .class;
		else if(clazz==Short    .class) return short  .class;
		else if(clazz==Integer  .class) return int    .class;
		else if(clazz==Long     .class) return long   .class;
		else if(clazz==Float    .class) return float  .class;
		else if(clazz==Double   .class) return double .class;
		else
			return null;
	}

	private PrimitiveUtil()
	{
		// prevent instantiation
	}
}
