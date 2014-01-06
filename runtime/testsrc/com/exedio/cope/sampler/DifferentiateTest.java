/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.sampler;

import static com.exedio.cope.sampler.Stuff.sampler;
import static com.exedio.cope.sampler.Stuff.samplerModel;

import com.exedio.cope.Query;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.junit.CopeAssert;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class DifferentiateTest extends ConnectedTest
{
	public void testIt()
	{
		final Date from  = new Date(System.currentTimeMillis()-1);
		final Date until = new Date(System.currentTimeMillis()+50000);
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		final String fromString = dateFormat.format(from);
		final String untilString = dateFormat.format(until);

		samplerModel.createSchema();
		sampler.checkInternal();
		samplerModel.startTransaction("HistoryTest");
		assertEquals(0, SamplerModel.TYPE.search().size());
		assertEquals(0, SamplerItemCache.TYPE.search().size());
		assertEquals(0, SamplerMedia.TYPE.search().size());
		samplerModel.commit();

		sampler.sampleInternal();
		waitForSystemTimeChange();
		sampler.sampleInternal();
		waitForSystemTimeChange();
		sampler.sampleInternal();

		final List<Query<List<Object>>> queries = sampler.differentiate(from, until);
		final Query<List<Object>> modelQuery   = queries.get(0);
		final Query<List<Object>> itemQuery    = queries.get(1);
		final Query<List<Object>> clusterQuery = queries.get(2);
		final Query<List<Object>> mediaQuery   = queries.get(3);
		assertEquals(4, queries.size());
		assertUnmodifiable(queries);
		assertEquals(
				"select date,a1.date," +
					"minus(a1.connectionPoolGet,connectionPoolGet)," +
					"minus(a1.connectionPoolPut,connectionPoolPut)," +
					"minus(a1.connectionPoolInvalidOnGet,connectionPoolInvalidOnGet)," +
					"minus(a1.connectionPoolInvalidOnPut,connectionPoolInvalidOnPut)," +
					"minus(a1.nextTransactionId,nextTransactionId)," +
					"minus(a1.commitWithoutConnection,commitWithoutConnection)," +
					"minus(a1.commitWithConnection,commitWithConnection)," +
					"minus(a1.rollbackWithoutConnection,rollbackWithoutConnection)," +
					"minus(a1.rollbackWithConnection,rollbackWithConnection)," +
					"minus(a1.itemCacheHits,itemCacheHits)," +
					"minus(a1.itemCacheMisses,itemCacheMisses)," +
					"minus(a1.itemCacheConcurrentLoads,itemCacheConcurrentLoads)," +
					"minus(a1.itemCacheReplacementRuns,itemCacheReplacementRuns)," +
					"minus(a1.itemCacheReplacements,itemCacheReplacements)," +
					"minus(a1.itemCacheInvalidationsOrdered,itemCacheInvalidationsOrdered)," +
					"minus(a1.itemCacheInvalidationsDone,itemCacheInvalidationsDone)," +
					"minus(a1.itemCacheInvalidateLastHits,itemCacheInvalidateLastHits)," +
					"minus(a1.itemCacheInvalidateLastPurged,itemCacheInvalidateLastPurged)," +
					"minus(a1.queryCacheHits,queryCacheHits)," +
					"minus(a1.queryCacheMisses,queryCacheMisses)," +
					"minus(a1.queryCacheReplacements,queryCacheReplacements)," +
					"minus(a1.queryCacheInvalidations,queryCacheInvalidations)," +
					"minus(a1.changeListenerCleared,changeListenerCleared)," +
					"minus(a1.changeListenerRemoved,changeListenerRemoved)," +
					"minus(a1.changeListenerFailed,changeListenerFailed)," +
					"minus(a1.changeListenerOverflow,changeListenerOverflow)," +
					"minus(a1.changeListenerException,changeListenerException)," +
					"minus(a1.mediasNoSuchPath,mediasNoSuchPath)," +
					"minus(a1.mediasRedirectFrom,mediasRedirectFrom)," +
					"minus(a1.mediasException,mediasException)," +
					"minus(a1.mediasGuessedUrl,mediasGuessedUrl)," +
					"minus(a1.mediasNotAnItem,mediasNotAnItem)," +
					"minus(a1.mediasNoSuchItem,mediasNoSuchItem)," +
					"minus(a1.mediasMoved,mediasMoved)," +
					"minus(a1.mediasIsNull,mediasIsNull)," +
					"minus(a1.mediasNotComputable,mediasNotComputable)," +
					"minus(a1.mediasNotModified,mediasNotModified)," +
					"minus(a1.mediasDelivered,mediasDelivered)," +
					"minus(a1.clusterSenderInvalidationSplit,clusterSenderInvalidationSplit)," +
					"minus(a1.clusterListener-exception,clusterListener-exception)," +
					"minus(a1.clusterListener-missingMagic,clusterListener-missingMagic)," +
					"minus(a1.clusterListener-wrongSecret,clusterListener-wrongSecret)," +
					"minus(a1.clusterListener-fromMyself,clusterListener-fromMyself) " +
				"from AbsoluteModel join AbsoluteModel a1 " +
				"where (date>='" + fromString + "' AND date<='" + untilString + "' " +
					"AND a1.connectDate=connectDate " +
					"AND a1.sampler=sampler " +
					"AND a1.running=(running+1)) " +
				"order by this", modelQuery.toString());
		assertEquals(
				"select type,date,a1.date," +
					"minus(a1.hits,hits)," +
					"minus(a1.misses,misses)," +
					"minus(a1.concurrentLoads,concurrentLoads)," +
					"minus(a1.replacementRuns,replacementRuns)," +
					"minus(a1.replacements,replacements)," +
					"minus(a1.invalidationsOrdered,invalidationsOrdered)," +
					"minus(a1.invalidationsDone,invalidationsDone)," +
					"minus(a1.invalidateLastHits,invalidateLastHits)," +
					"minus(a1.invalidateLastPurged,invalidateLastPurged) " +
				"from AbsoluteItemCache join AbsoluteItemCache a1 " +
				"where (date>='" + fromString + "' AND date<='" + untilString + "' " +
					"AND a1.type=type " +
					"AND a1.connectDate=connectDate " +
					"AND a1.sampler=sampler " +
					"AND a1.running=(running+1)) " +
				"order by this",
			itemQuery.toString());
		assertEquals(
				"select id,date,a1.date," +
					"minus(a1.invalidate-inOrder,invalidate-inOrder)," +
					"minus(a1.invalidate-outOfOrder,invalidate-outOfOrder)," +
					"minus(a1.invalidate-duplicate,invalidate-duplicate)," +
					"minus(a1.invalidate-lost,invalidate-lost)," +
					"minus(a1.invalidate-late,invalidate-late)," +
					"minus(a1.invalidate-pending,invalidate-pending)," +
					"minus(a1.ping-inOrder,ping-inOrder)," +
					"minus(a1.ping-outOfOrder,ping-outOfOrder)," +
					"minus(a1.ping-duplicate,ping-duplicate)," +
					"minus(a1.ping-lost,ping-lost)," +
					"minus(a1.ping-late,ping-late)," +
					"minus(a1.ping-pending,ping-pending)," +
					"minus(a1.pong-inOrder,pong-inOrder)," +
					"minus(a1.pong-outOfOrder,pong-outOfOrder)," +
					"minus(a1.pong-duplicate,pong-duplicate)," +
					"minus(a1.pong-lost,pong-lost)," +
					"minus(a1.pong-late,pong-late)," +
					"minus(a1.pong-pending,pong-pending) " +
				"from AbsoluteClusterNode join AbsoluteClusterNode a1 " +
				"where (date>='" + fromString + "' AND date<='" + untilString + "' " +
					"AND a1.id=id " +
					"AND a1.connectDate=connectDate " +
					"AND a1.sampler=sampler " +
					"AND a1.running=(running+1)) " +
				"order by this",
			clusterQuery.toString());
		assertEquals(
				"select media,date,a1.date," +
					"minus(a1.redirectFrom,redirectFrom)," +
					"minus(a1.exception,exception)," +
					"minus(a1.guessedUrl,guessedUrl)," +
					"minus(a1.notAnItem,notAnItem)," +
					"minus(a1.noSuchItem,noSuchItem)," +
					"minus(a1.moved,moved)," +
					"minus(a1.isNull,isNull)," +
					"minus(a1.notComputable,notComputable)," +
					"minus(a1.notModified,notModified)," +
					"minus(a1.delivered,delivered) " +
				"from AbsoluteMedia join AbsoluteMedia a1 " +
				"where (date>='" + fromString + "' AND date<='" + untilString + "' " +
					"AND a1.media=media " +
					"AND a1.connectDate=connectDate " +
					"AND a1.sampler=sampler " +
					"AND a1.running=(running+1)) " +
				"order by this",
			mediaQuery.toString());
		assertQuery(modelQuery);
		assertQuery(itemQuery);
		assertQuery(clusterQuery);
		assertQuery(mediaQuery);

		samplerModel.startTransaction("SampleTest#differentiate");
		{
			final Iterator<List<Object>> models = modelQuery.search().iterator();
			assertFalse(models.hasNext());
		}
		{
			final Iterator<List<Object>> items = itemQuery.search().iterator();
			assertFalse(items.hasNext());
		}
		{
			final Iterator<List<Object>> clusters = clusterQuery.search().iterator();
			assertFalse(clusters.hasNext());
		}
		{
			final Iterator<List<Object>> medias = mediaQuery.search().iterator();
			assertFalse(medias.hasNext());
		}

		samplerModel.commit();

	}

	private static void assertQuery(final Query<?> query)
	{
		final String search = SchemaInfo.search(query);
		assertTrue(search, search.startsWith("SELECT "));
		final String total = SchemaInfo.total(query);
		assertTrue(total, total.startsWith("SELECT COUNT(*) FROM "));
	}

	/**
	 * Wait for new Date() to return a different value to avoid unique violation on SamplerModel.date. Especially useful for Windows systems which have
	 * a low system time resolution.
	 * @see Sampler#sample()
	 */
	private static void waitForSystemTimeChange()
	{
		try
		{
			CopeAssert.sleepLongerThan(1);
		}
		catch (final InterruptedException e)
		{
			fail("Correctness of following code is not asserted.");
		}
	}
}
