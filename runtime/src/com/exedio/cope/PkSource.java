/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.sql.Connection;

final class PkSource
{
	private final Table table;
	
	PkSource(final Table table)
	{
		this.table = table;
	}

	private int nextPk = Type.NOT_A_PK;
	private final Object lock = new Object();
	
	void flushPK()
	{
		synchronized(lock)
		{
			nextPk = Type.NOT_A_PK;
		}
	}

	int nextPK(final Connection connection)
	{
		final int result;
		
		synchronized(lock)
		{
			if(nextPk==Type.NOT_A_PK)
			{
				final Integer maxPK = table.database.nextPK(connection, table);
				nextPk = maxPK!=null ? (maxPK.intValue()+1) : 0;
			}
			
			result = nextPk++;
		}
		
		if(!isPk(result)) // pk overflow
			throw new RuntimeException();
		
		return result;
	}

	static long pk2id(final int pk)
	{
		if(!isPk(pk))
			throw new IllegalArgumentException("not a pk");
		
		return pk;
	}
	
	static boolean isPk(final int pk)
	{
		return pk>=Type.MIN_PK && pk<=Type.MAX_PK;
	}

	Integer getPrimaryKeyInfo()
	{
		return nextPk!=Type.NOT_A_PK ? nextPk : null;
	}
}
