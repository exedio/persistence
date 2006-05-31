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

import java.util.List;

abstract class PkSource
{
	final Type type;
	
	PkSource(final Type type)
	{
		this.type = type;
	}

	abstract void flushPK();
	abstract int nextPK();
	abstract long pk2id(int pk);
	abstract int id2pk(long id, String idString) throws NoSuchIDException;

	/**
	 * There may be one PkSource for many tables (in a inheritance tree),
	 * so we have to specify the orderByType here,
	 * since orderByType.getTable() may not be equal to this.table.
	 */
	abstract void appendDeterministicOrderByExpression(Statement bf, Type orderBy);
	
	abstract void appendOrderByExpression(Statement bf, Function orderBy);
	
	protected final int[] getMinMaxPK()
	{
		final Query<List> q = new Query<List>(new Selectable[]{type.getThis().min(), type.getThis().max()}, type, null);
		final List qr = q.searchSingleton();

		assert qr.size()==2;
		final Item minItem = (Item)qr.get(0);
		final Item maxItem = (Item)qr.get(1);

		assert (minItem==null) == (maxItem==null);
		if(minItem==null)
			return null;
		
		final int[] result = new int[2];
		result[0] = minItem.pk;
		result[1] = maxItem.pk;
		return result;
	}

}
