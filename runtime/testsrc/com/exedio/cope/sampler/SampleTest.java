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

import static com.exedio.cope.sampler.Stuff.MODEL;
import static com.exedio.cope.sampler.Stuff.sampler;
import static com.exedio.cope.sampler.Stuff.samplerModel;
import static java.util.Arrays.asList;

import com.exedio.cope.Item;
import com.exedio.cope.Query;
import com.exedio.cope.Transaction;
import com.exedio.cope.Type;
import com.exedio.cope.junit.CopeAssert;
import java.util.Date;
import java.util.Iterator;

public class SampleTest extends ConnectedTest
{
	public void testIt()
	{
		samplerModel.createSchema();
		sampler.checkInternal();
		samplerModel.startTransaction("HistoryTest");
		assertEquals(0, SamplerEnvironment.TYPE.search().size());
		assertEquals(0, SamplerModel.TYPE.search().size());
		assertEquals(0, SamplerItemCache.TYPE.search().size());
		assertEquals(0, SamplerMedia.TYPE.search().size());
		samplerModel.commit();
		assertEquals(0, sampler.analyzeCount(SamplerModel.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerMedia.TYPE));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerModel.TYPE)));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerItemCache.TYPE)));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerClusterNode.TYPE)));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerMedia.TYPE)));

		final Date before55 = new Date();
		assertEquals(null, sampler.sampleInternal());
		final Date after55 = new Date();
		samplerModel.startTransaction("HistoryTest2");
		assertEquals(1, SamplerEnvironment.TYPE.search().size());
		{
			final Iterator<SamplerModel> iter = SamplerModel.TYPE.search().iterator();
			assertFalse(iter.hasNext());
		}
		{
			final Iterator<SamplerTransaction> iter = SamplerTransaction.TYPE.search().iterator();
			assertFalse(iter.hasNext());
		}
		{
			final Iterator<SamplerItemCache> iter = SamplerItemCache.TYPE.search().iterator();
			assertFalse(iter.hasNext());
		}
		{
			final Iterator<SamplerMedia> iter = SamplerMedia.TYPE.search().iterator();
			assertFalse(iter.hasNext());
		}
		samplerModel.commit();
		assertEquals(0, sampler.analyzeCount(SamplerModel.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerMedia.TYPE));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerModel.TYPE)));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerItemCache.TYPE)));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerClusterNode.TYPE)));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerMedia.TYPE)));

		waitForSystemTimeChange();
		final Date before66 = new Date();
		final SamplerModel model66 = sampler.sampleInternal();
		final Date after66 = new Date();
		samplerModel.startTransaction("HistoryTest2");
		assertEquals(1, SamplerEnvironment.TYPE.search().size());
		assertWithin(before55, after55, SamplerModel.from.get(model66));
		{
			final Iterator<SamplerModel> iter = iter(SamplerModel.TYPE);
			assertIt(model66, before66, after66, iter.next());
			assertFalse(iter.hasNext());
		}
		final Date date66 = SamplerModel.date.get(model66);
		final SamplerTransaction transaction66;
		{
			final Iterator<SamplerTransaction> iter = SamplerTransaction.TYPE.search().iterator();
			transaction66 = assertIt(model66, iter.next());
			assertFalse(iter.hasNext());
		}
		final SamplerItemCache itemCache66a;
		final SamplerItemCache itemCache66b;
		{
			final Iterator<SamplerItemCache> iter = iter(SamplerItemCache.TYPE);
			if(c)
			{
				itemCache66a = assertIt(model66, "SampledModelItem",  iter.next());
				itemCache66b = assertIt(model66, "SampledModelItem2", iter.next());
			}
			else
			{
				itemCache66a = null;
				itemCache66b = null;
			}
			assertFalse(iter.hasNext());
		}
		final SamplerMedia media66a;
		final SamplerMedia media66b;
		{
			final Iterator<SamplerMedia> iter = iter(SamplerMedia.TYPE);
			media66a = assertIt(model66, "SampledModelItem.mediaA", iter.next());
			media66b = assertIt(model66, "SampledModelItem.mediaB", iter.next());
			assertFalse(iter.hasNext());
		}
		samplerModel.commit();
		assertEquals(1, sampler.analyzeCount(SamplerModel.TYPE));
		assertEquals(c?2:0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(2, sampler.analyzeCount(SamplerMedia.TYPE));
		assertEquals(asList(date66, date66  ), asList(sampler.analyzeDate(SamplerModel.TYPE)));
		assertEquals(c?asList(date66, date66):asList((Date)null, null), asList(sampler.analyzeDate(SamplerItemCache.TYPE)));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerClusterNode.TYPE)));
		assertEquals(asList(date66, date66  ), asList(sampler.analyzeDate(SamplerMedia.TYPE)));

		waitForSystemTimeChange();
		final Date before77 = new Date();
		final SamplerModel model77 = sampler.sampleInternal();
		final Date after77 = new Date();
		samplerModel.startTransaction("HistoryTest2");
		assertEquals(1, SamplerEnvironment.TYPE.search().size());
		assertEquals(date66, SamplerModel.from.get(model77));
		{
			final Iterator<SamplerModel> iter = iter(SamplerModel.TYPE);
			assertEquals(model66, iter.next());
			assertIt(model77, before77, after77, iter.next());
			assertFalse(iter.hasNext());
		}
		final Date date77 = SamplerModel.date.get(model77);
		{
			final Iterator<SamplerTransaction> iter = SamplerTransaction.TYPE.search().iterator();
			assertEquals(transaction66, iter.next());
			assertIt(model77, iter.next());
			assertFalse(iter.hasNext());
		}
		{
			final Iterator<SamplerItemCache> iter = iter(SamplerItemCache.TYPE);
			if(c)
			{
				assertEquals(itemCache66a, iter.next());
				assertEquals(itemCache66b, iter.next());
				assertIt(model77, "SampledModelItem",  iter.next());
				assertIt(model77, "SampledModelItem2", iter.next());
			}
			assertFalse(iter.hasNext());
		}
		{
			final Iterator<SamplerMedia> iter = iter(SamplerMedia.TYPE);
			assertEquals(media66a, iter.next());
			assertEquals(media66b, iter.next());
			assertIt(model77, "SampledModelItem.mediaA", iter.next());
			assertIt(model77, "SampledModelItem.mediaB", iter.next());
			assertFalse(iter.hasNext());
		}
		samplerModel.commit();
		assertEquals(2, sampler.analyzeCount(SamplerModel.TYPE));
		assertEquals(c?4:0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(4, sampler.analyzeCount(SamplerMedia.TYPE));
		assertEquals(asList(date66, date77  ), asList(sampler.analyzeDate(SamplerModel.TYPE)));
		assertEquals(c?asList(date66, date77):asList((Date)null, null), asList(sampler.analyzeDate(SamplerItemCache.TYPE)));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerClusterNode.TYPE)));
		assertEquals(asList(date66, date77  ), asList(sampler.analyzeDate(SamplerMedia.TYPE)));
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

	private static final SamplerModel assertIt(
			final SamplerModel expected,
			final Date before, final Date after,
			final SamplerModel model)
	{
		assertEquals(expected, model);
		assertWithin(before, after, SamplerModel.date.get(model));
		assertEquals(MODEL.getInitializeDate(), SamplerModel.initialized.get(model));
		assertEquals(MODEL.getConnectDate(), SamplerModel.connected.get(model));
		return model;
	}

	private static final SamplerTransaction assertIt(
			final SamplerModel model,
			final SamplerTransaction transaction)
	{
		assertEquals(model, transaction.getModel());
		assertEquals(SamplerModel.date.get(model), transaction.getDate());
		final Transaction tx = MODEL.currentTransaction();
		assertEquals(tx.getID(), transaction.getID());
		assertEquals(tx.getName(), transaction.getName());
		assertEquals(tx.getStartDate(), transaction.getStartDate());
		assertNotNull(transaction.getThread());
		return transaction;
	}

	private static final SamplerItemCache assertIt(
			final SamplerModel model,
			final String id,
			final SamplerItemCache itemCache)
	{
		assertEquals(model, itemCache.getModel());
		assertEquals(id, itemCache.getType());
		assertEquals(SamplerModel.date.get(model), itemCache.getDate());
		return itemCache;
	}

	private static final SamplerMedia assertIt(
			final SamplerModel model,
			final String id,
			final SamplerMedia media)
	{
		assertEquals(model, media.getModel());
		assertEquals(id, media.getMedia());
		assertEquals(SamplerModel.date.get(model), media.getDate());
		return media;
	}

	private static final <E extends Item> Iterator<E> iter(final Type<E> type)
	{
		final Query<E> q = new Query<>(type.getThis());
		q.setOrderBy(type.getThis(), true);
		return q.search().iterator();
	}
}
