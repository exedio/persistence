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

final class SimpleItemMarshaller<E extends Item> extends Marshaller<E>
{
	private final Type<? extends E> onlyPossibleTypeOfInstances;

	SimpleItemMarshaller(final Type<? extends E> onlyPossibleTypeOfInstances)
	{
		super(1);
		this.onlyPossibleTypeOfInstances = onlyPossibleTypeOfInstances;
		assert onlyPossibleTypeOfInstances!=null;
	}

	@Override
	E unmarshal(final ResultSet row, final int columnIndex) throws SQLException
	{
		final long cell = row.getLong(columnIndex);
		if(row.wasNull())
			return null;

		return onlyPossibleTypeOfInstances.getItemObject(cell);
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
