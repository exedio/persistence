/*
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

package com.exedio.cope;

import java.sql.Connection;

final class SequentialPkSource extends PkSource
{
	
	SequentialPkSource(final Table table)
	{
		super(table);
	}

	private int nextPk = Type.NOT_A_PK;
	private final Object lock = new Object();
	
	@Override
	void flushPK()
	{
		synchronized(lock)
		{
			nextPk = Type.NOT_A_PK;
		}
	}

	@Override
	int nextPK(final Connection connection)
	{
		synchronized(lock)
		{
			if(nextPk==Type.NOT_A_PK)
			{
				final int[] minMaxPks = table.database.getMinMaxPK(connection, table);
				if(minMaxPks==null)
				{
					nextPk = 0;
				}
				else
				{
					if(minMaxPks.length!=2)
						throw new RuntimeException(String.valueOf(minMaxPks.length));
					
					if(minMaxPks[0]<0)
						throw new RuntimeException(
								"The smallest pk for table " + table.id +
									" is " + minMaxPks[0] + " but must be non-negative for use with SequentialPkSource." +
								" You may consider using " + Properties.PKSOURCE_BUTTERFLY + " instead.");
					
					nextPk = minMaxPks[1]+1;
				}
			}
			
			final int result = nextPk++;
	
			if(result==Type.NOT_A_PK) // pk overflow
				throw new RuntimeException();
			return result;
		}
	}

	@Override
	long pk2id(final int pk)
	{
		// needs no synchronized, since this method
		// does not use any member variables.

		if(pk==Type.NOT_A_PK)
			throw new RuntimeException("not a pk");

		return pk;
	}

	@Override
	int id2pk(final long id, final String idString)
			throws NoSuchIDException
	{
		// needs no synchronized, since this method
		// does not use any member variables.

		if(id<0)
			throw new NoSuchIDException(idString, true, "must be positive");
		if(id>=2147483648l)
			throw new NoSuchIDException(idString, true, "does not fit in 31 bit");

		return (int)id;
	}
	
	@Override
	void appendOrderByExpression(final Statement bf, final Function orderBy)
	{
		bf.append(orderBy, (Join)null);
	}

	@Override
	int[] getPrimaryKeyInfo()
	{
		return new int[]{nextPk};
	}
}
