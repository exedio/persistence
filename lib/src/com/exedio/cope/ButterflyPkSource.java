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

package com.exedio.cope;

import java.sql.Connection;

final class ButterflyPkSource extends PkSource
{
	
	ButterflyPkSource(final Table table)
	{
		super(table);
	}

	private int nextPkLo = Type.NOT_A_PK;
	private int nextPkHi = Type.NOT_A_PK;
	private boolean nextIsLo;
	private final Object lock = new Object();
	
	void flushPK()
	{
		synchronized(lock)
		{
			nextPkLo = Type.NOT_A_PK;
			nextPkHi = Type.NOT_A_PK;
		}
	}

	int nextPK(final Connection connection)
	{
		synchronized(lock)
		{
			if(nextPkLo==Type.NOT_A_PK)
			{
				final int[] minMaxPks = table.database.getMinMaxPK(connection, table);
				if(minMaxPks==null)
				{
					nextPkLo = -1;
					nextPkHi = 0;
				}
				else
				{
					if(minMaxPks.length!=2)
						throw new RuntimeException(String.valueOf(minMaxPks.length));
					nextPkLo = minMaxPks[0]-1;
					nextPkHi = minMaxPks[1]+1;
				}

				if(nextPkLo>=nextPkHi)
					throw new RuntimeException(String.valueOf(nextPkLo)+">="+String.valueOf(nextPkHi));
				nextIsLo = (-nextPkLo)<=nextPkHi;
				//System.out.println(this.trimmedName+": getNextPK:"+nextPkLo+"/"+nextPkHi+"  nextIs"+(nextIsLo?"Lo":"Hi"));
			}
			
			//System.out.println(this.trimmedName+": nextPK:"+nextPkLo+"/"+nextPkHi+"  nextIs"+(nextIsLo?"Lo":"Hi"));
			final int result = nextIsLo ? nextPkLo-- : nextPkHi++;
			nextIsLo = !nextIsLo;
	
			if(nextPkLo>=nextPkHi) // TODO : somehow handle pk overflow
				throw new RuntimeException(String.valueOf(nextPkHi)+String.valueOf(nextPkLo));
			return result;
		}
	}

	long pk2id(final int pk)
	{
		// needs no synchronized, since this method
		// does not use any member variables.

		if(pk==Type.NOT_A_PK)
			throw new RuntimeException("not a pk");

		final long longPk = (long)pk;
		return
			(pk>=0) ?
				(longPk<<1) : // 2*pk
				-((longPk<<1)|1l); // -(2*pk + 1)
	}

	int id2pk(final long id)
			throws NoSuchIDException
	{
		// needs no synchronized, since this method
		// does not use any member variables.

		if(id<0)
			throw new NoSuchIDException(id, "must be positive");
		if(id>=4294967296l)
			throw new NoSuchIDException(id, "does not fit in 32 bit");

		final long result =
			((id&1l)>0) ? // odd id ?
				-((id>>>1)+1l) : // -(id/2 +1)
				id>>1; // id/2

		//System.out.println("id2pk: "+id+" -> "+result);
		if(result==(long)Type.NOT_A_PK)
			throw new NoSuchIDException(id, "is a NOT_A_PK");

		return (int)result;
	}
	
	void appendDeterministicOrderByExpression(final Statement bf, final Table orderByTable)
	{
		bf.append("abs(").
			append(orderByTable.protectedID).
			append('.').
			append(orderByTable.getPrimaryKey().protectedID).
			append("*4+1)");
	}

}
