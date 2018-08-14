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
import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.MessageDigestFactory;
import com.exedio.cope.vault.VaultNotFoundException;
import com.exedio.cope.vault.VaultProperties;
import com.exedio.cope.vault.VaultPutInfo;
import com.exedio.cope.vault.VaultService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.security.MessageDigest;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

final class DataFieldVaultStore extends DataFieldStore
{
	private final StringColumn column;
	private final MessageDigestFactory algorithm;
	private final String hashForEmpty;
	private final VaultService service;

	DataFieldVaultStore(
			final DataField field,
			final Table table,
			final String name,
			final boolean optional,
			final VaultProperties properties,
			final Connect connect)
	{
		super(field);
		final int length = properties.getAlgorithmLength();
		this.column = new StringColumn(
				table, name, optional, length, length,
				CharSet.HEX_LOWER,
				mysqlExtendedVarchar);
		this.algorithm = properties.getAlgorithmFactory();
		this.hashForEmpty = properties.getAlgorithmDigestForEmptyByteSequence();
		this.service = connect.vault;
	}

	private static final MysqlExtendedVarchar mysqlExtendedVarchar = new MysqlExtendedVarchar() { @Override public Class<? extends Annotation> annotationType() { return MysqlExtendedVarchar.class; } };

	@Override
	Column column()
	{
		return column;
	}

	@Override
	BlobColumn blobColumnIfSupported(final String capability)
	{
		throw new UnsupportedQueryException(
				"DataField " + field + " does not support " + capability + " as it has vault enabled");
	}


	@Override
	boolean isNull(final Transaction tx, final Item item)
	{
		return getHash(tx, item)==null;
	}

	@Override
	long loadLength(final Transaction tx, final Item item)
	{
		final String hash = getHash(tx, item);
		if(hash==null)
			return -1;

		if(hashForEmpty.equals(hash))
			return 0;

		getLength.incrementAndGet();
		try
		{
			return service.getLength(hash);
		}
		catch(final VaultNotFoundException e)
		{
			throw wrap(e, item);
		}
	}

	@Override
	@SuppressFBWarnings("PZLA_PREFER_ZERO_LENGTH_ARRAYS")
	byte[] load(final Transaction tx, final Item item)
	{
		final String hash = getHash(tx, item);
		if(hash==null)
			return null;

		if(hashForEmpty.equals(hash))
			return EMPTY_BYTES;

		getBytes.incrementAndGet();
		try
		{
			return service.get(hash);
		}
		catch(final VaultNotFoundException e)
		{
			throw wrap(e, item);
		}
	}

	private static final byte[] EMPTY_BYTES = {};

	@Override
	void load(final Transaction tx, final Item item, final OutputStream data)
	{
		final String hash = getHash(tx, item);
		if(hash==null)
			return;

		if(hashForEmpty.equals(hash))
			return;

		getStream.incrementAndGet();
		try
		{
			service.get(hash, data);
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
		catch(final VaultNotFoundException e)
		{
			throw wrap(e, item);
		}
	}

	private String getHash(final Transaction tx, final Item item)
	{
		return tx.getEntity(item, true).get(column);
	}

	private IllegalStateException wrap(
			@Nonnull final VaultNotFoundException e,
			final Item item)
	{
		return new IllegalStateException(
				"vault data missing on " + item.getCopeID() + " for " + field + ", " +
				"service: " + service + ", " +
				"hash(" + algorithm + "): " + e.getHashAnonymous(),
				e);
	}


	@Override
	void store(final Transaction tx, final Item item, final Value data)
	{
		put(hash ->
		{
			final Entity entity = tx.getEntity(item, true);
			entity.put(column, hash);
			entity.write(null);
		},
		data, item, item);
	}

	@Override
	void put(final Entity entity, final Value data, final Item exceptionItem)
	{
		put(hash -> entity.put(column, hash), data, exceptionItem, entity.getItem());
	}

	private void put(
			final Consumer<String> entityPutter, Value data,
			final Item exceptionItem,
			final Item infoItem)
	{
		if(data==null)
		{
			entityPutter.accept(null);
			return;
		}

		final MessageDigest messageDigest = algorithm.newInstance();
		try
		{
			data = data.update(messageDigest, field,
					exceptionItem); // BEWARE: is not the same as entity.getItem()
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}

		final String hash = Hex.encodeLower(messageDigest.digest());
		entityPutter.accept(hash);

		if(hashForEmpty.equals(hash))
			return;

		final VaultPutInfo info = new VaultPutInfo()
		{
			@Override
			public DataField getField()
			{
				return field;
			}
			@Override
			public Item getItem()
			{
				return infoItem;
			}
			@Override
			public String getOrigin()
			{
				return ORIGIN; // do not compute again and again
			}
			@Override
			public String toString()
			{
				return getFieldString() + ' ' + getItemString();
			}
		};

		final boolean result;
		try
		{
			result = data.put(service, hash, info);
		}
		catch(final IOException e)
		{
			throw new RuntimeException(field.toString(), e);
		}
		(result ? putInitial : putRedundant).incrementAndGet();
	}

	private static final String ORIGIN = VaultPutInfo.getOriginDefault();


	private final AtomicLong
			getLength = new AtomicLong(),
			getBytes = new AtomicLong(),
			getStream = new AtomicLong(),
			putInitial = new AtomicLong(),
			putRedundant = new AtomicLong();

	@Override
	DataFieldVaultInfo getVaultInfo()
	{
		return new DataFieldVaultInfo(
				field, service,
				getLength, getBytes, getStream,
				putInitial, putRedundant);
	}
}
