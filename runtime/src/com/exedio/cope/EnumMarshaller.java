/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

final class EnumMarshaller<E extends Enum<E>> implements Marshaller<E>
{
	private final EnumFieldType<E> type;

	EnumMarshaller(final EnumFieldType<E> type)
	{
		this.type = type;
	}

	@Override
	public E unmarshal(final ResultSet row, final IntHolder columnIndex) throws SQLException
	{
		final Object cell = row.getObject(columnIndex.value++);
		if(cell==null)
			return null;

		return type.getValueByNumber(((Number)cell).intValue());
	}

	@Override
	public String marshal(final E value)
	{
		return String.valueOf(type.getNumber(value)); // TODO precompute strings
	}

	@Override
	public Object marshalPrepared(final E value)
	{
		return type.getNumber(value);
	}
}
