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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.DataField.Value;
import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.MessageDigestFactory;
import com.exedio.cope.vault.VaultNotFoundException;
import com.exedio.cope.vault.VaultProperties;
import com.exedio.cope.vault.VaultPutInfo;
import com.exedio.cope.vault.VaultService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.security.MessageDigest;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

final class DataFieldVaultStore extends DataFieldStore
{
	private final StringColumn column;
	private final MessageDigestFactory algorithm;
	private final String algorithmName;
	private final String serviceKey;
	private final VaultService service;
	private final VaultTrail trail;
	private final Counter getLength, getBytes, getStream, putInitial, putRedundant, putInitialSize, putRedundantSize;

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
		this.algorithmName = algorithm.getAlgorithm();
		final String serviceKeyExplicit = field.getAnnotatedVaultValue();
		this.serviceKey = serviceKeyExplicit!=null ? serviceKeyExplicit : Vault.DEFAULT;
		this.service = requireNonNull(connect.vaults.get(serviceKey));
		this.trail = connect.database.vaultTrails.get(serviceKey);

		final Metrics metrics = new Metrics(field, serviceKey);
		getLength = metrics.counter("getLength");
		getBytes  = metrics.counter("get", "sink", "bytes");
		getStream = metrics.counter("get", "sink", "stream");
		putInitial   = metrics.counter("put", "result", "initial");
		putRedundant = metrics.counter("put", "result", "redundant");
		putInitialSize   = metrics.counter("putSize", "result", "initial");
		putRedundantSize = metrics.counter("putSize", "result", "redundant");
	}

	private static final class Metrics
	{
		final MetricsBuilder back;

		Metrics(final DataField field, final String service)
		{
			this.back = new MetricsBuilder(DataField.class, Tags.of(
					"feature", field.getID(),
					"service", service));
		}

		Counter counter(
				final String methodName)
		{
			return counter(methodName, Tags.empty());
		}

		Counter counter(
				final String methodName,
				final String key,
				final String value)
		{
			return counter(methodName, Tags.of(key, value));
		}

		Counter counter(
				final String methodName,
				final Tags tags)
		{
			return back.counter(
					"vault." + methodName,
					"VaultService#" + methodName + " calls",
					tags);
		}
	}

	static final MysqlExtendedVarchar mysqlExtendedVarchar = new MysqlExtendedVarchar() { @Override public Class<? extends Annotation> annotationType() { return MysqlExtendedVarchar.class; } };

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
	void appendHashExpression(final Statement bf, final String algorithm)
	{
		if(!algorithm.equals(algorithmName))
			throw new UnsupportedQueryException(
					"DataField " + field + " supports hashMatches with algorithm >" + algorithmName + "< only, " +
					"but not >" + algorithm + "< as it has vault enabled");

		bf.append(column);
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

		getLength.increment();
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
	byte[] load(final Transaction tx, final Item item)
	{
		final String hash = getHash(tx, item);
		if(hash==null)
			return null;

		getBytes.increment();
		try
		{
			return service.get(hash);
		}
		catch(final VaultNotFoundException e)
		{
			throw wrap(e, item);
		}
	}

	@Override
	void load(final Transaction tx, final Item item, final OutputStream sink)
	{
		final String hash = getHash(tx, item);
		if(hash==null)
			return;

		getStream.increment();
		try
		{
			service.get(hash, sink);
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

	@Override
	String getHash(final Transaction tx, final Item item)
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
		final LengthConsumer length = trail!=null ? trail.newDataConsumer() : new LengthConsumer(0);
		try
		{
			data = data.update(messageDigest, length, field,
					exceptionItem); // BEWARE: is not the same as entity.getItem()
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}

		final String hash = Hex.encodeLower(messageDigest.digest());
		entityPutter.accept(hash);

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
		(result ? putInitial : putRedundant).increment();
		(result ? putInitialSize : putRedundantSize).increment(length.value());
		if(trail!=null)
			trail.put(hash, length, info, result);
	}

	private static final String ORIGIN = VaultPutInfo.getOriginDefault();

	@Override
	String getVaultServiceKey()
	{
		return serviceKey;
	}

	@Override
	long checkVaultTrail()
	{
		if(trail==null)
			throw new IllegalStateException("trail is disabled");

		return trail.check(field);
	}

	@Override
	DataFieldVaultInfo getVaultInfo()
	{
		return new DataFieldVaultInfo(
				field, serviceKey, service,
				getLength, getBytes, getStream,
				putInitial, putRedundant);
	}
}
