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

import static com.exedio.cope.util.Check.requireNonEmpty;
import static java.util.Collections.emptyMap;

import com.exedio.cope.util.JobContext;
import com.exedio.cope.vault.VaultProperties;
import com.exedio.cope.vault.VaultResilientService;
import gnu.trove.TLongHashSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

final class Connect
{
	final Instant date = Instant.now();
	private final double dateEpoch = ModelMetrics.toEpoch(date);
	private final RevisionsConnect revisions;
	final ConnectProperties properties;
	final Dialect dialect;

	final SortedSet<String> supportedDataHashAlgorithms;
	final boolean supportsRandom;
	final boolean supportsCheckConstraint;
	final boolean supportsNativeDate;
	final boolean supportsUniqueViolation;

	final ConnectionFactory connectionFactory;
	final ConnectionPool connectionPool;
	final Marshallers marshallers;
	final Executor executor;
	final Database database;
	private final Map<String, VaultConnect> vaults;
	final CacheStamp cacheStamp;
	final ItemCache itemCache;
	final QueryCache queryCache;
	final Cluster cluster;
	final ChangeListenerDispatcher changeListenerDispatcher;

	boolean revised = false;

	Connect(
			final ModelMetrics metrics,
			final Types types,
			final ChangeListeners changeListeners,
			final Revisions.Factory revisionsFactory,
			final ConnectProperties properties,
			final Transactions transactions)
	{
		this.properties = properties;

		final CopeProbe probe = new CopeProbe(properties, properties.probeEnvironmentInfo());

		this.revisions = RevisionsConnect.wrap(probe.environmentInfo(), revisionsFactory);
		this.dialect = properties.dialect.newInstance(probe);

		supportedDataHashAlgorithms = toUnmodifiableSortedSet(dialect.getBlobHashAlgorithms());
		supportsRandom = dialect.supportsRandom();
		// SchemaInfo
		supportsCheckConstraint = !properties.disableCheckConstraint && dialect.dsmfDialect.supportsCheckConstraint();
		supportsNativeDate = !properties.isSupportDisabledForNativeDate();
		supportsUniqueViolation = !properties.isSupportDisabledForUniqueViolation() && dialect.supportsUniqueViolation();

		this.connectionFactory = new ConnectionFactory(properties, probe.environmentInfo().sqlDriver, dialect);
		this.connectionPool = new ConnectionPool(metrics, connectionFactory, properties.connectionPool);
		this.marshallers = new Marshallers(dialect, supportsNativeDate);
		this.executor = new Executor(dialect, supportsUniqueViolation, properties, marshallers);
		{
			final HashMap<String, VaultMarkPut> vaultMarkPut = new HashMap<>();
			final VaultProperties props = properties.vault;
			final Function<String, BooleanSupplier> markPut = key ->
			{
				final VaultMarkPut result = new VaultMarkPut(metrics, key);
				if(vaultMarkPut.putIfAbsent(key, result)!=null)
					throw new RuntimeException(key);
				return result;
			};
			final Map<String, VaultResilientService> services = props!=null ? props.newServices(markPut) : emptyMap();
			final HashMap<String, VaultConnect> vaults = new HashMap<>();
			for(final Map.Entry<String, VaultResilientService> e : services.entrySet())
				vaults.put(e.getKey(), new VaultConnect(e.getKey(), props, properties.trimmerStandard, connectionPool, executor, e.getValue(), vaultMarkPut.get(e.getKey())));
			this.vaults = Collections.unmodifiableMap(vaults);
		}
		this.database = new Database(
				dialect.dsmfDialect,
				probe,
				dialect,
				connectionPool,
				executor,
				transactions,
				vaults.values(),
				revisions);

		this.cacheStamp = new CacheStamp(properties.cacheStamps);
		this.itemCache = new ItemCache(metrics, types, properties, cacheStamp);
		this.queryCache = new QueryCache(metrics, properties.getQueryCacheLimit(), properties.cacheStamps, cacheStamp);

		{
			final ClusterProperties props = properties.cluster;
			//noinspection ThisEscapedInObjectConstruction
			this.cluster = props!=null ? new Cluster(metrics, types, props, this) : null;
		}

		this.changeListenerDispatcher =
				new ChangeListenerDispatcher(metrics, types, changeListeners, properties);

		metrics.gaugeConnect(
				c -> c.dateEpoch,
				"connectTime", "When the model was connected as UNIX epoch.");
		metrics.gaugeConnect(
				ignored -> 1.0,
				"connect", "Describes the connect of the model to the database.",
				probe.tags());
	}

	private static SortedSet<String> toUnmodifiableSortedSet(final String[] array)
	{
		if(array==null || array.length==0)
			return Collections.emptySortedSet();

		return Collections.unmodifiableSortedSet(
				new TreeSet<>(Arrays.asList(array)));
	}

	VaultConnect vault(final String bucket)
	{
		final VaultConnect result =
				vaults.get(requireNonEmpty(bucket, "bucket"));
		if(result==null)
			throw new IllegalArgumentException(
					"bucket " + bucket + " does not exist, " +
					"use one of " + vaults.keySet());
		return result;
	}

	void close()
	{
		changeListenerDispatcher.startClose();
		if(cluster!=null)
			cluster.startClose();

		connectionPool.flush();

		final ArrayList<VaultConnect> reverseVaults = new ArrayList<>(vaults.values());
		Collections.reverse(reverseVaults);
		for(final VaultConnect vault : reverseVaults)
			vault.service.close();

		// let threads have some time to terminate,
		// doing other thing in the mean time
		changeListenerDispatcher.joinClose();
		if(cluster!=null)
			cluster.joinClose();
	}

	void invalidate(
			final TLongHashSet[] invalidations,
			final TransactionInfo transactionInfo)
	{
		itemCache.invalidate(invalidations);
		queryCache.invalidate(invalidations);
		if((transactionInfo instanceof TransactionInfoLocal) && cluster!=null)
			cluster.sendInvalidate(invalidations);
		changeListenerDispatcher.invalidate(invalidations, transactionInfo);
	}

	void createSchema()
	{
		database.createSchema();
		clearCache();
	}

	void deleteSchema(final boolean forTest)
	{
		database.deleteSchema(forTest && properties.deleteSchemaForTest);
		clearCache();
		database.flushSequences();
	}

	void dropSchema()
	{
		database.dropSchema();
		clearCache();
	}

	void tearDownSchema()
	{
		database.tearDownSchema();
		clearCache();
	}

	void clearCache()
	{
		itemCache.clear();
		queryCache.clear();
	}

	void purgeSchema(final JobContext ctx)
	{
		dialect.purgeSchema(ctx, database, connectionPool);

		for(final VaultConnect vault : vaults.values())
			vault.purgeSchema(ctx);
	}

	Revisions getRevisions()
	{
		return revisions!=null ? revisions.get() : null;
	}

	void revise(final boolean explicitRequest)
	{
		if(revised) // synchronization is done by Model#revise
			return;

		revisions.get().revise(
				properties,
				connectionFactory, connectionPool,
				executor, database.probe, dialect,
				explicitRequest);

		revised = true;
	}

	Map<Integer, byte[]> getRevisionLogs(final boolean withMutex)
	{
		return Revisions.getLogs(withMutex, properties, connectionPool, executor);
	}

	List<ThreadController> getThreadControllers()
	{
		final ArrayList<ThreadController> result = new ArrayList<>(2);
		changeListenerDispatcher.addThreadControllers(result);
		if(cluster!=null)
			cluster.addThreadControllers(result);
		return Collections.unmodifiableList(result);
	}

	String getSchemaSavepoint() throws SchemaSavepointNotAvailableException, SQLException
	{
		return dialect.getSchemaSavepoint(connectionPool);
	}

	/**
	 * For junit tests only
	 */
	Set<String> vaultBuckets()
	{
		return vaults.keySet();
	}
}
