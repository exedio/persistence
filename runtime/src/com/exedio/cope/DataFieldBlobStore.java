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

import com.exedio.cope.DataField.Value;
import java.io.OutputStream;

final class DataFieldBlobStore extends DataFieldStore
{
	private final BlobColumn column;

	DataFieldBlobStore(
			final DataField field,
			final Table table,
			final String name,
			final boolean optional,
			final long maximumLength)
	{
		super(field);
		this.column = new BlobColumn(table, name, optional, maximumLength);
	}

	@Override
	Column column()
	{
		return column;
	}

	@Override
	BlobColumn blobColumnIfSupported(final String capability)
	{
		return column;
	}

	@Override
	void appendHashExpression(final Statement bf, final String algorithm)
	{
		bf.dialect.appendBlobHash(bf, column, null, algorithm);
	}

	@Override
	boolean isNull(final Transaction tx, final Item item)
	{
		return column.isNull(tx, item);
	}

	@Override
	long loadLength(final Transaction tx, final Item item)
	{
		return column.loadLength(tx, item);
	}

	@Override
	byte[] load(final Transaction tx, final Item item)
	{
		return column.load(tx, item);
	}

	@Override
	void load(final Transaction tx, final Item item, final OutputStream sink)
	{
		column.load(tx, item, sink, field);
	}

	@Override
	void put(final Entity entity, final Value value, final Item exceptionItem)
	{
		// deliberately empty
	}
}
