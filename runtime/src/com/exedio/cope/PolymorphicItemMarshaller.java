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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

final class PolymorphicItemMarshaller<E extends Item> extends Marshaller<E>
{
	private final HashMap<String, Type<? extends E>> typesOfInstancesMap;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	PolymorphicItemMarshaller(final HashMap<String, Type<? extends E>> typesOfInstancesMap)
	{
		super(2);
		this.typesOfInstancesMap = typesOfInstancesMap;
		assert typesOfInstancesMap!=null;
	}

	@Override
	E unmarshal(final ResultSet row, final int columnIndex) throws SQLException
	{
		final long pkCell = row.getLong(columnIndex);
		final boolean pkCellNull = row.wasNull();
		final String typeCell = row.getString(columnIndex + 1);

		if(pkCellNull!=(typeCell==null))
			throw new RuntimeException("inconsistent type column >" + pkCell + "< / >" + typeCell + '<');

		if(pkCellNull)
			return null;

		final Type<? extends E> resultType = typesOfInstancesMap.get(typeCell);
		if(resultType==null)
			throw new RuntimeException("invalid type column >" + pkCell + "< / >" + typeCell + '<');

		return resultType.getItemObject(((Number)pkCell).longValue());
	}

	@Override
	String marshalLiteral(final E value)
	{
		return String.valueOf(value.pk);
	}

	@Override
	Object marshalPrepared(final E value)
	{
		return value.pk;
	}
}
