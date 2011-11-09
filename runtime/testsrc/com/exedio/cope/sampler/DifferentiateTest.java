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

package com.exedio.cope.sampler;

import static com.exedio.cope.sampler.Stuff.sampler;
import static com.exedio.cope.sampler.Stuff.samplerModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.Query;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.junit.CopeAssert;

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

		final SamplerModel model1 = sampler.sampleInternal();
		waitForSystemTimeChange();
		final SamplerModel model2 = sampler.sampleInternal();
		waitForSystemTimeChange();
		final SamplerModel model3 = sampler.sampleInternal();

		final List<Query<List<Object>>> queries = sampler.differentiate(from, until);
		final Query<List<Object>> modelQuery   = queries.get(0);
		final Query<List<Object>> itemQuery    = queries.get(1);
		final Query<List<Object>> clusterQuery = queries.get(2);
		final Query<List<Object>> mediaQuery   = queries.get(3);
		assertEquals(4, queries.size());
		assertUnmodifiable(queries);
		assertEquals(
				"select date,s1.date," +
					"minus(s1.connectionPoolGet,connectionPoolGet)," +
					"minus(s1.connectionPoolPut,connectionPoolPut)," +
					"minus(s1.connectionPoolInvalidOnGet,connectionPoolInvalidOnGet)," +
					"minus(s1.connectionPoolInvalidOnPut,connectionPoolInvalidOnPut)," +
					"minus(s1.nextTransactionId,nextTransactionId)," +
					"minus(s1.commitWithoutConnection,commitWithoutConnection)," +
					"minus(s1.commitWithConnection,commitWithConnection)," +
					"minus(s1.rollbackWithoutConnection,rollbackWithoutConnection)," +
					"minus(s1.rollbackWithConnection,rollbackWithConnection)," +
					"minus(s1.itemCacheHits,itemCacheHits)," +
					"minus(s1.itemCacheMisses,itemCacheMisses)," +
					"minus(s1.itemCacheConcurrentLoads,itemCacheConcurrentLoads)," +
					"minus(s1.itemCacheReplacementRuns,itemCacheReplacementRuns)," +
					"minus(s1.itemCacheReplacements,itemCacheReplacements)," +
					"minus(s1.itemCacheInvalidationsOrdered,itemCacheInvalidationsOrdered)," +
					"minus(s1.itemCacheInvalidationsDone,itemCacheInvalidationsDone)," +
					"minus(s1.itemCacheInvalidateLastHits,itemCacheInvalidateLastHits)," +
					"minus(s1.itemCacheInvalidateLastPurged,itemCacheInvalidateLastPurged)," +
					"minus(s1.queryCacheHits,queryCacheHits)," +
					"minus(s1.queryCacheMisses,queryCacheMisses)," +
					"minus(s1.queryCacheReplacements,queryCacheReplacements)," +
					"minus(s1.queryCacheInvalidations,queryCacheInvalidations)," +
					"minus(s1.changeListenerCleared,changeListenerCleared)," +
					"minus(s1.changeListenerRemoved,changeListenerRemoved)," +
					"minus(s1.changeListenerFailed,changeListenerFailed)," +
					"minus(s1.changeListenerOverflow,changeListenerOverflow)," +
					"minus(s1.changeListenerException,changeListenerException)," +
					"minus(s1.mediasNoSuchPath,mediasNoSuchPath)," +
					"minus(s1.mediasRedirectFrom,mediasRedirectFrom)," +
					"minus(s1.mediasException,mediasException)," +
					"minus(s1.mediasGuessedUrl,mediasGuessedUrl)," +
					"minus(s1.mediasNotAnItem,mediasNotAnItem)," +
					"minus(s1.mediasNoSuchItem,mediasNoSuchItem)," +
					"minus(s1.mediasMoved,mediasMoved)," +
					"minus(s1.mediasIsNull,mediasIsNull)," +
					"minus(s1.mediasNotComputable,mediasNotComputable)," +
					"minus(s1.mediasNotModified,mediasNotModified)," +
					"minus(s1.mediasDelivered,mediasDelivered)," +
					"minus(s1.clusterSenderInvalidationSplit,clusterSenderInvalidationSplit)," +
					"minus(s1.clusterListener-exception,clusterListener-exception)," +
					"minus(s1.clusterListener-missingMagic,clusterListener-missingMagic)," +
					"minus(s1.clusterListener-wrongSecret,clusterListener-wrongSecret)," +
					"minus(s1.clusterListener-fromMyself,clusterListener-fromMyself) " +
				"from SamplerModel join SamplerModel s1 " +
				"where (date>='" + fromString + "' AND date<='" + untilString + "' " +
					"AND s1.connectDate=connectDate " +
					"AND s1.sampler=sampler " +
					"AND s1.running=(running+1)) " +
				"order by this", modelQuery.toString());
		assertEquals(
				"select type,date,s1.date," +
					"minus(s1.hits,hits)," +
					"minus(s1.misses,misses)," +
					"minus(s1.concurrentLoads,concurrentLoads)," +
					"minus(s1.replacementRuns,replacementRuns)," +
					"minus(s1.replacements,replacements)," +
					"minus(s1.invalidationsOrdered,invalidationsOrdered)," +
					"minus(s1.invalidationsDone,invalidationsDone)," +
					"minus(s1.invalidateLastHits,invalidateLastHits)," +
					"minus(s1.invalidateLastPurged,invalidateLastPurged) " +
				"from SamplerItemCache join SamplerItemCache s1 " +
				"where (date>='" + fromString + "' AND date<='" + untilString + "' " +
					"AND s1.type=type " +
					"AND s1.connectDate=connectDate " +
					"AND s1.sampler=sampler " +
					"AND s1.running=(running+1)) " +
				"order by this",
			itemQuery.toString());
		assertEquals(
				"select id,date,s1.date," +
					"minus(s1.invalidate-inOrder,invalidate-inOrder)," +
					"minus(s1.invalidate-outOfOrder,invalidate-outOfOrder)," +
					"minus(s1.invalidate-duplicate,invalidate-duplicate)," +
					"minus(s1.invalidate-lost,invalidate-lost)," +
					"minus(s1.invalidate-late,invalidate-late)," +
					"minus(s1.invalidate-pending,invalidate-pending)," +
					"minus(s1.ping-inOrder,ping-inOrder)," +
					"minus(s1.ping-outOfOrder,ping-outOfOrder)," +
					"minus(s1.ping-duplicate,ping-duplicate)," +
					"minus(s1.ping-lost,ping-lost)," +
					"minus(s1.ping-late,ping-late)," +
					"minus(s1.ping-pending,ping-pending)," +
					"minus(s1.pong-inOrder,pong-inOrder)," +
					"minus(s1.pong-outOfOrder,pong-outOfOrder)," +
					"minus(s1.pong-duplicate,pong-duplicate)," +
					"minus(s1.pong-lost,pong-lost)," +
					"minus(s1.pong-late,pong-late)," +
					"minus(s1.pong-pending,pong-pending) " +
				"from SamplerClusterNode join SamplerClusterNode s1 " +
				"where (date>='" + fromString + "' AND date<='" + untilString + "' " +
					"AND s1.id=id " +
					"AND s1.connectDate=connectDate " +
					"AND s1.sampler=sampler " +
					"AND s1.running=(running+1)) " +
				"order by this",
			clusterQuery.toString());
		assertEquals(
				"select media,date,s1.date," +
					"minus(s1.redirectFrom,redirectFrom)," +
					"minus(s1.exception,exception)," +
					"minus(s1.guessedUrl,guessedUrl)," +
					"minus(s1.notAnItem,notAnItem)," +
					"minus(s1.noSuchItem,noSuchItem)," +
					"minus(s1.moved,moved)," +
					"minus(s1.isNull,isNull)," +
					"minus(s1.notComputable,notComputable)," +
					"minus(s1.notModified,notModified)," +
					"minus(s1.delivered,delivered) " +
				"from SamplerMedia join SamplerMedia s1 " +
				"where (date>='" + fromString + "' AND date<='" + untilString + "' " +
					"AND s1.media=media " +
					"AND s1.connectDate=connectDate " +
					"AND s1.sampler=sampler " +
					"AND s1.running=(running+1)) " +
				"order by this",
			mediaQuery.toString());
		assertQuery(modelQuery);
		assertQuery(itemQuery);
		assertQuery(clusterQuery);
		assertQuery(mediaQuery);

		samplerModel.startTransaction("SampleTest#differentiate");
		{
			final Iterator<List<Object>> models = modelQuery.search().iterator();
			assertEquals(list(
					SamplerModel.date.get(model1),
					SamplerModel.date.get(model2)),
				models.next().subList(0, 2));
			assertEquals(list(
					SamplerModel.date.get(model2),
					SamplerModel.date.get(model3)),
				models.next().subList(0, 2));
			assertFalse(models.hasNext());
		}
		{
			final Iterator<List<Object>> items = itemQuery.search().iterator();
			if(c)
			{
				assertEquals(list(
						"SampledModelItem",
						SamplerModel.date.get(model1),
						SamplerModel.date.get(model2)),
					items.next().subList(0, 3));
				assertEquals(list(
						"SampledModelItem2",
						SamplerModel.date.get(model1),
						SamplerModel.date.get(model2)),
					items.next().subList(0, 3));
				assertEquals(list(
						"SampledModelItem",
						SamplerModel.date.get(model2),
						SamplerModel.date.get(model3)),
					items.next().subList(0, 3));
				assertEquals(list(
						"SampledModelItem2",
						SamplerModel.date.get(model2),
						SamplerModel.date.get(model3)),
					items.next().subList(0, 3));
			}
			assertFalse(items.hasNext());
		}
		{
			final Iterator<List<Object>> clusters = clusterQuery.search().iterator();
			assertFalse(clusters.hasNext());
		}
		{
			final Iterator<List<Object>> medias = mediaQuery.search().iterator();
			assertEquals(list(
					"SampledModelItem.mediaA",
					SamplerModel.date.get(model1),
					SamplerModel.date.get(model2)),
				medias.next().subList(0, 3));
			assertEquals(list(
					"SampledModelItem.mediaB",
					SamplerModel.date.get(model1),
					SamplerModel.date.get(model2)),
				medias.next().subList(0, 3));
			assertEquals(list(
					"SampledModelItem.mediaA",
					SamplerModel.date.get(model2),
					SamplerModel.date.get(model3)),
				medias.next().subList(0, 3));
			assertEquals(list(
					"SampledModelItem.mediaB",
					SamplerModel.date.get(model2),
					SamplerModel.date.get(model3)),
				medias.next().subList(0, 3));
			assertFalse(medias.hasNext());
		}

		samplerModel.commit();

	}

	private void assertQuery(final Query query)
	{
		final String search = SchemaInfo.search(query);
		assertTrue(search, search.startsWith("select "));
		final String total = SchemaInfo.total(query);
		assertTrue(total, total.startsWith("select count(*) from "));
	}

	/**
	 * Wait for new Date() to return a different value to avoid unique violation on SamplerModel.date. Especially useful for Windows systems which have
	 * a low system time resolution.
	 * @see Sampler#sample()
	 */
	private void waitForSystemTimeChange()
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
