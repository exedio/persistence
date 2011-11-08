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

import java.util.Iterator;
import java.util.List;

import com.exedio.cope.Query;
import com.exedio.cope.junit.CopeAssert;

public class DifferentiateTest extends ConnectedTest
{
	public void testIt()
	{
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

		final Query<List<Object>> modelQuery = Differentiate.makeQuery(SamplerModel.TYPE);
		final Query<List<Object>> itemQuery = Differentiate.makeQuery(SamplerItemCache.TYPE);
		final Query<List<Object>> clusterQuery = Differentiate.makeQuery(SamplerClusterNode.TYPE);
		final Query<List<Object>> mediaQuery = Differentiate.makeQuery(SamplerMedia.TYPE);
		assertEquals(
				"select date,s1.date," +
					"plus(s1.connectionPoolGet,connectionPoolGet)," +
					"plus(s1.connectionPoolPut,connectionPoolPut)," +
					"plus(s1.connectionPoolInvalidOnGet,connectionPoolInvalidOnGet)," +
					"plus(s1.connectionPoolInvalidOnPut,connectionPoolInvalidOnPut)," +
					"plus(s1.nextTransactionId,nextTransactionId)," +
					"plus(s1.commitWithoutConnection,commitWithoutConnection)," +
					"plus(s1.commitWithConnection,commitWithConnection)," +
					"plus(s1.rollbackWithoutConnection,rollbackWithoutConnection)," +
					"plus(s1.rollbackWithConnection,rollbackWithConnection)," +
					"plus(s1.itemCacheHits,itemCacheHits)," +
					"plus(s1.itemCacheMisses,itemCacheMisses)," +
					"plus(s1.itemCacheConcurrentLoads,itemCacheConcurrentLoads)," +
					"plus(s1.itemCacheReplacementRuns,itemCacheReplacementRuns)," +
					"plus(s1.itemCacheReplacements,itemCacheReplacements)," +
					"plus(s1.itemCacheInvalidationsOrdered,itemCacheInvalidationsOrdered)," +
					"plus(s1.itemCacheInvalidationsDone,itemCacheInvalidationsDone)," +
					"plus(s1.itemCacheInvalidateLastHits,itemCacheInvalidateLastHits)," +
					"plus(s1.itemCacheInvalidateLastPurged,itemCacheInvalidateLastPurged)," +
					"plus(s1.queryCacheHits,queryCacheHits)," +
					"plus(s1.queryCacheMisses,queryCacheMisses)," +
					"plus(s1.queryCacheReplacements,queryCacheReplacements)," +
					"plus(s1.queryCacheInvalidations,queryCacheInvalidations)," +
					"plus(s1.changeListenerCleared,changeListenerCleared)," +
					"plus(s1.changeListenerRemoved,changeListenerRemoved)," +
					"plus(s1.changeListenerFailed,changeListenerFailed)," +
					"plus(s1.changeListenerOverflow,changeListenerOverflow)," +
					"plus(s1.changeListenerException,changeListenerException)," +
					"plus(s1.mediasNoSuchPath,mediasNoSuchPath)," +
					"plus(s1.mediasRedirectFrom,mediasRedirectFrom)," +
					"plus(s1.mediasException,mediasException)," +
					"plus(s1.mediasGuessedUrl,mediasGuessedUrl)," +
					"plus(s1.mediasNotAnItem,mediasNotAnItem)," +
					"plus(s1.mediasNoSuchItem,mediasNoSuchItem)," +
					"plus(s1.mediasMoved,mediasMoved)," +
					"plus(s1.mediasIsNull,mediasIsNull)," +
					"plus(s1.mediasNotComputable,mediasNotComputable)," +
					"plus(s1.mediasNotModified,mediasNotModified)," +
					"plus(s1.mediasDelivered,mediasDelivered)," +
					"plus(s1.clusterSenderInvalidationSplit,clusterSenderInvalidationSplit)," +
					"plus(s1.clusterListener-exception,clusterListener-exception)," +
					"plus(s1.clusterListener-missingMagic,clusterListener-missingMagic)," +
					"plus(s1.clusterListener-wrongSecret,clusterListener-wrongSecret)," +
					"plus(s1.clusterListener-fromMyself,clusterListener-fromMyself) " +
				"from SamplerModel join SamplerModel s1 " +
				"where (s1.connectDate=connectDate " +
					"AND s1.sampler=sampler " +
					"AND s1.running=(running+1)) " +
				"order by this", modelQuery.toString());
		assertEquals(
				"select type,date,s1.date," +
					"plus(s1.hits,hits)," +
					"plus(s1.misses,misses)," +
					"plus(s1.concurrentLoads,concurrentLoads)," +
					"plus(s1.replacementRuns,replacementRuns)," +
					"plus(s1.replacements,replacements)," +
					"plus(s1.invalidationsOrdered,invalidationsOrdered)," +
					"plus(s1.invalidationsDone,invalidationsDone)," +
					"plus(s1.invalidateLastHits,invalidateLastHits)," +
					"plus(s1.invalidateLastPurged,invalidateLastPurged) " +
				"from SamplerItemCache join SamplerItemCache s1 " +
				"where (s1.type=type " +
					"AND s1.connectDate=connectDate " +
					"AND s1.sampler=sampler " +
					"AND s1.running=(running+1)) " +
				"order by this",
			itemQuery.toString());
		assertEquals(
				"select id,date,s1.date," +
					"plus(s1.invalidate-inOrder,invalidate-inOrder)," +
					"plus(s1.invalidate-outOfOrder,invalidate-outOfOrder)," +
					"plus(s1.invalidate-duplicate,invalidate-duplicate)," +
					"plus(s1.invalidate-lost,invalidate-lost)," +
					"plus(s1.invalidate-late,invalidate-late)," +
					"plus(s1.invalidate-pending,invalidate-pending)," +
					"plus(s1.ping-inOrder,ping-inOrder)," +
					"plus(s1.ping-outOfOrder,ping-outOfOrder)," +
					"plus(s1.ping-duplicate,ping-duplicate)," +
					"plus(s1.ping-lost,ping-lost)," +
					"plus(s1.ping-late,ping-late)," +
					"plus(s1.ping-pending,ping-pending)," +
					"plus(s1.pong-inOrder,pong-inOrder)," +
					"plus(s1.pong-outOfOrder,pong-outOfOrder)," +
					"plus(s1.pong-duplicate,pong-duplicate)," +
					"plus(s1.pong-lost,pong-lost)," +
					"plus(s1.pong-late,pong-late)," +
					"plus(s1.pong-pending,pong-pending) " +
				"from SamplerClusterNode join SamplerClusterNode s1 " +
				"where (s1.id=id " +
					"AND s1.connectDate=connectDate " +
					"AND s1.sampler=sampler " +
					"AND s1.running=(running+1)) " +
				"order by this",
			clusterQuery.toString());
		assertEquals(
				"select media,date,s1.date," +
					"plus(s1.redirectFrom,redirectFrom)," +
					"plus(s1.exception,exception)," +
					"plus(s1.guessedUrl,guessedUrl)," +
					"plus(s1.notAnItem,notAnItem)," +
					"plus(s1.noSuchItem,noSuchItem)," +
					"plus(s1.moved,moved)," +
					"plus(s1.isNull,isNull)," +
					"plus(s1.notComputable,notComputable)," +
					"plus(s1.notModified,notModified)," +
					"plus(s1.delivered,delivered) " +
				"from SamplerMedia join SamplerMedia s1 " +
				"where (s1.media=media " +
					"AND s1.connectDate=connectDate " +
					"AND s1.sampler=sampler " +
					"AND s1.running=(running+1)) " +
				"order by this",
			mediaQuery.toString());

		samplerModel.startTransaction("SampleTest#consolidate");
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
			assertEquals(list(
					"SampledModelItem",
					SamplerModel.date.get(model1),
					SamplerModel.date.get(model2)),
				items.next().subList(0, 3));
			assertEquals(list(
					"SampledModelItem",
					SamplerModel.date.get(model2),
					SamplerModel.date.get(model3)),
				items.next().subList(0, 3));
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
