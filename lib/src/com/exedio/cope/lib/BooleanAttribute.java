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

import java.util.Collections;
import java.util.List;

public final class BooleanAttribute extends ObjectAttribute
{
	static final int[] ALLOWED_VALUES = new int[]{0, 1};

	/**
	 * see Item#booleanAttribute(Option)
	 */
	BooleanAttribute(final Option option)
	{
		super(option);
	}
	
	protected List createColumns(final Table table, final String name, final boolean notNull)
	{
		return Collections.singletonList(new IntegerColumn(table, name, notNull, 1, false, ALLOWED_VALUES));
	}
	
	Object cacheToSurface(final Object cache)
	{
		if(cache==null)
			return null;
		else
		{
			switch(((Integer)cache).intValue())
			{
				case 0:
					return Boolean.FALSE;
				case 1:
					return Boolean.TRUE;
				default:
					throw new RuntimeException("cacheToSurface:"+cache);
			}
		}
	}
	
	static final Integer FALSE = new Integer(0);
	static final Integer TRUE = new Integer(1);
		
	Object surfaceToCache(final Object surface)
	{
		return
			surface==null ?
				null :
				((Boolean)surface).booleanValue() ? TRUE : FALSE;
	}
	
}
