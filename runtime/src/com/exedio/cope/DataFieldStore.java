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
import java.util.IdentityHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

abstract class DataFieldStore
{
	final DataField field;

	DataFieldStore(final DataField field)
	{
		this.field = field;
	}

	abstract Column column();
	void toBlobs(
			final Supplier<byte[]> value,
			final IdentityHashMap<BlobColumn, byte[]> result)
	{
		// do nothing
	}
	abstract void appendHashExpression(Statement bf, String algorithm);
	abstract boolean isNull(Transaction tx, Item item);
	abstract long loadLength(Transaction tx, Item item);
	abstract byte[] load(Transaction tx, Item item);
	abstract void load(Transaction tx, Item item, OutputStream sink);
	abstract String getHash(Transaction tx, Item item);
	abstract VaultAncestry getVaultAncestry(Transaction tx, Item item);
	abstract void put(Entity entity, Value value, Item exceptionItem);

	String getBucket()
	{
		return null;
	}

	Statement checkVaultTrail(final Statement.Mode mode)
	{
		throw new IllegalStateException("vault is disabled");
	}

	DataFieldVaultInfo getVaultInfo()
	{
		return null;
	}

	void appendStartsWithAfterFrom(final Statement bf, final int offset, final byte[] value) {}
	abstract Consumer<Statement> getStartsWithColumn();
}
