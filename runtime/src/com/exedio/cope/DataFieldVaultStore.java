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
import com.exedio.cope.vault.VaultResilientService;
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
	private final String bucket;
	private final VaultResilientService service;
	private final VaultTrail trail;
	private final Dialect dialect;
	private final Counter getBytes, getStream, putInitial, putRedundant, putInitialSize, putRedundantSize;

	DataFieldVaultStore(
			final DataField field,
			final Table table,
			final String name,
			final boolean optional,
			final VaultProperties properties,
			final Connect connect,
			final ModelMetrics metricsTemplate)
	{
		super(field);
		final int length = properties.getAlgorithmLength();
		this.column = new StringColumn(
				table, name, optional, length, length,
				CharSet.HEX_LOWER, null,
				mysqlExtendedVarchar);
		this.algorithm = properties.getAlgorithmFactory();
		this.algorithmName = algorithm.getAlgorithm();
		final String bucketExplicit = field.getAnnotatedVaultValue();
		this.bucket = bucketExplicit!=null ? bucketExplicit : Vault.DEFAULT;
		this.service = requireNonNull(connect.vaults.get(bucket));
		this.trail = requireNonNull(connect.database.vaultTrails.get(bucket));
		this.dialect = connect.dialect;

		final Metrics metrics = new Metrics(metricsTemplate, field, bucket);
		getBytes  = metrics.counter("get", "sink", "bytes");
		getStream = metrics.counter("get", "sink", "stream");
		putInitial   = metrics.counter("put", "result", "initial");
		putRedundant = metrics.counter("put", "result", "redundant");
		putInitialSize   = metrics.counter("putSize", "result", "initial");
		putRedundantSize = metrics.counter("putSize", "result", "redundant");
	}

	private static final class Metrics
	{
		final ModelMetrics back;

		Metrics(final ModelMetrics metricsTemplate, final DataField field, final String bucket)
		{
			this.back = metricsTemplate.name(DataField.class).
					tag(field).
					tag("bucket", bucket);
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

		return trail.getLength(hash);
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
		put(hash -> entity.put(column, hash), data, exceptionItem);
	}

	private void put(
			final Consumer<String> entityPutter, Value data,
			final Item exceptionItem)
	{
		if(data==null)
		{
			entityPutter.accept(null);
			return;
		}

		final MessageDigest messageDigest = algorithm.newInstance();
		final DataConsumer consumer = trail.newDataConsumer();
		try
		{
			data = data.update(messageDigest, consumer, field,
					exceptionItem); // BEWARE: is not the same as entity.getItem()
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}

		final String hash = Hex.encodeLower(messageDigest.digest());
		entityPutter.accept(hash);

		final boolean result;
		try
		{
			result = data.put(service, hash);
		}
		catch(final IOException e)
		{
			throw new RuntimeException(field.toString(), e);
		}
		(result ? putInitial : putRedundant).increment();
		(result ? putInitialSize : putRedundantSize).increment(consumer.length());
		trail.put(dialect, hash, consumer, field);
	}

	static final VaultPutInfo FAILURE_INFO = new VaultPutInfo()
	{
		@Override
		public DataField getField()
		{
			throw new RuntimeException();
		}
		@Override
		public String getFieldString()
		{
			throw new RuntimeException();
		}
		@Override
		public Item getItem()
		{
			throw new RuntimeException();
		}
		@Override
		public String getItemString()
		{
			throw new RuntimeException();
		}
		@Override
		public String getOrigin()
		{
			throw new RuntimeException();
		}
		@Override
		public String toString()
		{
			throw new RuntimeException();
		}
	};

	@Override
	String getBucket()
	{
		return bucket;
	}

	@Override
	Statement checkVaultTrail(final Statement.Mode mode)
	{
		return trail.check(field, mode);
	}

	@Override
	DataFieldVaultInfo getVaultInfo()
	{
		return new DataFieldVaultInfo(
				field, bucket, service,
				getBytes, getStream,
				putInitial, putRedundant);
	}

	@Override
	void appendStartsWithAfterFrom(final Statement bf, final int offset, final byte[] value)
	{
		final int required = offset + value.length;
		if(required>trail.startLimit)
			throw new UnsupportedQueryException(
					"DataField " + field + " does not support startsWith as it has vault enabled, " +
					"trail supports up to " + trail.startLimit + " bytes only but " +
					"condition requires " + required + " bytes" +
					(offset>0 ? " (offset " + offset + " plus value " + value.length + ')' : ""));

		bf.joinVaultTrailIfAbsent(field, trail);
	}

	@Override
	Consumer<Statement> getStartsWithColumn()
	{
		return bf -> bf.
				append(bf.getJoinVaultTrailAlias(field)).
				append('.').
				append(trail.startQuoted);
	}
}
